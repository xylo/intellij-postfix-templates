package de.endrullis.idea.postfixtemplates.language;

import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import de.endrullis.idea.postfixtemplates.language.psi.CptFile;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.language.psi.CptTemplate;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import de.endrullis.idea.postfixtemplates.settings.*;
import de.endrullis.idea.postfixtemplates.utils.Tuple2;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;
import static de.endrullis.idea.postfixtemplates.utils.StringUtils.replace;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@SuppressWarnings("WeakerAccess")
public class CptUtil {
	public static final String PLUGIN_ID = "de.endrullis.idea.postfixtemplates";

	private static String templatesPathString = null;

	public static Project findProject(PsiElement element) {
		PsiFile containingFile = element.getContainingFile();
		if (containingFile == null) {
			if (!element.isValid()) {
				return null;
			}
		} else if (!containingFile.isValid()) {
			return null;
		}

		return (containingFile == null ? element : containingFile).getProject();
	}

	public static List<CptMapping> findMappings(Project project, String key) {
		List<CptMapping> result = null;

		val virtualFiles = FileTypeIndex.getFiles(CptFileType.INSTANCE, GlobalSearchScope.allScope(project));

		/*
		Collection<VirtualFile> virtualFiles =
			FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CptFileType.INSTANCE,
				GlobalSearchScope.allScope(project));
		*/

		for (VirtualFile virtualFile : virtualFiles) {
			CptFile cptFile = (CptFile) PsiManager.getInstance(project).findFile(virtualFile);

			if (cptFile != null) {
				CptMapping[] mappings = PsiTreeUtil.getChildrenOfType(cptFile, CptMapping.class);
				if (mappings != null) {
					for (CptMapping mapping : mappings) {
						if (key.equals(mapping.getMatchingClassName())) {
							if (result == null) {
								result = new ArrayList<>();
							}
							result.add(mapping);
						}
					}
				}
			}
		}
		return result != null ? result : Collections.emptyList();
	}

	public static List<CptMapping> findMappings(Project project) {
		List<CptMapping> result = new ArrayList<>();

		val virtualFiles = FileTypeIndex.getFiles(CptFileType.INSTANCE, GlobalSearchScope.allScope(project));

		/*
		Collection<VirtualFile> virtualFiles =
			FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CptFileType.INSTANCE,
				GlobalSearchScope.allScope(project));
		*/

		for (VirtualFile virtualFile : virtualFiles) {
			CptFile cptFile = (CptFile) PsiManager.getInstance(project).findFile(virtualFile);
			if (cptFile != null) {
				CptMapping[] mappings = PsiTreeUtil.getChildrenOfType(cptFile, CptMapping.class);
				if (mappings != null) {
					Collections.addAll(result, mappings);
				}
			}
		}
		return result;
	}

