package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.utils.Tuple2;
import lombok.*;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
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
	private final boolean varLambdaStyle;

	@Attribute("UpdateWebTemplatesAutomatically")
	private final boolean updateWebTemplatesAutomatically;

	@Attribute("ActivateNewWebTemplateFilesAutomatically")
	private final boolean activateNewWebTemplateFilesAutomatically;

	@Attribute("SettingsVersion")
	private final int settingsVersion;

	@MapAnnotation()
	private final Map<String, List<VFile>> langName2virtualFiles;

	private transient Map<String, String>                file2langName;
	private transient Map<String, Tuple2<String, VFile>> file2langAndVFile;

	private CptPluginSettings() {
		this(true, true, true, 1, new HashMap<>());
	}

	public CptPluginSettings(boolean varLambdaStyle,
	                         boolean updateWebTemplatesAutomatically,
	                         boolean activateNewWebTemplateFilesAutomatically,
	                         int settingsVersion,
	                         @NotNull Map<String, List<VFile>> langName2virtualFiles) {
		
		this.varLambdaStyle = varLambdaStyle;
		this.updateWebTemplatesAutomatically = updateWebTemplatesAutomatically;
		this.activateNewWebTemplateFilesAutomatically = activateNewWebTemplateFilesAutomatically;
		this.settingsVersion = settingsVersion;
		this.langName2virtualFiles = langName2virtualFiles;
	}

	void upgrade() {
	}

	public Map<String, List<VFile>> getLangName2virtualFiles() {
		return langName2virtualFiles.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
			return e.getValue().stream().map(f -> new VFile(f.enabled, f.id, f.url, f.file.replace("${PLUGIN}", CptUtil.getTemplatesPath().getAbsolutePath()))).collect(Collectors.toList());
		}));
	}

	public Map<String, String> getFile2langName() {
		if (file2langName == null) {
			file2langName = new HashMap<>();
			for (Map.Entry<String, List<VFile>> entry : getLangName2virtualFiles().entrySet()) {
				for (VFile vFile : entry.getValue()) {
					file2langName.put(CptUtil.fixFilePath(vFile.getFile()), entry.getKey());
				}
			}
		}

		return file2langName;
	}

	public Map<String, Tuple2<String, VFile>> getFile2langAndVFile() {
		if (file2langAndVFile == null) {
			file2langAndVFile = new HashMap<>();
			for (Map.Entry<String, List<VFile>> entry : getLangName2virtualFiles().entrySet()) {
				for (VFile vFile : entry.getValue()) {
					file2langAndVFile.put(CptUtil.fixFilePath(vFile.getFile()), $(entry.getKey(), vFile));
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

		public boolean isUserTemplateFile() {
			return url == null;
		}

		public boolean isLocalTemplateFile() {
			return url != null && url.startsWith("file:");
		}

		public URL getJavaUrl() throws MalformedURLException {
			return SystemUtils.IS_OS_WINDOWS
				? new URL(url.replaceFirst("file://", "file:///"))
				: new URL(url);
		}
	}
}
