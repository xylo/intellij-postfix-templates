package de.endrullis.idea.postfixtemplates.actions;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.panels.VerticalBox;
import de.endrullis.idea.postfixtemplates.language.CptFileType;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import lombok.val;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Action to open the templates of the language in the current editor.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class OpenTemplatesAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		Project project = anActionEvent.getProject();

		if (project != null) {
			FileEditor[] selectedEditors = FileEditorManager.getInstance(project).getSelectedEditors();

			if (selectedEditors.length > 0) {
				FileEditor editor = selectedEditors[0];

				final PsiFile psiFile = PsiManager.getInstance(project).findFile(Objects.requireNonNull(editor.getFile()));

				assert psiFile != null;

				List<Language> supportedLanguages = new ArrayList<>();

				{
					Language language = psiFile.getLanguage();
					while (language != null) {
						if (SupportedLanguages.supportedLanguageIds.contains(language.getID().toLowerCase())) {
							supportedLanguages.add(language);
						}
						language = language.getBaseLanguage();
					}
				}

				if (supportedLanguages.isEmpty()) {
					if (!psiFile.getFileType().equals(CptFileType.INSTANCE)) {
						val builder = new DialogBuilder().title("Custom Postfix Templates").centerPanel(
							new VerticalBox() {{
								add(new JLabel("The Custom Postfix Templates plugin does not support " + psiFile.getLanguage().getDisplayName() + " at the moment."));
							}}
						);
						builder.addOkAction().setText("OK");
						builder.show();
					}
				} else {
					boolean multiLang = supportedLanguages.size() > 1;

					DefaultActionGroup actionGroup = new DefaultActionGroup();

					for (Language language : supportedLanguages) {
						if (SupportedLanguages.supportedLanguageIds.contains(language.getID().toLowerCase())) {

							List<File> templateFiles = CptUtil.getEditableTemplateFiles(language.getID().toLowerCase());

							if (!templateFiles.isEmpty()) {
								for (File file : templateFiles) {
									String prefix = multiLang ? language.getDisplayName() + ": " : "";
									actionGroup.add(new DumbAwareAction(prefix + file.getName().replace(".postfixTemplates", "")) {
										@Override
										public void actionPerformed(AnActionEvent anActionEvent) {
											Project project = CptUtil.getActiveProject();
											CptUtil.openFileInEditor(project, file);
										}
									});
								}
							}
						}
					}

					actionGroup.add(new DumbAwareAction("Create new template files / edit settings") {
						@Override
						public void actionPerformed(AnActionEvent anActionEvent) {
							Project project = CptUtil.getActiveProject();
							CptUtil.openPluginSettings(project);
						}
					});

					DataContext context = anActionEvent.getDataContext();
					ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup("Choose postfix template file to open", actionGroup, context,
						JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING, true, null);
					popup.showInBestPositionFor(context);
				}
			}
		}
	}
}
