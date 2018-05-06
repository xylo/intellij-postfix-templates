package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.messages.Topic;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Property;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@State(
	name = "CustomPostfixTemplatesApplicationSettings",
	storages = @Storage("customPostfixTemplates.xml")
)
public class CptApplicationSettings implements PersistentStateComponent<CptApplicationSettings.State>, CptPluginSettings.Holder {

	@Nullable
	@Getter
	private State state = new State();

	public CptApplicationSettings() {
	}

	@NotNull
	public static CptApplicationSettings getInstance() {
		return ServiceManager.getService(CptApplicationSettings.class);
	}

	@Override
	public void loadState(State state) {
		XmlSerializerUtil.copyBean(state, this.state);
	}

	@Override
	public void setPluginSettings(@NotNull CptPluginSettings settings) {
		state.pluginSettings = settings;

		settingsChanged();
	}

	private void settingsChanged() {
		// check changes and eventually update file tree
		val lastTreeState = CptPluginSettingsForm.getLastTreeState();

		if (lastTreeState != null) {
			for (List<CptVirtualFile> cptVirtualFiles : lastTreeState.values()) {
				for (CptVirtualFile cptVirtualFile : cptVirtualFiles) {
					try {
						boolean needsUpdate = false;

						if (cptVirtualFile.getFile() != null) {
							createParent(cptVirtualFile.getFile());
						}

						if (cptVirtualFile.isNew()) {
							cptVirtualFile.getFile().createNewFile();

							needsUpdate = cptVirtualFile.getUrl() != null;
						}
						if (cptVirtualFile.fileHashChanged()) {
							cptVirtualFile.getOldFile().renameTo(cptVirtualFile.getFile());
						}
						if (cptVirtualFile.urlHashChanged()) {
							needsUpdate = true;
						}

						if (needsUpdate) {
							val content = new Scanner(cptVirtualFile.getUrl().openStream(), "UTF-8").useDelimiter("\\A").next();
							try (BufferedWriter writer = new BufferedWriter(new FileWriter(cptVirtualFile.getFile()))) {
								writer.write(content);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
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
