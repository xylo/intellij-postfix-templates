package de.endrullis.idea.postfixtemplates.language;

import com.intellij.application.options.*;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.*;
import org.jetbrains.annotations.*;

public class CptCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
  @Override
  public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
    return new CptCodeStyleSettings(settings);
  }

  @Nullable
  @Override
  public String getConfigurableDisplayName() {
    return "Custom Postfix Templates";
  }

  @NotNull
  @Override
  public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
    return new CodeStyleAbstractConfigurable(settings, originalSettings, "CustomPostfixTemplates") {
      @Override
      protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
        return new CptCodeStyleMainPanel(getCurrentSettings(), settings);
      }

      @Nullable
      @Override
      public String getHelpTopic() {
        return null;
      }
    };
  }

}