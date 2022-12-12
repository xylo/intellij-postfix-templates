package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.GuiUtils;
import com.intellij.ui.ToolbarDecorator;
import de.endrullis.idea.postfixtemplates.language.CptFileType;
import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.languages.SupportedLanguages;
import de.endrullis.idea.postfixtemplates.utils.CptUpdateUtils;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;

public class CptPluginSettingsForm implements CptPluginSettings.Holder, Disposable {
	/**
	 * This field holds the last state of the tree before saving the settings or null.
	 */
	@Nullable
	private static Map<CptLang, List<CptVirtualFile>> lastTreeState;

	static void resetLastTreeState() {
		lastTreeState = null;
	}

	static Map<CptLang, List<CptVirtualFile>> getLastTreeState() {
		val state = lastTreeState;
		lastTreeState = null;
		return state;
	}

	private JPanel       mainPanel;
	private JPanel       templatesEditorPanel;
	private JRadioButton emptyLambdaRadioButton;
	private JRadioButton varLambdaRadioButton;
	private JPanel       treeContainer;
	private JEditorPane  templatesFileInfoLabel;
	private JCheckBox    automaticUpdatesCheckBox;
	private JCheckBox    activateNewWebTemplatesCheckBox;
	private JButton      updateNowButton;
	private JButton      testErrorReportButton;

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
				assert checkboxTree != null;
				val selectedFile = checkboxTree.getSelectedFile();

				if (selectedFile != null) {
					val file     = selectedFile.getFile();
					val fileName = selectedFile.getName().replace(".postfixTemplates", "");
					setEditorContent(file.exists() ? CptUtil.getContent(file) : "");

					if (selectedFile.isSelfMade()) {
						templatesFileInfoLabel.setText("<html><body>User Template File \"" + fileName + "\"");
					} else if (selectedFile.isLocal()) {
						String s = "<html><body>Local Template File \"" + fileName + "\"<table style='width: 100%'>";
						assert selectedFile.getUrl() != null;
						s += "<tr><td>URL:</td><td style='width: 100%'><a style='width: 100%' href=\"" + selectedFile.getUrl().toString() + "\">" + limitTo50(selectedFile.getUrl().toString()) + "</a></td></tr></table>";
						templatesFileInfoLabel.setText(s);
					} else if (selectedFile.getWebTemplateFile() != null) {
						val    webTemplateFile = selectedFile.getWebTemplateFile();
						String subject         = URLEncoder.encode("[Custom Postfix Templates] " + fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
						String s               = "<html><body>Web Template File \"" + fileName + "\"<table style='width: 100%'>";
						s += "<tr><td>Author:</td><td style='width: 100%'><a style='width: 100%' href=\"mailto:" + webTemplateFile.email + "?subject=" + subject + "\">" + webTemplateFile.author + "</a></td></tr>";
						s += "<tr><td>Website:</td><td style='width: 100%'><a style='width: 100%' href=\"" + webTemplateFile.website + "\">" + limitTo50(webTemplateFile.website) + "</a></td></tr>";
						assert selectedFile.getUrl() != null;
						s += "<tr><td>URL:</td><td style='width: 100%'><a style='width: 100%' href=\"" + selectedFile.getUrl().toString() + "\">" + limitTo50(selectedFile.getUrl().toString()) + "</a></td></tr>";
						s += "<tr><tds>Description:</td><td style='width: 100%'>" + webTemplateFile.description + "</td></tr></table>";
						templatesFileInfoLabel.setText(s);
					} else {
						templatesFileInfoLabel.setText("");
					}
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
			.setMoveDownAction(e -> checkboxTree.moveDownSelectedFiles())
			.setMoveUpActionUpdater(e -> checkboxTree.canMoveSelectedFiles())
			.setMoveUpAction(e -> checkboxTree.moveUpSelectedFiles())
			.addExtraAction(new AnAction("Help", null, AllIcons.Actions.Help) {
				@Override
				public void actionPerformed(@NotNull AnActionEvent event) {
					showHelpDialog(event.getProject());
				}
			})
			.createPanel());

		treeContainer.setLayout(new BorderLayout());
		treeContainer.add(panel);
	}

	private String limitTo50(String string) {
		if (string.length() > 50) {
			return string.substring(0, 48) + "...";
		} else {
			return string;
		}
	}

	private void showHelpDialog(Project project) {
		val dialog = new SettingsHelpDialog(project);
		dialog.show();
	}

	JComponent getComponent() {
		GuiUtils.replaceJSplitPaneWithIDEASplitter(mainPanel);

		emptyLambdaRadioButton.addActionListener(e -> changeLambdaStyle(false));
		varLambdaRadioButton.addActionListener(e -> changeLambdaStyle(true));

		updateNowButton.addActionListener(e -> askForUpdatingTemplateFilesNow());
		testErrorReportButton.addActionListener(e -> testErrorReport());

		templatesFileInfoLabel.addHyperlinkListener(e -> {
			if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
				BrowserUtil.browse(e.getURL());
			}
		});

