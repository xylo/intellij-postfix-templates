package de.endrullis.idea.postfixtemplates;

import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import de.endrullis.idea.postfixtemplates.actions.EditorTypedHandler;
import de.endrullis.idea.postfixtemplates.utils.CptUpdateUtils;
import io.sentry.Sentry;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * Project opening activity.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class MyPostStartupActivity implements StartupActivity {

	public void runActivity(@NotNull Project project) {
		setupSentry();
		setupEditorTypedHandler();
		CptUpdateUtils.checkForWebTemplateUpdates(project);
	}

	private void setupSentry() {
		Sentry.init(options -> {
			options.setDsn("https://d5db57a4e01b468b823e45f831d58fb7@o1399782.ingest.sentry.io/6727652");
			// Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
			// We recommend adjusting this value in production.
			options.setTracesSampleRate(1.0);
			// When first trying Sentry it's good to see what the SDK is doing:
			options.setDebug(true);
		});
	}

	private void setupEditorTypedHandler() {
		val typedAction = TypedAction.getInstance();
		val oldHandler  = typedAction.getHandler();
		typedAction.setupHandler(new EditorTypedHandler(oldHandler));
	}

}
