package de.endrullis.idea.postfixtemplates;

import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import de.endrullis.idea.postfixtemplates.actions.EditorTypedHandler;
import lombok.val;

/**
 * Plugin initialization.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class ApplicationImpl implements ApplicationInterface {

	public ApplicationImpl() {
		setupEditorTypedHandler();
	}

	private void setupEditorTypedHandler() {
		val actionManager = EditorActionManager.getInstance();
		val typedAction = actionManager.getTypedAction();
		val oldHandler = typedAction.getHandler();
		typedAction.setupHandler(new EditorTypedHandler(oldHandler));
	}

}
