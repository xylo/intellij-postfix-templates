package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.codeInsight.template.postfix.templates.PostfixLiveTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileContentsChangedAdapter;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.OrderedSet;
import com.intellij.util.messages.MessageBusConnection;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.settings.CptApplicationSettings;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.language.CptUtil.processTemplates;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.processEscapes;
import static java.util.stream.Collectors.toList;

public abstract class CustomPostfixTemplateProvider implements PostfixTemplateProvider, CptApplicationSettings.SettingsChangedListener {
	private Set<PostfixTemplate> templates;

	/**
	 * Template file change listener.
	 */
	// TODO remove this code if VirtualFileListener is able to replace this code on all platforms
	private final FileDocumentManagerListener templateFileChangeListener = new FileDocumentManagerListener() {
		@Override
		public void beforeDocumentSaving(@NotNull Document d) {
			VirtualFile vFile = FileDocumentManager.getInstance().getFile(d);
			if (vFile != null && CptUtil.isTemplateFile(vFile, getLanguage())) {
				reloadTemplates();
			}
		}
	};

	protected CustomPostfixTemplateProvider() {
		// listen to file changes of template files
		LocalFileSystem.getInstance().addRootToWatch(CptUtil.getTemplatesPath().getAbsolutePath(), true);
		LocalFileSystem.getInstance().addVirtualFileListener(new VirtualFileContentsChangedAdapter() {
			@Override
			protected void onFileChange(@NotNull VirtualFile vFile) {
				if (CptUtil.isTemplateFile(vFile, getLanguage())) {
					reloadTemplates();
				}
			}

			@Override
			protected void onBeforeFileChange(@NotNull VirtualFile virtualFile) {
			}
		});

		// listen to settings changes
		MessageBusConnection messageBus = ApplicationManager.getApplication().getMessageBus().connect();
		messageBus.subscribe(CptApplicationSettings.SettingsChangedListener.TOPIC, this);

		// listen to file changes of template file
		messageBus.subscribe(FileDocumentManagerListener.TOPIC, templateFileChangeListener);

		// load templates
		reload(CptApplicationSettings.getInstance());
	}

	/**
	 * Reloads the templates.
	 *
	 * @param settings current application settings
	 */
	private void reload(CptApplicationSettings settings) {
		reloadTemplates();
	}

	/**
	 * Reloads templates from file system.
	 */
	@Override
	public void reloadTemplates() {
		templates = loadTemplatesFromFiles(CptUtil.getTemplateFiles(getLanguage()));
		//PostfixTemplateStorage.getInstance().setTemplates(this, templates);
	}

	@NotNull
	protected abstract String getLanguage();

	/**
	 * Loads the postfix templates from the given file and returns them.
	 *
	 * @param files templates files
	 * @return set of postfix templates
	 */
	@SuppressWarnings("WeakerAccess")
	public Set<PostfixTemplate> loadTemplatesFromFiles(@NotNull List<File> files) {
		val vFiles = files.stream().map(f -> LocalFileSystem.getInstance().findFileByIoFile(f)).filter(f -> f != null).collect(Collectors.toList());

		return loadTemplatesFromVFiles(vFiles);
	}

	public String getPluginClassName() {
		return null;
	}

	/**
	 * Loads the postfix templates from the given virtual file and returns them.
	 *
	 * @param vFiles virtual templates files
	 * @return set of postfix templates
	 */
	@SuppressWarnings("WeakerAccess")
	public Set<PostfixTemplate> loadTemplatesFromVFiles(@NotNull List<VirtualFile> vFiles) {
		try {
			// load templates only if the plugin is activated
			if (getPluginClassName() != null) {
				Class.forName(getPluginClassName());
			}
		} catch (ClassNotFoundException e) {
			return new HashSet<>();
		}

		List<PostfixTemplate> templates = new ArrayList<>();

		ApplicationManager.getApplication().runReadAction(() -> {
			Project[] projects = ProjectManager.getInstance().getOpenProjects();

			if (projects.length == 0) {
				return;
			}
			Project project = projects[0];

			for (VirtualFile vFile : vFiles) {
				processTemplates(project, vFile, (cptTemplate, mapping) -> {
					StringBuilder sb = new StringBuilder();
					for (PsiElement element : mapping.getReplacement().getChildren()) {
						sb.append(element.getText());
					}

					val template            = processEscapes(sb.toString()).trim();
					val templateDescription = template.equals("[SKIP]") ? "[SKIP]" : cptTemplate.getTemplateDescription();

					templates.add(createTemplate(
						mapping,
						mapping.getMatchingClassName(),
						mapping.getConditionClassName(),
						cptTemplate.getTemplateName(),
						templateDescription,
						template,
						this
					));
				});
			}
		});

		return combineTemplatesWithSameName(templates);
	}

