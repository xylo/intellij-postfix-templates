package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiManager;
import com.intellij.ui.GuiUtils;
import com.intellij.ui.ToolbarDecorator;
import de.endrullis.idea.postfixtemplates.language.CptFileType;
import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.language.psi.CptFile;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateProvider;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;
import static de.endrullis.idea.postfixtemplates.utils.StringUtils.replace;

public class CptPluginSettingsForm implements CptPluginSettings.Holder, Disposable {
	/** This field holds the last state of the tree before saving the settings or null. */
	@Nullable
	private static Map<CptLang, List<CptVirtualFile>> lastTreeState;

	public static Map<CptLang, List<CptVirtualFile>> getLastTreeState() {
		val state = lastTreeState;
		lastTreeState = null;
		return state;
	}

	private JPanel         mainPanel;
	private JPanel         templatesEditorPanel;
	private JButton        editButton;
	private JRadioButton   emptyLambdaRadioButton;
	private JRadioButton   varLambdaRadioButton;
	private JButton        resetYourTemplatesButton;
	private JButton        showDiffButton;
	private JList<CptLang> languageList;
	private JTextField     templateSuffixField;
	private JPanel         treeContainer;

	@Nullable
	private Editor templatesEditor;

	@Nullable
	private CptManagementTree checkboxTree;


	public CptPluginSettingsForm() {
	}

