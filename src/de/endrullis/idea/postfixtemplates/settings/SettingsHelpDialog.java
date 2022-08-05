package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class SettingsHelpDialog extends DialogWrapper {
	private JPanel contentPane;

	public SettingsHelpDialog(@Nullable Project project) {
		super(project);

		setTitle("How It Works");

		if (project != null) {
			init();
		}
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return contentPane;
	}
}
