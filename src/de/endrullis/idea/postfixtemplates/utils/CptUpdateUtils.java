package de.endrullis.idea.postfixtemplates.utils;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import de.endrullis.idea.postfixtemplates.language.CptIcons;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import de.endrullis.idea.postfixtemplates.settings.CptApplicationSettings;
import de.endrullis.idea.postfixtemplates.settings.CptPluginSettings;
import de.endrullis.idea.postfixtemplates.settings.CptVirtualFile;
import de.endrullis.idea.postfixtemplates.settings.WebTemplateFileLoader;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Date;

import static de.endrullis.idea.postfixtemplates.language.CptUtil.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * CPT update utilities.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@UtilityClass
public class CptUpdateUtils {

	private static final Object updateSync = new Object();

	public static void checkForWebTemplateUpdates(Project project) {
		checkForWebTemplateUpdates(project, false, null);
	}

	public static void checkForWebTemplateUpdates(Project project, boolean forceUpdate, @Nullable Runnable afterUpdateAction) {
		final CptPluginSettings pluginSettings = CptApplicationSettings.getInstance().getPluginSettings();

		if (pluginSettings.isUpdateWebTemplatesAutomatically()) {

			ProgressManager.getInstance().run(new Task.Backgroundable(project, "Updating custom postfix web templates") {
				@Override
				public void run(@NotNull ProgressIndicator progressIndicator) {
					progressIndicator.setIndeterminate(false);
					progressIndicator.setText("Updating local postfix templates...");
					progressIndicator.setFraction(0);

					synchronized (updateSync) {
						for (String language : SupportedLanguages.supportedLanguageIds) {
							// eventually move old templates file to new directory
							moveOldTemplateFile(language);

							// copy local template files
							val vFiles = pluginSettings.getLangName2virtualFiles().get(language);
							if (vFiles != null) {
								for (CptPluginSettings.VFile vFile : vFiles) {
									if (vFile.isLocalTemplateFile()) {
										try (val stream = vFile.getJavaUrl().openStream()) {
											val targetFile = new File(vFile.getFile());
											if (!targetFile.getParentFile().exists()) {
												if (!targetFile.getParentFile().mkdirs()) {
													throw new FailedToCopyLocalTemplatesException("Could not create directory \"" + targetFile.getParent() + "\"");
												}
											}
											if (targetFile.exists() && !targetFile.canWrite()) {
												if (!targetFile.setWritable(true)) {
													throw new FailedToCopyLocalTemplatesException("Cannot make file \"" + targetFile.getAbsoluteFile() + "\" writable.");
												}
											}
											Files.copy(stream, new File(vFile.file).toPath(), REPLACE_EXISTING);
										} catch (IOException e) {
											throw new FailedToCopyLocalTemplatesException(e);
										}
									}
								}
							}
						}

						boolean newTemplateFiles = false;

						// download the web templates file only once a day
						if (forceUpdate || !getWebTemplatesInfoFile().exists() || new Date().getTime() - getWebTemplatesInfoFile().lastModified() > 1000 * 60 * 60 * 24) {
							try {
								progressIndicator.setText("Updating postfix web templates...");
								progressIndicator.setFraction(0.05);

								downloadWebTemplatesInfoFile();

								progressIndicator.setFraction(0.1);

								val webTemplateFiles = WebTemplateFileLoader.load(getWebTemplatesInfoFile());

								for (int i = 0; i < webTemplateFiles.length && !progressIndicator.isCanceled(); i++) {
									val webTemplateFile = webTemplateFiles[i];

									CptVirtualFile cptFile = new CptVirtualFile(
										webTemplateFile.getId(),
										new URI(webTemplateFile.getUrl()).toURL(),
										CptUtil.getTemplateFile(webTemplateFile.getLang(), webTemplateFile.getId()),
										true);

									newTemplateFiles |= CptUtil.downloadWebTemplateFile(cptFile);

									progressIndicator.setFraction(0.10 + 0.90 * (i + 1) / (webTemplateFiles.length));
								}
							} catch (URISyntaxException | IOException e) {
								//noinspection CallToPrintStackTrace
								e.printStackTrace();
								MyNotifier.notificationGroup
									.createNotification("Failed to download postfix web templates. Please check your internet connection.", NotificationType.ERROR)
									.notify(project);
							}
						}

						progressIndicator.setFraction(1.0);
						progressIndicator.setText("Finished");

						if (afterUpdateAction != null) {
							ApplicationManager.getApplication().invokeLater(() -> {
								afterUpdateAction.run();
							});
						} else {
							if (pluginSettings.getSettingsVersion() < 2) {
								openNotification("Custom Postfix Templates 2.0", "Version 2.0 brings you user and web template files.  Please open the settings to configure the plugin.", project);
							} else if (newTemplateFiles) {
								openNotification("Custom Postfix Templates", "New web template files are available.  Please open the settings to activate them.", project);
							}
						}
					}
				}
			});

		}
	}

	private static void openNotification(String title, String content, Project project) {
		Notification notification = MyNotifier.notificationGroup.createNotification(title, content, NotificationType.INFORMATION);
		notification.setIcon(CptIcons.FILE);
		notification.setImportantSuggestion(true);
		notification.addAction(new AnAction("Open Settings") {
			@Override
			public void actionPerformed(@NotNull AnActionEvent e) {
				notification.expire();
				CptUtil.openPluginSettings(project);
			}
		});

		Notifications.Bus.notify(notification, project);
	}

}
