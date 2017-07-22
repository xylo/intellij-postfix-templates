package de.endrullis.idea.postfixtemplates.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.endrullis.idea.postfixtemplates.language.CptUtil;

import java.io.File;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class OpenJavaTemplatesAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		File file = CptUtil.getTemplateFile("java").get();

		CptUtil.openFileInEditor(anActionEvent.getProject(), file);
	}
}
