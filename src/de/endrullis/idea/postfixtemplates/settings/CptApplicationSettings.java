package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.util.messages.Topic;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Property;
import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.language.CptUtil.downloadWebTemplateFile;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;

@State(
	name = "CustomPostfixTemplatesApplicationSettings",
	storages = @Storage("customPostfixTemplates.xml")
)
public class CptApplicationSettings implements PersistentStateComponent<CptApplicationSettings.State>, CptPluginSettings.Holder {

	@Getter
	private final State state = new State();

	public CptApplicationSettings() {
	}

	@NotNull
	public static CptApplicationSettings getInstance() {
		return ApplicationManager.getApplication().getService(CptApplicationSettings.class);
	}

	@Override
	public void loadState(@NotNull State state) {
		XmlSerializerUtil.copyBean(state, this.state);
	}

	@Override
	public void setPluginSettings(@NotNull CptPluginSettings settings) {
		val oldVarLambdaStyle = state.pluginSettings.isVarLambdaStyle();

		state.pluginSettings = settings;

		val lambdaStyleChanged = oldVarLambdaStyle != settings.isVarLambdaStyle();

		settingsChanged(lambdaStyleChanged);
	}

	public void setPluginSettingsExternally(@NotNull CptPluginSettings settings) {
		CptPluginSettingsForm.resetLastTreeState();
		setPluginSettings(settings);
	}

	/**
	 * This method is called after the user changed some settings and saved them.
	 *
	 * @param lambdaStyleChanged indicates if lambda style has changed
	 */
	private void settingsChanged(boolean lambdaStyleChanged) {
		// check changes and eventually update file tree
		val lastTreeState = CptPluginSettingsForm.getLastTreeState();

		if (lastTreeState != null) {
			List<File> changedFiles = new ArrayList<>();

			for (CptLang cptLang : SupportedLanguages.supportedLanguages) {
				val cptVirtualFiles = lastTreeState.getOrDefault(cptLang, _List());

				for (CptVirtualFile cptVirtualFile : cptVirtualFiles) {
					try {
						boolean needsUpdate = false;

						if (cptVirtualFile.getFile() != null) {
							createParent(cptVirtualFile.getFile());
						}

						if (cptVirtualFile.isNew() || !cptVirtualFile.getFile().exists() || lambdaStyleChanged) {
							//noinspection ResultOfMethodCallIgnored
							cptVirtualFile.getFile().createNewFile();

							needsUpdate = cptVirtualFile.getUrl() != null;
						}
						if (cptVirtualFile.fileHasChanged()) {
							cptVirtualFile.getOldFile().renameTo(cptVirtualFile.getFile());
						}
						if (cptVirtualFile.urlHasChanged()) {
							needsUpdate = true;
						}

						if (needsUpdate) {
							downloadWebTemplateFile(cptVirtualFile);
							changedFiles.add(cptVirtualFile.getFile());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				val filesInTree = cptVirtualFiles.stream().map(f -> f.getFile().getAbsolutePath()).collect(Collectors.toSet());

				// delete the files that has been removed from the tree
				Arrays.stream(CptUtil.getTemplateFilesFromLanguageDir(cptLang.getLanguage()))
					.filter(f -> !filesInTree.contains(f.getAbsolutePath()))
					.forEach(f -> f.delete());
			}

			LocalFileSystem.getInstance().refreshIoFiles(changedFiles);
		}

		ApplicationManager.getApplication().getMessageBus().syncPublisher(SettingsChangedListener.TOPIC).onSettingsChange(this);
	}

	private void createParent(File file) {
		file.getParentFile().mkdirs();
	}

	@NotNull
	@Override
	public CptPluginSettings getPluginSettings() {
		state.pluginSettings.upgrade();
		return state.pluginSettings;
	}


	public static class State {
		@Property(surroundWithTag = false)
		@NotNull
		private CptPluginSettings pluginSettings = CptPluginSettings.DEFAULT;
	}

	public interface SettingsChangedListener {
		Topic<SettingsChangedListener> TOPIC = Topic.create("CustomPostfixTemplatesApplicationSettingsChanged", SettingsChangedListener.class);

		void reloadTemplates();

		void onSettingsChange(@NotNull CptApplicationSettings settings);
	}
}
