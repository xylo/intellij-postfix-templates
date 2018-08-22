package de.endrullis.idea.postfixtemplates;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import de.endrullis.idea.postfixtemplates.utils.CptUpdateUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Project opening activity.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class MyPostStartupActivity implements StartupActivity {

	public void runActivity(@NotNull Project project) {
		CptUpdateUtils.checkForWebTemplateUpdates(project);
	}

}
