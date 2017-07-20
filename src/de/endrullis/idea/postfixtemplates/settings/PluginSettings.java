package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;

public final class PluginSettings {
  public static final PluginSettings DEFAULT = new PluginSettings();

  @Attribute("PluginEnabled")
  private boolean pluginEnabled;
  @Attribute("TemplatesText")
  @NotNull
  private String templatesText;

  private PluginSettings() {
    this(true, "");
  }

	public PluginSettings(boolean pluginEnabled, @NotNull String templatesText) {
		this.pluginEnabled = pluginEnabled;
		this.templatesText = templatesText;
	}

	public boolean isPluginEnabled() {
		return pluginEnabled;
	}

	@NotNull
	public String getTemplatesText() {
		return templatesText;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PluginSettings that = (PluginSettings) o;

		if (pluginEnabled != that.pluginEnabled) return false;
		return templatesText.equals(that.templatesText);
	}

	@Override
	public int hashCode() {
		int result = (pluginEnabled ? 1 : 0);
		result = 31 * result + templatesText.hashCode();
		return result;
	}

	public interface Holder {
    void setPluginSettings(@NotNull PluginSettings settings);

    @NotNull
    PluginSettings getPluginSettings();
  }
}