	/**
	 * Returns the predefined plugin templates for the given language.
	 *
	 * @param language programming language
	 * @return predefined plugin templates for the given language
	 */
	public static String getDefaultTemplates(String language) {
		InputStream stream = CptUtil.class.getResourceAsStream("defaulttemplates/" + language + ".postfixTemplates");
		try {
			return getContent(stream);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Returns the content of the given file.
	 *
	 * @param file file
	 * @return content of the given file
	 * @throws FileNotFoundException
	 */
	public static String getContent(@NotNull File file) throws FileNotFoundException {
		try {
			return getContent(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Returns the content of the given input stream.
	 *
	 * @param stream input stream
	 * @return content of the given input stream
	 */
	private static String getContent(@NotNull InputStream stream) throws IOException {
		StringBuilder sb = new StringBuilder();

		// convert system newlines into UNIX newlines, because IDEA works only with UNIX newlines
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line).append("\n");
			}

			return sb.toString();
		}
	}

	/**
	 * Returns the path of the CPT plugin settings directory.
	 *
	 * @return path of the CPT plugin settings directory
	 */
	public static File getPluginPath() {
		File path = PluginManager.getPlugin(PluginId.getId(CptUtil.PLUGIN_ID)).getPath();

		if (path.getName().endsWith(".jar")) {
			path = new File(path.getParentFile(), path.getName().substring(0, path.getName().length() - 4));
		}

		return new File(path.getParentFile(), path.getName() + "_templates");
	}

	/**
	 * Returns the path "$CPT_PLUGIN_SETTINGS/templates".
	 *
	 * @return path "$CPT_PLUGIN_SETTINGS/templates"
	 */
	public static File getTemplatesPath() {
		final File templates = new File(getPluginPath(), "templates");
		templates.mkdirs();

		return templates;
	}

	public static String getTemplatesPathString() {
		if (templatesPathString != null) {
			templatesPathString = getTemplatesPath().getAbsolutePath().replace('\\', '/');
		}

		return templatesPathString;
	}

	public static void createTemplateFile(@NotNull String language, @NotNull String filename, @NotNull String content) {
		File file = new File(CptUtil.getTemplatesPath() + "/" + language, filename + ".postfixTemplates");

		if (file.exists()) return;

		//noinspection ResultOfMethodCallIgnored
		file.getParentFile().mkdirs();

		try {
			FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Template file " + file.getAbsolutePath() + " could not be written", e);
		}
	}

	public static void moveOldTemplateFile(@NotNull String language) {
		if (SupportedLanguages.supportedLanguageIds.contains(language.toLowerCase())) {
			File file = new File(CptUtil.getTemplatesPath(), language + ".postfixTemplates");

			if (file.exists()) {
				// move file to new position
				val newFile = getTemplateFile(language, "oldUserTemplates");
				
				file.renameTo(newFile);

				LocalFileSystem.getInstance().refreshIoFiles(_List(newFile));
			}
		}
	}

	public static File getTemplateFile(@NotNull String language, @NotNull String fileName) {
		val path = getTemplateDir(language);

		return new File(path, fileName + ".postfixTemplates");
	}

	@NotNull
	public static File getTemplateDir(@NotNull String language) {
		final File dir = new File(getTemplatesPath(), language);

		if (!dir.exists()) {
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
		}

		return dir;
	}

	@NotNull
	public static File getWebTemplatesInfoFile() {
		return new File(getTemplatesPath(), "webTemplateFiles.yaml");
	}

	public static List<File> getTemplateFiles(@NotNull String language) {
		return getTemplateFiles(language, f -> f.enabled);
	}

	public static List<File> getEditableTemplateFiles(@NotNull String language) {
		return getTemplateFiles(language, f -> f.enabled && f.url == null);
	}

	public static List<File> getTemplateFiles(@NotNull String language, Predicate<CptPluginSettings.VFile> fileFilter) {
		if (SupportedLanguages.supportedLanguageIds.contains(language.toLowerCase())) {
			// eventually move old templates file to new directory
			moveOldTemplateFile(language);

			val filesFromDir = getTemplateFilesFromLanguageDir(language);

			val settings = CptApplicationSettings.getInstance().getPluginSettings();

			val vFiles = settings.getLangName2virtualFiles().getOrDefault(language, new ArrayList<>());
			val allFilesFromConfig = vFiles.stream().map(f -> CptUtil.fixFilePath(f.file)).collect(Collectors.toSet());
			val enabledFilesFromConfig = vFiles.stream().filter(fileFilter).map(f -> new File(f.file)).filter(f -> f.exists()).collect(Collectors.toList());

			val remainingTemplateFilesFromDir = Arrays.stream(filesFromDir).filter(f -> !allFilesFromConfig.contains(CptUtil.fixFilePath(f.getAbsolutePath())));

			// templateFilesFromConfig + remainingTemplateFilesFromDir
			return Stream.concat(remainingTemplateFilesFromDir, enabledFilesFromConfig.stream()).collect(Collectors.toList());
		} else {
			return _List();
		}
	}

	@NotNull
	public static File[] getTemplateFilesFromLanguageDir(@NotNull String language) {
		final File[] files = getTemplateDir(language).listFiles(f -> f.getName().endsWith(".postfixTemplates"));

		if (files == null) {
			return new File[0];
		}

		return files;
	}

	@Nullable
	public static CptLang getLanguageOfTemplateFile(@NotNull VirtualFile vFile) {
		val name = vFile.getNameWithoutExtension();

		return Optional.ofNullable(
			SupportedLanguages.getCptLang(name)
		).orElseGet(() -> {
			val parent = getAbsoluteVirtualFile(vFile).getParent();
			if (parent != null) {
				return SupportedLanguages.getCptLang(parent.getName());
			} else {
				return null;
			}
		});
	}

	@NotNull
	public static String getPath(@NotNull VirtualFile vFile) {
		return fixFilePath(getAbsoluteVirtualFile(vFile).getPath());
	}

	@NotNull
	public static String fixFilePath(@NotNull String filePath) {
		return filePath.replace('\\', '/');
	}

	@NotNull
	public static VirtualFile getAbsoluteVirtualFile(@NotNull VirtualFile vFile) {
		if (vFile instanceof LightVirtualFile) {
			final VirtualFile originalFile = ((LightVirtualFile) vFile).getOriginalFile();

			if (originalFile != null) {
				vFile = originalFile;
			}
		}

		return vFile;
	}

	public static void openFileInEditor(@NotNull Project project, @NotNull File file) {
		VirtualFile vFile = LocalFileSystem.getInstance().findFileByIoFile(file);

		if (vFile == null) {
			LocalFileSystem.getInstance().refreshIoFiles(_List(file));
			vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
		}

		assert vFile != null;
		
		openFileInEditor(project, vFile);
	}

	public static void openFileInEditor(@NotNull Project project, @NotNull VirtualFile vFile) {
		// open templates file in an editor
		final OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, vFile);
		openFileDescriptor.navigate(true);

		//EditorFactory.getInstance().createViewer(document, project);
	}

	@Deprecated
	public static Project getActiveProject() {
		DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
		return CommonDataKeys.PROJECT.getData(dataContext);
	}

	public static void openPluginSettings(Project project) {
		ShowSettingsUtil.getInstance().showSettingsDialog(project, CptPluginConfigurable.class);
	}

	public static boolean isTemplateFile(@NotNull VirtualFile vFile, @NotNull String language) {
		val settings = CptApplicationSettings.getInstance().getPluginSettings();

		val path = getPath(vFile);
		val lang = settings.getFile2langName().get(path);

		return lang != null && lang.equals(language);
	}

	public static boolean isActiveTemplateFile(@NotNull VirtualFile vFile) {
		val path = getPath(vFile);
		return path.startsWith(getTemplatesPathString());
	}

	@Nullable
	public static Tuple2<String, CptPluginSettings.VFile> getLangAndVFile(@NotNull VirtualFile vFile) {
		val settings = CptApplicationSettings.getInstance().getPluginSettings();

		val path = getPath(vFile);

		return settings.getFile2langAndVFile().get(path);
	}

	public static boolean isUserTemplateFile(@NotNull VirtualFile vFile) {
		final Tuple2<String, CptPluginSettings.VFile> langAndVFile = getLangAndVFile(vFile);

		if (langAndVFile == null) {
			return false;
		}

		return langAndVFile._2.isUserTemplateFile();
	}

	public static void processTemplates(Project project, VirtualFile vFile, BiConsumer<CptTemplate, CptMapping> action) {
		CptFile cptFile = (CptFile) PsiManager.getInstance(project).findFile(vFile);
		if (cptFile != null) {
			CptTemplate[] cptTemplates = PsiTreeUtil.getChildrenOfType(cptFile, CptTemplate.class);
			if (cptTemplates != null) {
				for (CptTemplate cptTemplate : cptTemplates) {
					for (CptMapping mapping : cptTemplate.getMappings().getMappingList()) {
						action.accept(cptTemplate, mapping);
					}
				}
			}
		}
	}

	private static String applyReplacements(String templatesText, boolean preFilled) {
		final String[] finalTemplatesText = new String[]{templatesText};

		new BufferedReader(new InputStreamReader(
			CptUtil.class.getResourceAsStream("templatemapping/" + (preFilled ? "var" : "empty") + "Lambda.txt"), StandardCharsets.UTF_8
		)).lines().filter(l -> l.contains("→")).forEach(line -> {
			String[] split = line.split("→");
			finalTemplatesText[0] = replace(finalTemplatesText[0], split[0].trim(), split[1].trim());
		});

		return finalTemplatesText[0];
	}

	/** Downloads/updates the web template info file. */
	public static void downloadWebTemplatesInfoFile() throws IOException {
		URL url = new URL("https://raw.githubusercontent.com/xylo/intellij-postfix-templates/master/templates/webTemplateFiles.yaml");

		val tmpFile = File.createTempFile("idea.cpt.webtemplates", null);
		val content = getContent(url.openStream());

		FileUtils.writeStringToFile(tmpFile, content, StandardCharsets.UTF_8);

		Files.move(tmpFile.toPath(), getWebTemplatesInfoFile().toPath(), REPLACE_EXISTING);
	}

	/**
	 * Downloads/updates the given web template file.
	 *
	 * @return true if the file is new
	 */
	public static boolean downloadWebTemplateFile(CptVirtualFile cptVirtualFile) throws IOException {
		val preFilled = CptApplicationSettings.getInstance().getPluginSettings().isVarLambdaStyle();

		val tmpFile = File.createTempFile("idea.cpt." + cptVirtualFile.getName(), null);
		val content = applyReplacements(getContent(cptVirtualFile.getUrl().openStream()), preFilled);

		FileUtils.writeStringToFile(tmpFile, content, StandardCharsets.UTF_8);

		//Arrays.equals(Files.readAllBytes(tmpFile.toPath()), Files.readAllBytes(cptVirtualFile.getFile().toPath()));
		boolean isNew = !cptVirtualFile.getFile().exists();
		if (cptVirtualFile.getFile().exists()) {
			cptVirtualFile.getFile().setWritable(true);
		}
		Files.move(tmpFile.toPath(), cptVirtualFile.getFile().toPath(), REPLACE_EXISTING);

		if (cptVirtualFile.getId() != null) {
			cptVirtualFile.getFile().setReadOnly();
		}

		return isNew;
	}

	/**
	 * Downloads the web templates info file if the last update was more than 1 day ago, and
	 * returns the web template files listed in the info file.
	 *
	 * @return returns the web template files listed in the info file
	 */
	public static WebTemplateFile[] loadWebTemplateFiles() {
		// download the web templates file only once a day
		if (!getWebTemplatesInfoFile().exists() || new Date().getTime() - getWebTemplatesInfoFile().lastModified() > 1000*60*60*24) {
			try {
				downloadWebTemplatesInfoFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			return WebTemplateFileLoader.load(getWebTemplatesInfoFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	public static VirtualFile getVirtualFile(@NotNull PsiElement element) {
		return element.getContainingFile().getViewProvider().getVirtualFile();
	}

	public static void createTopPrioUserTemplateFile(String language, String fileName) {
		createTemplateFile(language, fileName, getDefaultTemplates("example"));
		val templateFile = getTemplateFile(language, fileName);

		val pluginSettings = CptApplicationSettings.getInstance().getPluginSettings();

		val langName2virtualFiles = pluginSettings.getLangName2virtualFiles();
		val vFiles = langName2virtualFiles.computeIfAbsent(language, l -> new ArrayList<>());
		vFiles.add(0, new CptPluginSettings.VFile(true, null, null, templateFile.getAbsolutePath()));

		val newLangName2virtualFiles = langName2virtualFiles.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
			return e.getValue().stream().map(f -> new CptPluginSettings.VFile(f.enabled, f.id, f.url, f.file.replace(CptUtil.getTemplatesPath().getAbsolutePath(), "${PLUGIN}"))).collect(Collectors.toList());
		}));

		CptPluginSettings newPluginSettings = new CptPluginSettings(
			pluginSettings.isVarLambdaStyle(),
			pluginSettings.isUpdateWebTemplatesAutomatically(),
			pluginSettings.isActivateNewWebTemplateFilesAutomatically(),
			pluginSettings.getSettingsVersion(),
			newLangName2virtualFiles
		);

		CptApplicationSettings.getInstance().setPluginSettingsExternally(newPluginSettings);
	}

}
