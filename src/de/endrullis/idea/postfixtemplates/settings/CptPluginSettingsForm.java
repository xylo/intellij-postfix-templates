package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.ui.GuiUtils;
import com.intellij.ui.ToolbarDecorator;
import de.endrullis.idea.postfixtemplates.language.CptFileType;
import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;

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
	private JRadioButton   emptyLambdaRadioButton;
	private JRadioButton   varLambdaRadioButton;
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
			val filesFromConfig = cptFiles.stream().map(f -> f.getFile()).collect(Collectors.toSet());

			// add files from filesystem that are not already in the settings
			val templateFilesFromDir = CptUtil.getTemplateFilesFromLanguageDir(lang.getLanguage());
			Arrays.stream(templateFilesFromDir).filter(f -> !filesFromConfig.contains(f.getAbsolutePath())).forEach(file -> {
				cptFiles.add(new CptPluginSettings.VFile(true, null, file.getAbsolutePath()));
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

		//updateEditorContent(preFilled);
	}

	/*
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
	*/

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

	/*
	private void openTemplatesInEditor() {
		File file = CptUtil.getTemplateFile(getSelectedLang().getLanguage()).get();

		Project project = CptUtil.getActiveProject();

		CptUtil.openFileInEditor(project, file);

		// close settings dialog
		closeSettings();
	}
	*/

	private void closeSettings() {
		JDialog frame = (JDialog) SwingUtilities.getRoot(mainPanel);
		frame.setVisible(false);
	}

	/*
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
	*/

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

}
