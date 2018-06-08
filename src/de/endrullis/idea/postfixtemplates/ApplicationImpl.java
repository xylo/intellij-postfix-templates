package de.endrullis.idea.postfixtemplates;

import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import de.endrullis.idea.postfixtemplates.actions.EditorTypedHandler;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.settings.CptApplicationSettings;
import de.endrullis.idea.postfixtemplates.settings.CptVirtualFile;
import de.endrullis.idea.postfixtemplates.settings.WebTemplateFileLoader;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import static de.endrullis.idea.postfixtemplates.language.CptUtil.downloadWebTemplatesInfoFile;
import static de.endrullis.idea.postfixtemplates.language.CptUtil.getWebTemplatesInfoFile;

/**
 * Plugin initialization.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class ApplicationImpl implements ApplicationInterface {

	public ApplicationImpl() {
		setupEditorTypedHandler();
		checkForWebTemplateUpdates();
	}

	private void setupEditorTypedHandler() {
		val actionManager = EditorActionManager.getInstance();
		val typedAction = actionManager.getTypedAction();
		val oldHandler = typedAction.getHandler();
		typedAction.setupHandler(new EditorTypedHandler(oldHandler));
	}

	private void checkForWebTemplateUpdates() {
		Project project = CptUtil.getActiveProject();

		if (CptApplicationSettings.getInstance().getPluginSettings().isUpdateWebTemplatesAutomatically()) {

			ProgressManager.getInstance().run(new Task.Backgroundable(project, "Updating Custom Postfix Web Templates") {
				public void run(@NotNull ProgressIndicator progressIndicator) {
					// download the web templates file only once a day
					if (!getWebTemplatesInfoFile().exists() || new Date().getTime() - getWebTemplatesInfoFile().lastModified() > 1000*60*60*24) {
						try {
							downloadWebTemplatesInfoFile();

							progressIndicator.setFraction(0.10);
							//progressIndicator.setText("90% to finish");

							val webTemplateFiles = WebTemplateFileLoader.load(getWebTemplatesInfoFile());

							for (int i = 0; i < webTemplateFiles.length; i++) {
								val webTemplateFile = webTemplateFiles[i];

								CptVirtualFile cptFile = new CptVirtualFile(
									webTemplateFile.getId(),
									new URL(webTemplateFile.getUrl()),
									CptUtil.getTemplateFile(webTemplateFile.getLang(), webTemplateFile.getId()),
									true);

								CptUtil.downloadWebTemplateFile(cptFile);

								progressIndicator.setFraction(0.10 + 0.90 * (i+1));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					progressIndicator.setFraction(1.0);
					progressIndicator.setText("finished");
				}
			});
			
		}
	}

}
