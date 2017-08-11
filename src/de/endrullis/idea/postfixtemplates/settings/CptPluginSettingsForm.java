package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiManager;
import de.endrullis.idea.postfixtemplates.language.CptFileType;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.language.psi.CptFile;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static de.endrullis.idea.postfixtemplates.utils.StringUtils.replace;

public class CptPluginSettingsForm implements CptPluginSettings.Holder, Disposable {
	private JPanel       mainPanel;
	private JPanel       templatesEditorPanel;
	private JButton      editButton;
	private JRadioButton emptyLambdaRadioButton;
	private JRadioButton varLambdaRadioButton;
	private JButton      resetYourTemplatesButton;
	private JButton      showDiffButton;

	@NotNull
	private String templatesText = "";
	@Nullable
	private Editor templatesEditor;


	public CptPluginSettingsForm() {
	}

	public JComponent getComponent() {
		editButton.addActionListener(e -> openTemplatesInEditor());
		resetYourTemplatesButton.addActionListener(e -> resetTemplates());
		showDiffButton.addActionListener(e -> showDiff());

		emptyLambdaRadioButton.addActionListener(e -> changeLambdaStyle(false));
		varLambdaRadioButton.addActionListener(e -> changeLambdaStyle(true));

		new ButtonGroup() {{
			add(emptyLambdaRadioButton);
			add(varLambdaRadioButton);
		}};

		return mainPanel;
	}

	private void changeLambdaStyle(boolean preFilled) {
		if (preFilled) {
			varLambdaRadioButton.setSelected(true);
		} else {
			emptyLambdaRadioButton.setSelected(true);
		}

		final String[] templatesText = {CptUtil.getDefaultJavaTemplates()};

		new BufferedReader(new InputStreamReader(
			CptUtil.class.getResourceAsStream("templatemapping/" + (preFilled ? "var" : "empty") + "Lambda.txt")
		)).lines().filter(l -> l.contains("→")).forEach(line -> {
			String[] split = line.split("→");
			templatesText[0] = replace(templatesText[0], split[0].trim(), split[1].trim());
		});

		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			ApplicationManager.getApplication().runWriteAction(() -> templatesEditor.getDocument().setText(templatesText[0]));
		}
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
		closeSettings();
	}

	private void resetTemplates() {
		CptUtil.createTemplateFile("java", templatesText);

		//ApplicationManager.getApplication().invokeLater(() -> {
		Project project = CptUtil.getActiveProject();

		// refresh file content
		File file = CptUtil.getTemplateFile("java").get();
		VirtualFile vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
		assert vFile != null;
		vFile.refresh(false, false);

		// refresh PsiFile
		CptFile cptFile = (CptFile) PsiManager.getInstance(project).findFile(vFile);
		Document document = PsiDocumentManager.getInstance(project).getDocument(cptFile);
		PsiDocumentManager.getInstance(project).commitDocument(document);

		// close settings dialog
		closeSettings();

		// tell template provider to reload the templates
		ApplicationManager.getApplication().getMessageBus().syncPublisher(CustomPostfixTemplateProvider.TOPIC).reloadTemplates();
		//});
	}

	private void closeSettings() {
		JDialog frame = (JDialog) SwingUtilities.getRoot(mainPanel);
		frame.setVisible(false);
	}

	private void showDiff() {
		Project project = CptUtil.getActiveProject();

		CptUtil.getTemplateFile("java").ifPresent(file -> {
			VirtualFile vFile = LocalFileSystem.getInstance().findFileByIoFile(file);

			DocumentContent content1 = DiffContentFactory.getInstance().create(templatesText);
			DocumentContent content2 = DiffContentFactory.getInstance().createDocument(project, vFile);
			DiffManager.getInstance().showDiff(project, new SimpleDiffRequest("Templates Diff", content1, content2,
				"Predefined plugin templates", "Your templates"));
		});
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
		/*
		CptUtil.getTemplateFile("java").ifPresent(file -> {
			if (file.exists()) {
				try {
					templatesText = CptUtil.getContent(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		*/
		changeLambdaStyle(settings.isVarLambdaStyle());
	}

	@NotNull
	@Override
	public CptPluginSettings getPluginSettings() {
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			templatesText = ReadAction.compute(() -> templatesEditor.getDocument().getText());
		}
		return new CptPluginSettings(varLambdaRadioButton.isSelected());
	}

	@Override
	public void dispose() {
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			EditorFactory.getInstance().releaseEditor(templatesEditor);
		}
		templatesEditor = null;
	}

}
