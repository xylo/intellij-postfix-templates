package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import org.jetbrains.annotations.NotNull;

public final class CptPluginSettings {
  public static final CptPluginSettings DEFAULT = new CptPluginSettings();

  @Attribute("PluginEnabled")
  private boolean pluginEnabled;
  @Attribute("TemplatesText")
  @NotNull
  private String templatesText;

  private CptPluginSettings() {
    this(true, CptUtil.getDefaultJavaTemplates());
  }

	public CptPluginSettings(boolean pluginEnabled, @NotNull String templatesText) {
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

		CptPluginSettings that = (CptPluginSettings) o;

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
    void setPluginSettings(@NotNull CptPluginSettings settings);

    @NotNull
    CptPluginSettings getPluginSettings();
  }
}
