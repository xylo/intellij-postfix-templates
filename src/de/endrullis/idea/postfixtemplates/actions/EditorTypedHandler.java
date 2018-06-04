package de.endrullis.idea.postfixtemplates.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.panels.VerticalBox;
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
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handler for typed events in editors.
 * <p>
 * This handler prevents that web template files are edited by the user.
 * Instead of editing those files it offers the user to edit the template in another file.
 */
public class EditorTypedHandler implements TypedActionHandler {
	private TypedActionHandler oldHandler;

	public EditorTypedHandler(TypedActionHandler oldHandler) {
		this.oldHandler = oldHandler;
	}

	@Override
	public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
		val document = editor.getDocument();
		val project = editor.getProject();

		boolean isWebTemplateFile = false;

		if (project != null) {
			val fileType = Objects.requireNonNull(PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument())).getFileType();

			if (fileType.equals(CptFileType.INSTANCE)) {
				val virtualFile = FileDocumentManager.getInstance().getFile(document);

				if (virtualFile != null) {
					val langAndVFile = CptUtil.getLangAndVFile(virtualFile);

					isWebTemplateFile = langAndVFile != null && langAndVFile._2.id != null;
				}
				//WriteCommandAction.runWriteCommandAction(project, () -> document.insertString(0, "Typed\n"));
			}
		}

		if (isWebTemplateFile) {
			val offset = editor.getCaretModel().getOffset();
			val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);

			if (psiFile != null) {
				PsiElement element = psiFile.findElementAt(offset);

				while (element != null && !(element instanceof CptMapping) && !(element instanceof CptTemplate)) {
					element = element.getParent();
				}

				val vFile = Objects.requireNonNull(FileDocumentManager.getInstance().getFile(document));
				val language = CptUtil.getLanguageOfTemplateFile(vFile);

				if (language != null && element != null) {
					if (element instanceof CptMapping) {
						CptMapping cptMapping = (CptMapping) element;
						CptTemplate cptTemplate = (CptTemplate) cptMapping.getParent().getParent();
						openFileEditDialog(project, language.getLanguage(), vFile, cptTemplate, cptMapping);
					}
				}
			}
		} else {
			oldHandler.execute(editor, c, dataContext);
		}
	}

	private void openFileEditDialog(Project project, String language, VirtualFile originalFile, CptTemplate cptTemplate, CptMapping cptMapping) {
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
		final ComboBox<FileWrapper> fileComboBox = new ComboBox<>(editableTemplateFiles.toArray(new FileWrapper[0]));

		val builder = new DialogBuilder().title("Edit template").centerPanel(
			new VerticalBox() {{
				add(new JLabel("Web template files cannot be edited directly."));
				add(new JLabel("But you can override the template " + cptTemplate.getTemplateName() + " in your own template file."));
				add(new JLabel("Please choose the template file you want to edit."));
				add(new BorderLayoutPanel() {{
					add(new JLabel("Template file to edit: "), BorderLayout.WEST);
					add(fileComboBox, BorderLayout.CENTER);
				}});
			}}
		);
		builder.addOkAction().setText("Edit template file");
		builder.addCancelAction();

		ApplicationManager.getApplication().invokeLater(() -> {
			if (builder.show() == DialogWrapper.OK_EXIT_CODE) {
				FileWrapper selectedFileWrapper = (FileWrapper) fileComboBox.getSelectedItem();

				if (selectedFileWrapper != null) {
					val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(selectedFileWrapper.file);
					if (virtualFile != null) {
						ApplicationManager.getApplication().invokeLater(() -> {
							CptUtil.openFileInEditor(project, selectedFileWrapper.file);

							// find insertion offset in editable template file (use template offset if template already exists)
							AtomicInteger offset = new AtomicInteger(-1);
							CptUtil.processTemplates(project, virtualFile, (thatCptTemplate, thatCptMapping) -> {
								if (cptTemplate.getTemplateName().equals(thatCptTemplate.getTemplateName())) {
									offset.set(thatCptMapping.getTextRange().getEndOffset());
								}
							});

							val psiFile = Objects.requireNonNull(PsiManager.getInstance(project).findFile(virtualFile));
							val document = Objects.requireNonNull(PsiDocumentManager.getInstance(project).getDocument(psiFile));

							// compose template/mapping to insert
							String textToInsert = "";
							if (offset.get() == -1) {
								textToInsert = "\n" + cptTemplate.getTemplateName() + " : " + cptTemplate.getTemplateDescription();
								offset.set(document.getTextLength());
							}
							textToInsert += "\n" + "  " + cptMapping.getMatchingClassName();

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
									CptUtil.processTemplates(project, virtualFile, (thatCptTemplate, thatCptMapping) -> {
										if (cptTemplate.getTemplateName().equals(thatCptTemplate.getTemplateName())
											&& cptMapping.getMatchingClassName().equals(thatCptMapping.getMatchingClassName())) {
											final int textOffset = thatCptMapping.getTextRange().getStartOffset();
											editor.getCaretModel().moveToOffset(textOffset);
										}
									});
								}
							});
						}, ModalityState.NON_MODAL);
					}
				}
			}
		});
	}

	@Data
	@AllArgsConstructor
	class FileWrapper {
		public File file;

		@Override
		public String toString() {
			return file.getName().replace(".postfixTemplates", "");
		}
	}

}
