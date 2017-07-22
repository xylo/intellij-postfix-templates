package de.endrullis.idea.postfixtemplates.language.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import de.endrullis.idea.postfixtemplates.language.CptFileType;

public class CptElementFactory {
	public static CptTemplate createTemplate(Project project, String name, String description) {
		final CptFile file = createFile(project, "." + name + " : " + description);
		return (CptTemplate) file.getFirstChild();
	}

	public static CptMapping createMapping(Project project, String name, String code) {
		final CptFile file = createFile(project, name + " -> " + code);
		return (CptMapping) file.getFirstChild();
	}

	public static PsiElement createCRLF(Project project) {
		final CptFile file = createFile(project, "\n");
		return file.getFirstChild();
	}

	public static CptFile createFile(Project project, String text) {
		String name = "dummy.Cpt";
		return (CptFile) PsiFileFactory.getInstance(project).
			createFileFromText(name, CptFileType.INSTANCE, text);
	}
}