	private void createTree() {
		checkboxTree = new CptManagementTree() {
			@Override
			protected void selectionChanged() {
				try {
					assert checkboxTree != null;
					if (checkboxTree.getSelectedFile() != null) {
						val file = checkboxTree.getSelectedFile().getFile();
						setEditorContent(file.exists() ? CptUtil.getContent(file) : "");
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		};

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(ToolbarDecorator.createDecorator(checkboxTree)
			.setAddActionUpdater(e -> checkboxTree.canAddFile())
			.setAddAction(button -> checkboxTree.addFile(button))
			.setEditActionUpdater(e -> checkboxTree.canEditSelectedFile())
			.setEditAction(button -> checkboxTree.editSelectedFile())
			.setRemoveActionUpdater(e -> checkboxTree.canRemoveSelectedFiles())
			.setRemoveAction(button -> checkboxTree.removeSelectedFiles())
			.setMoveDownActionUpdater(e -> checkboxTree.canMoveSelectedFiles())
			.setMoveDownAction( e -> checkboxTree.moveDownSelectedFiles())
			.setMoveUpActionUpdater(e -> checkboxTree.canMoveSelectedFiles())
			.setMoveUpAction(e -> checkboxTree.moveUpSelectedFiles())
			.createPanel());

		treeContainer.setLayout(new BorderLayout());
		treeContainer.add(panel);
	}

	JComponent getComponent() {
		GuiUtils.replaceJSplitPaneWithIDEASplitter(mainPanel);

		languageList.setModel(new DefaultListModel<CptLang>() {{
			SupportedLanguages.supportedLanguages.forEach(l -> addElement(l));
		}});
		languageList.getSelectionModel().addListSelectionListener(e -> {
			updateEditorContent();
		});
		languageList.setSelectedValue(SupportedLanguages.getCptLang("java"), true);

		editButton.addActionListener(e -> openTemplatesInEditor());
		resetYourTemplatesButton.addActionListener(e -> resetTemplates());
		showDiffButton.addActionListener(e -> showDiff());

		emptyLambdaRadioButton.addActionListener(e -> changeLambdaStyle(false));
		varLambdaRadioButton.addActionListener(e -> changeLambdaStyle(true));

		new ButtonGroup() {{
			add(emptyLambdaRadioButton);
			add(varLambdaRadioButton);
		}};

		if (checkboxTree == null) {
			createTree();
		}

		return mainPanel;
	}

	private void fillTree(Map<String, List<CptPluginSettings.VFile>> langName2virtualFile) {
		assert checkboxTree != null;

		Map<CptLang, List<CptPluginSettings.VFile>> lang2file = new HashMap<>();

		for (CptLang lang : SupportedLanguages.supportedLanguages) {
			// add files from saved settings
			List<CptPluginSettings.VFile> cptFiles = new ArrayList<>(langName2virtualFile.getOrDefault(lang.getLanguage(), _List()));

			// add files from file system (for compatibility with older versions)
			CptUtil.getTemplateFile(lang.getLanguage()).ifPresent(file -> {
				if (cptFiles.stream().noneMatch(f -> FileUtil.filesEqual(new File(f.file), file))) {
					try {
						cptFiles.add(new CptPluginSettings.VFile(true, file.toURI().toURL().toString(), file.getAbsolutePath()));
					} catch (MalformedURLException ignored) {
					}
				}
			});

			lang2file.put(lang, cptFiles);
		}

		checkboxTree.initTree(lang2file);
	}

	private void changeLambdaStyle(boolean preFilled) {
		if (preFilled) {
			varLambdaRadioButton.setSelected(true);
		} else {
			emptyLambdaRadioButton.setSelected(true);
		}

		updateEditorContent(preFilled);
	}

	private void updateEditorContent() {
		updateEditorContent(varLambdaRadioButton.isSelected());
	}

	private void updateEditorContent(boolean preFilled) {
		final String[] templatesText = {CptUtil.getDefaultTemplates(getSelectedLang().getLanguage())};

		new BufferedReader(new InputStreamReader(
			CptUtil.class.getResourceAsStream("templatemapping/" + (preFilled ? "var" : "empty") + "Lambda.txt")
		)).lines().filter(l -> l.contains("→")).forEach(line -> {
			String[] split = line.split("→");
			templatesText[0] = replace(templatesText[0], split[0].trim(), split[1].trim());
		});

		setEditorContent(templatesText[0]);
	}

	private void setEditorContent(String templatesText) {
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			ApplicationManager.getApplication().runWriteAction(() -> templatesEditor.getDocument().setText(templatesText));
		}
	}

	private void createUIComponents() {
		templatesEditorPanel = new JPanel(new BorderLayout());

		templatesEditor = createEditor();
		templatesEditorPanel.add(templatesEditor.getComponent(), BorderLayout.CENTER);
	}

	private void openTemplatesInEditor() {
		File file = CptUtil.getTemplateFile(getSelectedLang().getLanguage()).get();

		Project project = CptUtil.getActiveProject();

		CptUtil.openFileInEditor(project, file);

		// close settings dialog
		closeSettings();
	}

	private void resetTemplates() {
		CptUtil.createTemplateFile(getSelectedLang().getLanguage(), getTemplateText());

		//ApplicationManager.getApplication().invokeLater(() -> {
		Project project = CptUtil.getActiveProject();

		// refresh file content
		File file = CptUtil.getTemplateFile(getSelectedLang().getLanguage()).get();
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
	}

	private void closeSettings() {
		JDialog frame = (JDialog) SwingUtilities.getRoot(mainPanel);
		frame.setVisible(false);
	}

	private void showDiff() {
		Project project = CptUtil.getActiveProject();

		CptUtil.getTemplateFile(getSelectedLang().getLanguage()).ifPresent(file -> {
			VirtualFile vFile = LocalFileSystem.getInstance().findFileByIoFile(file);

			DocumentContent content1 = DiffContentFactory.getInstance().create(getTemplateText());
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
		templateSuffixField.setText(settings.getTemplateSuffix());

		fillTree(settings.getLangName2virtualFile());
	}

	@NotNull
	@Override
	public CptPluginSettings getPluginSettings() {
		assert checkboxTree != null;
		lastTreeState = checkboxTree.getState();
		val langName2virtualFile = checkboxTree.getExport();
		return new CptPluginSettings(varLambdaRadioButton.isSelected(), langName2virtualFile, templateSuffixField.getText());
	}

	@Override
	public void dispose() {
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			EditorFactory.getInstance().releaseEditor(templatesEditor);
		}
		templatesEditor = null;
	}

	public CptLang getSelectedLang() {
		return languageList.getSelectedValue();
	}

	public CptVirtualFile getSelectedFile() {
		return checkboxTree.getSelectedFile();
	}

	public String getTemplateText() {
		return CptUtil.getDefaultTemplates(getSelectedLang().getLanguage());
	}
}
