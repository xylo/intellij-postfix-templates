package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PluginSettingsForm implements PluginSettings.Holder, Disposable {
	private JPanel mainPanel;
	private JBCheckBox pluginEnabledField;
	private JPanel templatesEditorPanel;

	@NotNull
	private String templatesText = "";
	@Nullable
	private Editor templatesEditor;
	@NotNull
	private final ActionListener actionListener;


	public PluginSettingsForm() {
		actionListener = e -> {
			templatesEditorPanel.setEnabled(pluginEnabledField.isSelected());
			if (templatesEditor != null && !templatesEditor.isDisposed()) {
				final boolean canEdit = pluginEnabledField.isSelected();

				templatesEditor.getDocument().setReadOnly(!canEdit);
				templatesEditor.getSettings().setCaretRowShown(canEdit);

				Color baseColor = templatesEditor.getColorsScheme().getDefaultBackground();
				if (canEdit) {
					((EditorEx) templatesEditor).setBackgroundColor(baseColor);
				} else {
					((EditorEx) templatesEditor).setBackgroundColor(ColorUtil.isDark(baseColor) ?
						ColorUtil.brighter(baseColor, 1) : ColorUtil.darker(baseColor, 1));
				}
			}
		};
		pluginEnabledField.addActionListener(actionListener);
	}                   

	public JComponent getComponent() {
		return mainPanel;
	}

	private void createUIComponents() {
		templatesEditorPanel = new JPanel(new BorderLayout());

		templatesEditor = createEditor();
		templatesEditorPanel.add(templatesEditor.getComponent(), BorderLayout.CENTER);
	}

	@NotNull
	private static Editor createEditor() {
		EditorFactory editorFactory = EditorFactory.getInstance();
		Document editorDocument = editorFactory.createDocument("");
		EditorEx editor = (EditorEx) editorFactory.createEditor(editorDocument);
		fillEditorSettings(editor.getSettings());
		setHighlighting(editor);
		return editor;
	}

	private static void setHighlighting(EditorEx editor) {
		final FileType txtFileType = FileTypeManager.getInstance().getFileTypeByExtension("txt");
		if (txtFileType == UnknownFileType.INSTANCE) {
			return;
		}
		final EditorHighlighter editorHighlighter =
			HighlighterFactory.createHighlighter(txtFileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
		editor.setHighlighter(editorHighlighter);
	}

	private static void fillEditorSettings(final EditorSettings editorSettings) {
		editorSettings.setWhitespacesShown(false);
		editorSettings.setLineMarkerAreaShown(false);
		editorSettings.setIndentGuidesShown(false);
		editorSettings.setLineNumbersShown(true);
		editorSettings.setFoldingOutlineShown(false);
		editorSettings.setAdditionalColumnsCount(1);
		editorSettings.setAdditionalLinesCount(1);
		editorSettings.setUseSoftWraps(false);
	}

	@Override
	public void setPluginSettings(@NotNull PluginSettings settings) {
		pluginEnabledField.setSelected(settings.isPluginEnabled());
		templatesText = settings.getTemplatesText();
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			ApplicationManager.getApplication().runWriteAction(() -> templatesEditor.getDocument().setText(templatesText));
		}

		//noinspection ConstantConditions
		actionListener.actionPerformed(null);
	}

	@NotNull
	@Override
	public PluginSettings getPluginSettings() {
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			templatesText = ReadAction.compute(() -> templatesEditor.getDocument().getText());
		}
		return new PluginSettings(pluginEnabledField.isSelected(), templatesText);
	}

	@Override
	public void dispose() {
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			EditorFactory.getInstance().releaseEditor(templatesEditor);
		}
		templatesEditor = null;
	}

}
