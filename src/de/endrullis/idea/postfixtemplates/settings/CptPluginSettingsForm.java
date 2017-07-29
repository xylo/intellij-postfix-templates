package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import de.endrullis.idea.postfixtemplates.language.CptFileType;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

public class CptPluginSettingsForm implements CptPluginSettings.Holder, Disposable {
	private JPanel mainPanel;
	private JPanel templatesEditorPanel;
	private JButton editButton;

	@NotNull
	private String templatesText = "";
	@Nullable
	private Editor templatesEditor;


	public CptPluginSettingsForm() {
	}

	public JComponent getComponent() {
		editButton.addActionListener(event -> openTemplatesInEditor());

		return mainPanel;
	}

	private void createUIComponents() {
		templatesEditorPanel = new JPanel(new BorderLayout());

		templatesEditor = createEditor();
		templatesEditorPanel.add(templatesEditor.getComponent(), BorderLayout.CENTER);
	}

	private void openTemplatesInEditor() {
		File file = CptUtil.getTemplateFile("java").get();

		Project project = CptUtil.getActiveProject();

		CptUtil.openFileInEditor(project, file);

		// close settings dialog
		JDialog frame = (JDialog) SwingUtilities.getRoot(mainPanel);
		frame.setVisible(false);
	}

	@NotNull
	private static Editor createEditor() {
		EditorFactory editorFactory = EditorFactory.getInstance();
		Document editorDocument = editorFactory.createDocument("");
		return editorFactory.createEditor(editorDocument, null, CptFileType.INSTANCE, true);
	}

	@Override
	public void setPluginSettings(@NotNull CptPluginSettings settings) {
		// load template file content to display
		CptUtil.getTemplateFile("java").ifPresent(file -> {
			if (file.exists()) {
				try {
					templatesText = CptUtil.getContent(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			ApplicationManager.getApplication().runWriteAction(() -> templatesEditor.getDocument().setText(templatesText));
		}
	}

	@NotNull
	@Override
	public CptPluginSettings getPluginSettings() {
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			templatesText = ReadAction.compute(() -> templatesEditor.getDocument().getText());
		}
		return new CptPluginSettings();
	}

	@Override
	public void dispose() {
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			EditorFactory.getInstance().releaseEditor(templatesEditor);
		}
		templatesEditor = null;
	}

}
