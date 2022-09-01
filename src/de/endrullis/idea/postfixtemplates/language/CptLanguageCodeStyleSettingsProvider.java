package de.endrullis.idea.postfixtemplates.language;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.BlankLinesOption.KEEP_BLANK_LINES_IN_CODE;
import static com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.SpacingOption.SPACE_AROUND_ASSIGNMENT_OPERATORS;
import static com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.SpacingOption.SPACE_AROUND_LAMBDA_ARROW;
import static com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.WrappingOrBraceOption.ALIGN_GROUP_FIELD_DECLARATIONS;

/**
 * The code style settings provider for CPT.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

	@NotNull
	@Override
	public Language getLanguage() {
		return CptLanguage.INSTANCE;
	}

	@Override
	public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
		if (settingsType == SettingsType.SPACING_SETTINGS) {
			consumer.showStandardOptions(SPACE_AROUND_ASSIGNMENT_OPERATORS.name());
			consumer.renameStandardOption(SPACE_AROUND_ASSIGNMENT_OPERATORS.name(), "Separator");
			consumer.showStandardOptions(SPACE_AROUND_LAMBDA_ARROW.name());
			consumer.renameStandardOption(SPACE_AROUND_LAMBDA_ARROW.name(), "Mapping arrow");
		} else if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
			consumer.showStandardOptions(ALIGN_GROUP_FIELD_DECLARATIONS.name());
		} else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
			consumer.showStandardOptions(KEEP_BLANK_LINES_IN_CODE.name());
		}
	}

	@Override
	public String getCodeSample(@NotNull SettingsType settingsType) {
		return CptUtil.getDefaultTemplates("java");
	}

	@Override
	public IndentOptionsEditor getIndentOptionsEditor() {
		return new SmartIndentOptionsEditor();
	}

}
