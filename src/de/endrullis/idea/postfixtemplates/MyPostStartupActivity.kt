package de.endrullis.idea.postfixtemplates

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import de.endrullis.idea.postfixtemplates.utils.CptUpdateUtils

/**
 * Project opening activity.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class MyPostStartupActivity : ProjectActivity {
	override suspend fun execute(project: Project) {
		CptUpdateUtils.checkForWebTemplateUpdates(project)
	}
}
