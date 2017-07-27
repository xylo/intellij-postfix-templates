package de.endrullis.idea.postfixtemplates.language;

import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import de.endrullis.idea.postfixtemplates.language.psi.CptFile;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class CptUtil {
	public static final String PLUGIN_ID = "de.endrullis.idea.postfixtemplates";
	public static final Set<String> SUPPORTED_LANGUAGES = new HashSet<>(Arrays.asList("java"));

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

		Collection<VirtualFile> virtualFiles =
			FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CptFileType.INSTANCE,
				GlobalSearchScope.allScope(project));

		for (VirtualFile virtualFile : virtualFiles) {
			CptFile cptFile = (CptFile) PsiManager.getInstance(project).findFile(virtualFile);

			if (cptFile != null) {
				CptMapping[] mappings = PsiTreeUtil.getChildrenOfType(cptFile, CptMapping.class);
				if (mappings != null) {
					for (CptMapping mapping : mappings) {
						if (key.equals(mapping.getClassName())) {
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
		Collection<VirtualFile> virtualFiles =
			FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CptFileType.INSTANCE,
				GlobalSearchScope.allScope(project));
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

	public static String getDefaultJavaTemplates() {
		InputStream stream = CptUtil.class.getResourceAsStream("defaulttemplates/java.postfixTemplates");
		return getContent(stream);
	}

	public static String getContent(@NotNull File file) throws FileNotFoundException {
		return getContent(new FileInputStream(file));
	}

	private static String getContent(@NotNull InputStream stream) {
		StringBuilder sb = new StringBuilder();

		// convert system newlines into UNIX newlines, because IDEA works only with UNIX newlines
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line).append("\n");
			}

			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static File getPluginPath() {
		File path = PluginManager.getPlugin(PluginId.getId(CptUtil.PLUGIN_ID)).getPath();

		if (path.getName().endsWith(".jar")) {
			path = new File(path.getParentFile(), path.getName().substring(0, path.getName().length() - 4));
		}

		return path;
	}

	public static File getTemplatesPath() {
		return new File(getPluginPath(), "templates");
	}

	public static Optional<File> getTemplateFile(@NotNull String language) {
		if (SUPPORTED_LANGUAGES.contains(language.toLowerCase())) {
			File file = new File(CptUtil.getTemplatesPath(), language + ".postfixTemplates");

			//noinspection ResultOfMethodCallIgnored
			file.getParentFile().mkdirs();

			if (!file.exists()) {
				try (PrintStream out = new PrintStream(file, "UTF-8")) {
					out.println(CptUtil.getDefaultJavaTemplates());
					out.close();
				} catch (FileNotFoundException e) {
					throw new RuntimeException(language + " template file could not copied to " + file.getAbsolutePath(), e);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("UTF-8 not supported", e);
				}
			}

			return Optional.of(file);
		} else {
			return Optional.empty();
		}
	}

	public static void openFileInEditor(Project project, File file) {
		VirtualFile vFile = LocalFileSystem.getInstance().findFileByIoFile(file);

		if (vFile == null) {
			vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
		}

		// open templates file in an editor
		new OpenFileDescriptor(project, vFile).navigate(true);
	}

	public static Project getActiveProject() {
		DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
		return DataKeys.PROJECT.getData(dataContext);
	}
}
