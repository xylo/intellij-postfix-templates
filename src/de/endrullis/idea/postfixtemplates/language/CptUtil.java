package de.endrullis.idea.postfixtemplates.language;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import de.endrullis.idea.postfixtemplates.language.psi.*;

import java.util.*;

public class CptUtil {
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
}