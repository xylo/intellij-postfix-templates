package de.endrullis.idea.postfixtemplates.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import de.endrullis.idea.postfixtemplates.language.CptUtil;

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

				final PsiFile psiFile = PsiManager.getInstance(project).findFile(editor.getFile());

				String language = psiFile.getLanguage().getID().toLowerCase();

				CptUtil.getTemplateFile(language).ifPresent(file -> {
					CptUtil.openFileInEditor(project, file);
				});
			}
		}
	}
}
