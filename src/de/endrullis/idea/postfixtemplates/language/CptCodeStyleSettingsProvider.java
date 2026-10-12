package de.endrullis.idea.postfixtemplates.language;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CptCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
	@Nullable
	@Override
	public String getConfigurableDisplayName() {
		return "Custom Postfix Templates";
	}

	@Nullable
	@Override
	public String getConfigurableId() {
		return "preferences.sourceCode." + CptLanguage.INSTANCE.getID();
	}

	@NotNull
	@Override
	public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings originalSettings) {
		return new CodeStyleAbstractConfigurable(settings, originalSettings, "CustomPostfixTemplates") {
			@Override
			protected @NotNull CodeStyleAbstractPanel createPanel(@NotNull CodeStyleSettings settings) {
				return new CptCodeStyleMainPanel(getCurrentSettings(), settings);
			}

			@Nullable
			@Override
			public String getHelpTopic() {
				return null;
			}
		};
	}

	@Nullable
	@Override
	public Language getLanguage() {
		return CptLanguage.INSTANCE;
	}
}