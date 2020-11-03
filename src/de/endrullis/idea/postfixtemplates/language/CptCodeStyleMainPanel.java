package de.endrullis.idea.postfixtemplates.language;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;

/**
 * Main panel of code style settings.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class CptCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
	public CptCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
		super(CptLanguage.INSTANCE, currentSettings, settings);
	}

	@Override
	protected void initTabs(CodeStyleSettings settings) {
		addIndentOptionsTab(settings);
		addSpacesTab(settings);
		addWrappingAndBracesTab(settings);
		addBlankLinesTab(settings);
	}
}
