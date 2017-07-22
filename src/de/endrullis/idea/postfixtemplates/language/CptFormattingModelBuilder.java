package de.endrullis.idea.postfixtemplates.language;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import org.jetbrains.annotations.*;

public class CptFormattingModelBuilder implements FormattingModelBuilder {
	@NotNull
	@Override
	public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
		return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(),
			new CptBlock(element.getNode(),
				Wrap.createWrap(WrapType.NONE, false),
				Indent.getNoneIndent(), 
				Alignment.createAlignment(),
				createSpaceBuilder(settings)),
			settings);
	}

	private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
		return new SpacingBuilder(settings, CptLanguage.INSTANCE)
			.around(CptTypes.SEPARATOR)
			.spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
			.around(CptTypes.MAP)
			.spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
			.before(CptTypes.MAPPING)
			.spaces(2);
	}

	@Nullable
	@Override
	public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
		return null;
	}
}