package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

public class AddTemplateFileDialog extends DialogWrapper {
	private final Project        project;
	private final CptLang        lang;
	private final CptVirtualFile cptVirtualFile;
	private final Set<String>    otherFileNames;

	private JPanel                 contentPane;
	private JComboBox<CptFileType> typeField;
	private JTextField             urlField;
	private JButton                fileChooserButton;
	private JTextField             nameField;
	private JLabel                 urlLabel;

	public AddTemplateFileDialog(Project project, @NotNull CptLang lang, @Nullable CptVirtualFile cptVirtualFile, @NotNull Set<String> otherFileNames) {
		super(project);
		this.project = project;
		this.lang = lang;
		this.cptVirtualFile = cptVirtualFile;
		this.otherFileNames = otherFileNames;

		val url = cptVirtualFile != null ? cptVirtualFile.getUrl() : null;

		setTitle(url == null ? "Add Template File" : "Edit Template File");

		//setContentPane(contentPane);
		//setModal(true);

		typeField.setModel(new DefaultComboBoxModel<>(CptFileType.values()));
		typeField.addActionListener(e -> {
			val selectedItem = (CptFileType) typeField.getSelectedItem();
			fileChooserButton.setVisible(selectedItem != CptFileType.Web);
			urlLabel.setEnabled(selectedItem != CptFileType.LocalInPluginDir);
			urlField.setEnabled(selectedItem != CptFileType.LocalInPluginDir);
			fileChooserButton.setEnabled(selectedItem != CptFileType.LocalInPluginDir);
			if (selectedItem == CptFileType.LocalInPluginDir) {
				urlField.setText("");
			}
		});

		fileChooserButton.setVisible(false);
		fileChooserButton.addActionListener(e -> onChooseFile());

		if (url == null) {
			typeField.setSelectedItem(CptFileType.LocalInPluginDir);
		} else if (url.getProtocol().equals("file")) {
			typeField.setSelectedItem(CptFileType.LocalInFs);
		} else {
			typeField.setSelectedItem(CptFileType.Web);
		}

		if (url != null) {
			urlField.setText(url.toString());
		}

		if (cptVirtualFile != null) {
			nameField.setText(cptVirtualFile.getFile().getName().replace(".postfixTemplates", ""));
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
				return virtualFile != null && extensions.contains(virtualFile.getExtension());
			}
		};

		val virtualFile = FileChooser.chooseFile(descriptor, project, null);

		if (virtualFile != null) {
			urlField.setText(virtualFile.getUrl());
			nameField.setText(virtualFile.getNameWithoutExtension());
		}
	}

	public CptVirtualFile getCptVirtualFile() {
		try {
			URL     oldUrl  = null;
			File    oldFile = null;
			boolean isNew   = true;

			if (cptVirtualFile != null) {
				oldUrl = cptVirtualFile.getOldUrl();
				if (oldUrl == null) {
					oldUrl = cptVirtualFile.getUrl();
				}
				oldFile = cptVirtualFile.getOldFile();
				if (oldFile == null) {
					oldFile = cptVirtualFile.getFile();
				}
				isNew = cptVirtualFile.isNew();
			}

			val urlString = urlField.getText().trim().isEmpty() ? null : urlField.getText();
			val newUrl    = urlString != null ? new URI(urlString).toURL() : null;
			val newFile   = CptUtil.getTemplateFile(lang.getLanguage(), nameField.getText().trim());

			val newCptVirtualFile = new CptVirtualFile(null, newUrl, newFile, isNew);

			if (!isNew && newUrl != null && oldUrl != null && !newUrl.toString().equals(oldUrl.toString())) {
				newCptVirtualFile.setOldUrl(oldUrl);
			}
			if (!isNew && oldFile != null && !FileUtil.filesEqual(newFile, oldFile)) {
				newCptVirtualFile.setOldFile(oldFile);
			}

			return newCptVirtualFile;
		} catch (MalformedURLException | URISyntaxException ignored) {
			return null;
		}
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return contentPane;
	}

	public static void main(String[] args) {
		val dialog = new AddTemplateFileDialog(null, Objects.requireNonNull(SupportedLanguages.getCptLang("java")), null, _Set());

		new JDialog() {{
			getRootPane().setContentPane(dialog.createCenterPanel());
			setSize(500, 100);
			setVisible(true);
		}};
	}

	@Nullable
	@Override
	protected ValidationInfo doValidate() {
		val urlString = urlField.getText().trim();
		if (typeField.getSelectedItem() == CptFileType.LocalInFs) {
			try {
				//noinspection ResultOfMethodCallIgnored
				new URI(urlString).toURL();
			} catch (URISyntaxException | MalformedURLException e) {
				return new ValidationInfo("Please select an existing file.", fileChooserButton);
			}
		}

		if (typeField.getSelectedItem() == CptFileType.Web) {
			try {
				//noinspection ResultOfMethodCallIgnored
				new URI(urlString).toURL();
			} catch (URISyntaxException | MalformedURLException e) {
				return new ValidationInfo("Please enter a valid URL.", urlField);
			}
		}

		final String name = nameField.getText().trim();
		if (name.isEmpty()) {
			return new ValidationInfo("Please enter a name for the local file.", nameField);
		}
		if (otherFileNames.contains(name)) {
			return new ValidationInfo("This name is already used. Please choose another one.", nameField);
		}

		return null;
	}

	@Nullable
	@Override
	public JComponent getPreferredFocusedComponent() {
		return nameField;
	}

}
