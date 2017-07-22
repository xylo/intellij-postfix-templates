package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;

public final class CptPluginSettings {
	public static final CptPluginSettings DEFAULT = new CptPluginSettings();

	@Attribute("PluginEnabled")
	private boolean pluginEnabled;

	private CptPluginSettings() {
		this(true);
	}

	public CptPluginSettings(boolean pluginEnabled) {
		this.pluginEnabled = pluginEnabled;
	}

	public boolean isPluginEnabled() {
		return pluginEnabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CptPluginSettings that = (CptPluginSettings) o;

		return pluginEnabled == that.pluginEnabled;
	}

	@Override
	public int hashCode() {
		return pluginEnabled ? 1 : 0;
	}

	public interface Holder {
		void setPluginSettings(@NotNull CptPluginSettings settings);

		@NotNull
		CptPluginSettings getPluginSettings();
	}
}
