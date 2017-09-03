package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scala.lang.completion.postfix.templates.selector.AncestorSelector;

public class ScalaPostfixTemplateProvider extends CustomPostfixTemplateProvider {

	@NotNull
	@Override
	protected String getLanguage() {
		return "scala";
	}

	@NotNull
	@Override
	protected CustomScalaStringPostfixTemplate createTemplate(String className, String templateName, String description, String template) {
		return new CustomScalaStringPostfixTemplate(className, templateName, description, template);
	}

	@Override
	public void preExpand(@NotNull PsiFile file, @NotNull Editor editor) {
	}

	@Override
	public void afterExpand(@NotNull PsiFile file, @NotNull Editor editor) {
	}

	@NotNull
	@Override
	public PsiFile preCheck(@NotNull PsiFile copyFile, @NotNull Editor realEditor, int currentOffset) {
		return copyFile;
	}
}
