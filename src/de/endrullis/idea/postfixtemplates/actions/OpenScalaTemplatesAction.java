package de.endrullis.idea.postfixtemplates.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.endrullis.idea.postfixtemplates.language.CptUtil;

/**
 * Action to open the java templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@SuppressWarnings("ComponentNotRegistered")
public class OpenScalaTemplatesAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		if (anActionEvent.getProject() != null) {
			CptUtil.getTemplateFile("scala").ifPresent(file -> {
				CptUtil.openFileInEditor(anActionEvent.getProject(), file);
			});
		}
	}
}
