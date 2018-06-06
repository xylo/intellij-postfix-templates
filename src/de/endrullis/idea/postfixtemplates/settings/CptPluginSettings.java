package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.utils.Tuple2;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils.$;

@EqualsAndHashCode
@Getter
public final class CptPluginSettings {
	public static final CptPluginSettings DEFAULT = new CptPluginSettings();

	@Attribute("VarLambdaStyle")
	private boolean varLambdaStyle = true;

	@Attribute("UpdateWebTemplatesAutomatically")
	private boolean updateWebTemplatesAutomatically = true;

	@MapAnnotation()
	private Map<String, List<VFile>> langName2virtualFile;

	private transient Map<String, String>                file2langName;
	private transient Map<String, Tuple2<String, VFile>> file2langAndVFile;

	private CptPluginSettings() {
		this(true, true, new HashMap<>());
	}

	public CptPluginSettings(boolean varLambdaStyle, boolean updateWebTemplatesAutomatically, @NotNull Map<String, List<VFile>> langName2virtualFile) {
		this.varLambdaStyle = varLambdaStyle;
		this.updateWebTemplatesAutomatically = updateWebTemplatesAutomatically;
		this.langName2virtualFile = langName2virtualFile;
	}

	void upgrade() {
	}

	public Map<String, List<VFile>> getLangName2virtualFile() {
		return langName2virtualFile.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
			return e.getValue().stream().map(f -> new VFile(f.enabled, f.id, f.url, f.file.replace("${PLUGIN}", CptUtil.getTemplatesPath().getAbsolutePath()))).collect(Collectors.toList());
		}));
	}

	public Map<String, String> getFile2langName() {
		if (file2langName == null) {
			file2langName = new HashMap<>();
			for (Map.Entry<String, List<VFile>> entry : getLangName2virtualFile().entrySet()) {
				for (VFile vFile : entry.getValue()) {
					file2langName.put(vFile.getFile(), entry.getKey());
				}
			}
		}

		return file2langName;
	}

	public Map<String, Tuple2<String, VFile>> getFile2langAndVFile() {
		if (file2langAndVFile == null) {
			file2langAndVFile = new HashMap<>();
			for (Map.Entry<String, List<VFile>> entry : getLangName2virtualFile().entrySet()) {
				for (VFile vFile : entry.getValue()) {
					file2langAndVFile.put(vFile.getFile(), $(entry.getKey(), vFile));
				}
			}
		}

		return file2langAndVFile;
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
		public String id;
		public String url;
		public String file;
	}
}
