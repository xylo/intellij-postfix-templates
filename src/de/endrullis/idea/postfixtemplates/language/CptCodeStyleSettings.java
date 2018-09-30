package de.endrullis.idea.postfixtemplates.language;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class CptCodeStyleSettings extends CustomCodeStyleSettings {
	public CptCodeStyleSettings(CodeStyleSettings settings) {
		super("CustomPostfixTemplatesCodeStyleSettings", settings);
	}
}