	@NotNull
	protected abstract StringBasedPostfixTemplate createTemplate(CptMapping mapping, String matchingClass, String conditionClass, String templateName, String description, String template, PostfixTemplateProvider provider);

	/**
	 * Combines templates with the same name into a {@link CombinedPostfixTemplate} and returns the result.
	 *
	 * @param templates templates that may have name duplicates
	 * @return (combined) templates
	 */
	private Set<PostfixTemplate> combineTemplatesWithSameName(List<PostfixTemplate> templates) {
		// group templates by name
		Map<String, List<PostfixTemplate>> key2templates = templates.stream().collect(
			Collectors.groupingBy(
				PostfixTemplate::getKey, toList()
			)
		);

		// combine templates with the same name
		Set<PostfixTemplate> combinedTemplates = new OrderedSet<>();
		for (List<PostfixTemplate> theseTemplates : key2templates.values()) {
			if (theseTemplates.size() == 1) {
				combinedTemplates.add(theseTemplates.get(0));
			} else {
				val    examples = theseTemplates.stream().map(t -> t.getExample()).filter(s -> !s.equals("[SKIP]")).distinct().toList();
				String example  = !examples.isEmpty() ? examples.get(0) : "";
				combinedTemplates.add(new CombinedPostfixTemplate(theseTemplates.get(0).getKey(), example, theseTemplates, this));
			}
		}

		return combinedTemplates;
	}

	@NotNull
	@Override
	public Set<PostfixTemplate> getTemplates() {
		return templates;
	}

	@Override
	public boolean isTerminalSymbol(char currentChar) {
		return currentChar == '.' || currentChar == '!';
	}

	@Override
	public void preExpand(@NotNull final PsiFile file, @NotNull final Editor editor) {
		ApplicationManager.getApplication().assertIsDispatchThread();

		if (isSemicolonNeeded(file, editor)) {
			ApplicationManager.getApplication().runWriteAction(() -> CommandProcessor.getInstance().runUndoTransparentAction(
				() -> {
					EditorModificationUtil.insertStringAtCaret(editor, ";", false, false);
					PsiDocumentManager.getInstance(file.getProject()).commitDocument(editor.getDocument());
				}));
		}
	}

	@Override
	public void afterExpand(@NotNull final PsiFile file, @NotNull final Editor editor) {
	}

	@NotNull
	@Override
	public PsiFile preCheck(@NotNull PsiFile copyFile, @NotNull Editor realEditor, int currentOffset) {
		Document document = copyFile.getViewProvider().getDocument();
		assert document != null;
		CharSequence  sequence                 = document.getCharsSequence();
		StringBuilder fileContentWithSemicolon = new StringBuilder(sequence);
		if (isSemicolonNeeded(copyFile, realEditor)) {
			fileContentWithSemicolon.insert(currentOffset, ';');
			return PostfixLiveTemplate.copyFile(copyFile, fileContentWithSemicolon);
		}

		return copyFile;
	}

	protected boolean isSemicolonNeeded(@NotNull PsiFile file, @NotNull Editor editor) {
		return false;
	}

	/**
	 * Called when settings changed.
	 *
	 * @param settings application settings
	 */
	@Override
	public void onSettingsChange(@NotNull CptApplicationSettings settings) {
		reload(settings);
	}

}
