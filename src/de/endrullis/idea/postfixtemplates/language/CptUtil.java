package de.endrullis.idea.postfixtemplates.language;

import com.intellij.ide.plugins.PluginManager;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
		return new Scanner(CptUtil.class.getResourceAsStream("defaulttemplates/java.postfixTemplates"), "UTF-8")
			.useDelimiter("\\A").next();
	}

	public static File getPluginPath() {
		return PluginManager.getPlugin(PluginId.getId(CptUtil.PLUGIN_ID)).getPath();
	}

	public static File getTemplatesPath() {
		return new File(getPluginPath(), "templates");
	}

	public static Optional<File> getTemplateFile(String language) {
		if (SUPPORTED_LANGUAGES.contains(language.toLowerCase())) {
			File file = new File(CptUtil.getTemplatesPath(), language + ".postfixTemplates");

			//noinspection ResultOfMethodCallIgnored
			file.getParentFile().mkdirs();

			if (!file.exists()) {
				try (PrintStream out = new PrintStream(file)) {
					out.println(CptUtil.getDefaultJavaTemplates());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return Optional.empty();
				}
			}

			return Optional.of(file);
		} else {
			return Optional.empty();
		}
	}

	public static void openFileInEditor(Project project, File file) {
		VirtualFile vFile = LocalFileSystem.getInstance().findFileByIoFile(file);

		// open templates file in an editor
		new OpenFileDescriptor(project, vFile).navigate(true);
	}
}
