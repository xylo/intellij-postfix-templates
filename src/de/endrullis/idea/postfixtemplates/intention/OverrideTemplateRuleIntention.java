package de.endrullis.idea.postfixtemplates.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import de.endrullis.idea.postfixtemplates.actions.EditorTypedHandlerDelegate;
import de.endrullis.idea.postfixtemplates.language.CptFileType;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.language.psi.CptTemplate;
import lombok.val;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NonNls
public class OverrideTemplateRuleIntention extends PsiElementBaseIntentionAction implements IntentionAction {

	@NotNull
	public String getText() {
		return "Override template rule";
	}

	@Override
	public boolean checkFile(@Nullable PsiFile file) {
		if (file != null) {
			return file.getFileType().equals(CptFileType.INSTANCE);
		} else {
			return false;
		}
	}

	@NotNull
	public String getFamilyName() {
		return getText();
	}

	public boolean isAvailable(@NotNull Project project, Editor editor, @Nullable PsiElement element) {
		if (element == null || editor == null || CptUtil.isUserTemplateFile(CptUtil.getVirtualFile(element))) {
			return false;
		}

		while (element != null && !(element instanceof CptMapping) && !(element instanceof CptTemplate)) {
			element = element.getParent();
		}

		return element instanceof CptMapping;
	}

	public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
		val document = editor.getDocument();
		val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);

		if (psiFile != null) {
			EditorTypedHandlerDelegate.eventuallyOpenFileEditDialog(document, project, element, false);
		}
	}

	public boolean startInWriteAction() {
		return false;
	}

}
