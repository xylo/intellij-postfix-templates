package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
@Getter
public final class CptPluginSettings {
	public static final CptPluginSettings DEFAULT = new CptPluginSettings();

	@Attribute("VarLambdaStyle")
	private boolean varLambdaStyle;

	@MapAnnotation()
	private Map<String, List<VFile>> langName2virtualFile;

	@Attribute("TemplateSuffix")
	@NotNull
	private String templateSuffix;

	@Attribute("TemplateSuffixVersion")
	private int templateSuffixVersion = 0;

	private CptPluginSettings() {
		this(true, new HashMap<>(), "");
	}

	public CptPluginSettings(boolean varLambdaStyle, @NotNull Map<String, List<VFile>> langName2virtualFile, @NotNull String templateSuffix) {
		this.varLambdaStyle = varLambdaStyle;
		this.langName2virtualFile = langName2virtualFile;
		this.templateSuffix = templateSuffix;
		this.templateSuffixVersion = 1;
	}

	void upgrade() {
		if (templateSuffixVersion == 0 && templateSuffix.equals("→")) {
			templateSuffix = "";
		}
		templateSuffixVersion = 1;
	}

	public interface Holder {
		void setPluginSettings(@NotNull CptPluginSettings settings);

		@NotNull
		CptPluginSettings getPluginSettings();
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class VFile {
		public boolean enabled;
		public String url;
		public String file;
	}
}
