package de.endrullis.idea.postfixtemplates.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.settings.CptPluginConfigurable;

/**
 * Action to open the templates of the language in the current editor.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class OpenSettingsAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		Project project = anActionEvent.getProject();

		if (project != null) {
			CptUtil.openPluginSettings(project);
		}
	}
}
