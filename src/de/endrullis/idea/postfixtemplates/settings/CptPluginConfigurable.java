package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import de.endrullis.idea.postfixtemplates.bundle.PostfixTemplatesBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CptPluginConfigurable implements SearchableConfigurable, Configurable.NoScroll {
	@Nullable
	private CptPluginSettingsForm form = null;
	@NotNull
	private CptApplicationSettings pluginApplicationSettings;

	public CptPluginConfigurable(@NotNull CptApplicationSettings pluginApplicationSettings) {
		this.pluginApplicationSettings = pluginApplicationSettings;
	}

	@NotNull
	@Override
	public String getId() {
		return "Settings.CustomPostfixTemplates";
	}

	@Nullable
	@Override
	public Runnable enableSearch(String s) {
		return null;
	}

	@Nls
	@Override
	public String getDisplayName() {
		return PostfixTemplatesBundle.message("settings.plugin.name");
	}

	@Nullable
	@Override
	public String getHelpTopic() {
		return null;
	}

	@NotNull
	@Override
	public JComponent createComponent() {
		return getForm().getComponent();
	}

	@NotNull
	public CptPluginSettingsForm getForm() {
		if (form == null) {
			form = new CptPluginSettingsForm();
		}
		return form;
	}

	@Override
	public boolean isModified() {
		return !getForm().getPluginSettings().equals(pluginApplicationSettings.getPluginSettings());
	}

	@Override
	public void apply() throws ConfigurationException {
		pluginApplicationSettings.setPluginSettings(getForm().getPluginSettings());
	}

	@Override
	public void reset() {
		getForm().setPluginSettings(pluginApplicationSettings.getPluginSettings());
	}

	@Override
	public void disposeUIResources() {
		if (form != null) {
			Disposer.dispose(form);
		}
		form = null;
	}

}
