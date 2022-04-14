package de.endrullis.idea.postfixtemplates;

import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import de.endrullis.idea.postfixtemplates.actions.EditorTypedHandler;
import de.endrullis.idea.postfixtemplates.utils.CptUpdateUtils;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * Project opening activity.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class MyPostStartupActivity implements StartupActivity {

	public void runActivity(@NotNull Project project) {
		setupEditorTypedHandler();
		CptUpdateUtils.checkForWebTemplateUpdates(project);
	}

	private void setupEditorTypedHandler() {
		val typedAction = TypedAction.getInstance();
		val oldHandler  = typedAction.getHandler();
		typedAction.setupHandler(new EditorTypedHandler(oldHandler));
	}

}
