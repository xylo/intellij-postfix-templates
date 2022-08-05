package de.endrullis.idea.postfixtemplates.language.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import de.endrullis.idea.postfixtemplates.language.CptFileType;
import de.endrullis.idea.postfixtemplates.language.CptLanguage;
import org.jetbrains.annotations.NotNull;

public class CptFile extends PsiFileBase {
	public CptFile(@NotNull FileViewProvider viewProvider) {
		super(viewProvider, CptLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public FileType getFileType() {
		return CptFileType.INSTANCE;
	}

	@Override
	public String toString() {
		return "Custom Postfix Templates File";
	}

}
