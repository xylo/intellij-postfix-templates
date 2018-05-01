package de.endrullis.idea.postfixtemplates.actions;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import de.endrullis.idea.postfixtemplates.language.CptUtil;

import java.io.File;
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
				Language language = psiFile.getLanguage();

				while (language != null) {
					List<File> templateFile = CptUtil.getTemplateFiles(language.getID().toLowerCase());

					if (!templateFile.isEmpty()) {
						CptUtil.openFileInEditor(project, templateFile.get(0));
						return;
					}

					language = language.getBaseLanguage();
				}
			}
		}
	}
}
