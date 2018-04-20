package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;

public class AddTemplateFileDialog extends DialogWrapper {
	private final Project project;
	
	private JPanel contentPane;
	private JComboBox<CptFileType> typeField;
	private JTextField             urlField;
	private JButton                fileChooserButton;

	public AddTemplateFileDialog(Project project, URL url) {
		super(project);
		this.project = project;

		setTitle("Add template file");

		//setContentPane(contentPane);
		//setModal(true);

		typeField.setModel(new DefaultComboBoxModel<>(CptFileType.values()));
		typeField.addActionListener(e -> {
			final CptFileType selectedItem = (CptFileType) typeField.getSelectedItem();
			fileChooserButton.setVisible(selectedItem == CptFileType.Local);
		});

		fileChooserButton.setVisible(false);
		fileChooserButton.addActionListener(e -> onChooseFile());

		if (url != null) {
			typeField.setSelectedItem(url.getProtocol().equals("file") ? CptFileType.Local : CptFileType.Web);
			urlField.setText(url.toString());
		}

		// call onCancel() on ESCAPE
		//contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		if (project != null) {
			init();
		}
	}

	private void onChooseFile() {
		final FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
			private final List<String> extensions = _List("postfixTemplates");

			@Override
			public boolean isFileSelectable(VirtualFile virtualFile) {
				return extensions.contains(virtualFile.getExtension());
			}

			@Override
			public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
				return super.isFileVisible(file, showHiddenFiles) && (file.isDirectory() || isFileSelectable(file));
			}
		};

		val virtualFile = FileChooser.chooseFile(descriptor, project, null);

		if (virtualFile != null) {
			urlField.setText(virtualFile.getUrl());
		}
	}

	public URL getURL() throws MalformedURLException {
		return new URL(urlField.getText());
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return contentPane;
	}

	public static void main(String[] args) throws MalformedURLException {
		val url = new File(".").toURI().toURL();
		AddTemplateFileDialog dialog = new AddTemplateFileDialog(null, url);
		final JDialog jDialog = new JDialog();
		jDialog.getRootPane().setContentPane(dialog.createCenterPanel());
		jDialog.setSize(500, 100);
		jDialog.setVisible(true);
	}

}
