package de.endrullis.idea.postfixtemplates.actions;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.SeparatorComponent;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.endrullis.idea.postfixtemplates.language.CptFileType;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.language.psi.CptTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;

/**
 * @author Stefan Endrullis (endrullis@iat.uni-leipzig.de)
 */
public class EditorTypedHandlerDelegate extends TypedHandlerDelegate {

	@Override
	public @NotNull Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile, @NotNull FileType fileType) {
		val document = editor.getDocument();

		boolean isWebTemplateFile = false;

		//val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);

		//if (psiFile != null) {
			if (fileType.equals(CptFileType.INSTANCE)) {
				val virtualFile = FileDocumentManager.getInstance().getFile(document);

				if (virtualFile != null) {
					val langAndVFile = CptUtil.getLangAndVFile(virtualFile);

					isWebTemplateFile = langAndVFile != null && langAndVFile._2.id != null;
				}
				//WriteCommandAction.runWriteCommandAction(project, () -> document.insertString(0, "Typed\n"));
			}
		//}

		if (isWebTemplateFile) {
			val offset = editor.getCaretModel().getOffset();

			val element = psiFile.findElementAt(offset);
			eventuallyOpenFileEditDialog(document, project, element, true);

			return Result.STOP;
		} else {
			return Result.DEFAULT;
		}
	}

	public static void eventuallyOpenFileEditDialog(Document document, Project project, PsiElement element, boolean userTriedToEditFile) {
		while (element != null && !(element instanceof CptMapping) && !(element instanceof CptTemplate)) {
			element = element.getParent();
		}

		val vFile = Objects.requireNonNull(FileDocumentManager.getInstance().getFile(document));
		val language = CptUtil.getLanguageOfTemplateFile(vFile);

		if (language != null && element != null) {
			if (element instanceof CptMapping cptMapping) {
				CptTemplate cptTemplate = (CptTemplate) cptMapping.getParent().getParent();
				openFileEditDialog(project, language.getLanguage(), vFile, cptTemplate, cptMapping, userTriedToEditFile);
			}
		}
	}

	private static void openFileEditDialog(Project project, String language, VirtualFile originalFile, CptTemplate cptTemplate, CptMapping cptMapping, boolean userTriedToEditFile) {
		val editableTemplateFiles = new ArrayList<FileWrapper>();
		for (File file : CptUtil.getTemplateFiles(language)) {
			if (file.getName().equals(originalFile.getName())) {
				break;
			}
			val langAndVFile = CptUtil.getLangAndVFile(Objects.requireNonNull(LocalFileSystem.getInstance().findFileByIoFile(file)));
			if (langAndVFile != null && langAndVFile._2.id == null) {
				editableTemplateFiles.add(new FileWrapper(file));
			}
		}

		if (editableTemplateFiles.isEmpty()) {
			val builder = new DialogBuilder().title("Override Template Rule").centerPanel(
				new VerticalBox() {{
					if (userTriedToEditFile) {
						add(new JLabel("Web template files cannot be edited directly."));
						add(new JLabel("But you can override templates from web template files in your own user template files that are loaded before."));
						add(new JLabel("<html>To do so, please create a template file in the settings dialog and put it somewhere above the file <i/>" + originalFile.getName().replace(".postfixTemplates", "") + "</i>"));
						add(new JLabel("<html>or use the button <i>Create new user template file directly</i>."));
						add(new JLabel("<html>Afterwards, go to the template rule you want to override again, press <i>Alt+Enter</i>, and choose <i>Override template rule</i>."));
					} else {
						add(new JLabel("You can override templates from web template files in your own user template files that are loaded before."));
						add(new JLabel("<html>To do so, please create a template file in the settings dialog and put it somewhere above the file <i/>" + originalFile.getName().replace(".postfixTemplates", "") + "</i>"));
						add(new JLabel("<html>or use the button <i>Create new user template file directly</i>."));
						add(new JLabel("<html>Afterwards, go to the template rule you want to override again, press <i>Alt+Enter</i>, and choose <i>Override template rule</i>."));
					}
				}}
			);
			builder.addAction(new TextAction("Open settings dialog") {
				@Override
				public void actionPerformed(ActionEvent e) {
					builder.getDialogWrapper().close(DialogWrapper.CLOSE_EXIT_CODE);
					CptUtil.openPluginSettings(project);
				}
			});
			builder.addOkAction().setText("Create new user template file directly");
			builder.addCancelAction();

			ApplicationManager.getApplication().invokeLater(() -> {
				if (builder.show() == DialogWrapper.OK_EXIT_CODE) {
					CptUtil.createTopPrioUserTemplateFile(language, "overriddenTemplates");
					final File templateFile = CptUtil.getTemplateFile(language, "overriddenTemplates");
					LocalFileSystem.getInstance().refreshIoFiles(_List(templateFile));

					val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(templateFile);
					if (virtualFile != null) {
						addTemplateRuleToFile(project, cptTemplate, cptMapping, virtualFile);
					}

					CptUtil.openFileInEditor(project, templateFile);
				}
			});
		} else {
			final ComboBox<FileWrapper> fileComboBox = new ComboBox<>(editableTemplateFiles.toArray(new FileWrapper[0]));

			val builder = new DialogBuilder().title("Override Template Rule").centerPanel(
				new VerticalBox() {{
					if (userTriedToEditFile) {
						add(new JLabel("Web template files cannot be edited directly."));
						add(new JLabel("But you can override templates from web template files in your own user template files that are loaded before."));
						add(new SeparatorComponent(6, UIUtil.getPanelBackground(), UIUtil.getPanelBackground()));
					} else {
						//add(new JLabel("You can override this template rule by putting your own rule in a user template file"));
						//add(new JLabel("that is loaded before this web template file (" + cptTemplate.getTemplateName() + ")."));
					}
					add(new JLabel("Please choose the template file in which you want to override the template rule."));
					add(new SeparatorComponent(6, UIUtil.getPanelBackground(), UIUtil.getPanelBackground()));
					add(new BorderLayoutPanel() {{
						add(new JLabel("Template file to edit: "), BorderLayout.WEST);
						add(fileComboBox, BorderLayout.CENTER);
					}});
				}}
			);
			builder.setPreferredFocusComponent(fileComboBox);
			builder.addOkAction().setText("Edit template file");
			builder.addCancelAction();

			ApplicationManager.getApplication().invokeLater(() -> {
				if (builder.show() == DialogWrapper.OK_EXIT_CODE) {
					FileWrapper selectedFileWrapper = (FileWrapper) fileComboBox.getSelectedItem();

					if (selectedFileWrapper != null) {
						val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(selectedFileWrapper.file);
						if (virtualFile != null) {
							addTemplateRuleToFile(project, cptTemplate, cptMapping, virtualFile);
						}
					}
				}
			});
		}
	}

	private static void addTemplateRuleToFile(Project project, CptTemplate cptTemplate, CptMapping cptMapping, VirtualFile templateFileToEdit) {
		ApplicationManager.getApplication().invokeLater(() -> {
			CptUtil.openFileInEditor(project, templateFileToEdit);

			// find insertion offset in editable template file (use template offset if template already exists)
			AtomicInteger offset = new AtomicInteger(-1);
			CptUtil.processTemplates(project, templateFileToEdit, (thatCptTemplate, thatCptMapping) -> {
				if (cptTemplate.getTemplateName().equals(thatCptTemplate.getTemplateName())) {
					offset.set(thatCptMapping.getTextRange().getEndOffset());
				}
			});

			val psiFile = Objects.requireNonNull(PsiManager.getInstance(project).findFile(templateFileToEdit));
			val document = Objects.requireNonNull(PsiDocumentManager.getInstance(project).getDocument(psiFile));

			// compose template/mapping to insert
			String textToInsert = "";
			if (offset.get() == -1) {
				textToInsert = "\n" + cptTemplate.getTemplateName() + " : " + cptTemplate.getTemplateDescription();
				offset.set(document.getTextLength());
			}
			textToInsert += "\n\t" + cptMapping.getMatchingClassName();

			if (cptMapping.getConditionClassName() != null) {
				textToInsert += " [" + cptMapping.getConditionClassName() + "]";
			}
			textToInsert += "  →  " + cptMapping.getReplacementString();

			String finalTextToInsert = textToInsert;

			WriteCommandAction.runWriteCommandAction(project, () -> {
				// insert template/mapping
				document.insertString(offset.get(), finalTextToInsert);
				PsiDocumentManager.getInstance(project).commitDocument(document);
				val editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

				if (editor != null) {
					// move caret to newly inserted template/mapping
					CptUtil.processTemplates(project, templateFileToEdit, (thatCptTemplate, thatCptMapping) -> {
						if (cptTemplate.getTemplateName().equals(thatCptTemplate.getTemplateName())
							&& cptMapping.getMatchingClassName().equals(thatCptMapping.getMatchingClassName())) {
							final int textOffset = thatCptMapping.getTextRange().getStartOffset();
							editor.getCaretModel().moveToOffset(textOffset);
						}
					});
				}
			});
		}, ModalityState.nonModal());
	}

	@Data
	@AllArgsConstructor
	static class FileWrapper {
		public File file;

		@Override
		public String toString() {
			return file.getName().replace(".postfixTemplates", "");
		}
	}

}
