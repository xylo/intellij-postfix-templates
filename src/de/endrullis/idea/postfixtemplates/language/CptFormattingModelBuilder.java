package de.endrullis.idea.postfixtemplates.language;

import com.intellij.formatting.*;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import org.jetbrains.annotations.NotNull;

public class CptFormattingModelBuilder implements FormattingModelBuilder {
	@Override
	public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
		return FormattingModelProvider.createFormattingModelForPsiFile(formattingContext.getPsiElement().getContainingFile(),
			new CptBlock(formattingContext.getPsiElement().getNode(),
				Wrap.createWrap(WrapType.NONE, false),
				Indent.getNoneIndent(),
				Indent.getNoneIndent(),
				Alignment.createAlignment(),
				createSpaceBuilder(formattingContext.getCodeStyleSettings())),
			formattingContext.getCodeStyleSettings());
	}

	private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
		return new SpacingBuilder(settings, CptLanguage.INSTANCE)
			.around(CptTypes.SEPARATOR).spaceIf(settings.getCommonSettings(CptLanguage.INSTANCE).SPACE_AROUND_ASSIGNMENT_OPERATORS)
			//.around(CptTypes.MAP).spaceIf(settings.getCommonSettings(CptLanguage.INSTANCE).SPACE_AROUND_LAMBDA_ARROW)
			.before(CptTypes.MAPPING).spaces(2);
	}
}