		new ButtonGroup() {{
			add(emptyLambdaRadioButton);
			add(varLambdaRadioButton);
		}};

		if (checkboxTree == null) {
			createTree();
		}

		return mainPanel;
	}

	private void testErrorReport() {
		throw new RuntimeException("Test");
	}

	private void askForUpdatingTemplateFilesNow() {
		val project = CptUtil.findProject(mainPanel);

		val oldSettings     = CptApplicationSettings.getInstance().getPluginSettings();
		val currentSettings = getPluginSettings();

		if (!oldSettings.equals(currentSettings)) {
			val builder = new DialogBuilder().title("Unsaved Changes").centerPanel(
				new JLabel("Please apply the unsaved changes before running the update.")
			);
			builder.removeAllActions();
			builder.addOkAction();
			builder.showModal(true);
		} else {
			updateTemplateFilesNow(project, currentSettings);
		}
	}

	private void updateTemplateFilesNow(Project project, CptPluginSettings currentSettings) {
		CptUpdateUtils.checkForWebTemplateUpdates(project, true, () -> {
			fillTree(currentSettings.getLangName2virtualFiles(), currentSettings.isActivateNewWebTemplateFilesAutomatically());

			ApplicationManager.getApplication().invokeLater(() -> {
				val builder = new DialogBuilder().title("Update Successful").centerPanel(
					new JLabel("The templates have been successfully updated.")
				);
				builder.removeAllActions();
				builder.addOkAction();
				builder.show();
			});
		});
	}

	/**
	 * Fills the checkboxTree with template files.
	 *
	 * @param langName2virtualFile maps a language name to its VFile directory containing the template files
	 * @param activateNewFiles     if new template files shall be activated
	 */
	private void fillTree(Map<String, List<CptPluginSettings.VFile>> langName2virtualFile, boolean activateNewFiles) {
		assert checkboxTree != null;

		// to map a CptLang to its template files
		Map<CptLang, List<CptPluginSettings.VFile>> lang2file = new HashMap<>();

		for (CptLang lang : SupportedLanguages.supportedLanguages) {
			// add files from saved settings
			val cptFiles        = new ArrayList<>(langName2virtualFile.getOrDefault(lang.getLanguage(), _List()));
			val filesFromConfig = cptFiles.stream().map(f -> f.getFile()).collect(Collectors.toSet());

			// add files from filesystem that are not already in the settings
			val templateFilesFromDir = CptUtil.getTemplateFilesFromLanguageDir(lang.getLanguage());
			Arrays.stream(templateFilesFromDir).filter(f -> !filesFromConfig.contains(f.getAbsolutePath())).forEach(file -> {
				cptFiles.add(new CptPluginSettings.VFile(true, null, null, file.getAbsolutePath()));
			});

			lang2file.put(lang, cptFiles);
		}

		checkboxTree.initTree(lang2file, activateNewFiles);
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
		setToolTipRecursively(templatesEditor.getComponent(), "This editor is read-only.  To edit templates, close the settings dialog and press shift+alt+P in a normal IDEA editor tab.");
		templatesEditorPanel.add(templatesEditor.getComponent(), BorderLayout.CENTER);
	}

	public static void setToolTipRecursively(JComponent c, String text) {
		c.setToolTipText(text);

		for (Component cc : c.getComponents()) {
			if (cc instanceof JComponent) {
				setToolTipRecursively((JComponent) cc, text);
			}
		}
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
		EditorFactory editorFactory  = EditorFactory.getInstance();
		Document      editorDocument = editorFactory.createDocument("");
		return editorFactory.createEditor(editorDocument, null, CptFileType.INSTANCE, true);
	}

	@Override
	public void setPluginSettings(@NotNull CptPluginSettings settings) {
		automaticUpdatesCheckBox.setSelected(settings.isUpdateWebTemplatesAutomatically());
		activateNewWebTemplatesCheckBox.setSelected(settings.isActivateNewWebTemplateFilesAutomatically());

		changeLambdaStyle(settings.isVarLambdaStyle());

		fillTree(settings.getLangName2virtualFiles(), settings.isActivateNewWebTemplateFilesAutomatically());
	}

	@NotNull
	@Override
	public CptPluginSettings getPluginSettings() {
		assert checkboxTree != null;
		lastTreeState = checkboxTree.getState();
		val langName2virtualFile = checkboxTree.getExport();

		return new CptPluginSettings(
			varLambdaRadioButton.isSelected(),
			automaticUpdatesCheckBox.isSelected(),
			activateNewWebTemplatesCheckBox.isSelected(),
			2,
			langName2virtualFile);
	}

	@Override
	public void dispose() {
		if (templatesEditor != null && !templatesEditor.isDisposed()) {
			EditorFactory.getInstance().releaseEditor(templatesEditor);
		}
		templatesEditor = null;
	}

}
