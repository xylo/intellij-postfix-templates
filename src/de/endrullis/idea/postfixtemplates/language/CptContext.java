package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptContext extends TemplateContextType {
	public CptContext() {
		super("CPT", "Custom Postfix Templates");
	}

	@Override
	public boolean isInContext(@NotNull PsiFile psiFile, int offset) {
		return psiFile.getFileType() == CptFileType.INSTANCE;
	}
}
