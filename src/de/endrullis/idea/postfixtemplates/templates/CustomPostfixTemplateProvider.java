package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.AppTopics;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.JavaCompletionContributor;
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
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileContentsChangedAdapter;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.OrderedSet;
import com.intellij.util.messages.MessageBusConnection;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.language.psi.CptFile;
import de.endrullis.idea.postfixtemplates.language.psi.CptMapping;
import de.endrullis.idea.postfixtemplates.language.psi.CptTemplate;
import de.endrullis.idea.postfixtemplates.settings.CptApplicationSettings;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.processEscapes;
import static java.util.stream.Collectors.toList;

public abstract class CustomPostfixTemplateProvider implements PostfixTemplateProvider, CptApplicationSettings.SettingsChangedListener {
	private Set<PostfixTemplate> templates;

	/**
	 * Template file change listener.
	 */
	// TODO remove this code if VirtualFileListener is able to replace this code on all platforms
	private FileDocumentManagerListener templateFileChangeListener = new FileDocumentManagerAdapter() {
		@Override
		public void beforeDocumentSaving(@NotNull Document d) {
			VirtualFile vFile = FileDocumentManager.getInstance().getFile(d);
			if (CptUtil.isTemplateFile(vFile, getLanguage())) {
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
		messageBus.subscribe(AppTopics.FILE_DOCUMENT_SYNC, templateFileChangeListener);

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

		// TODO
		//Notification notification = new Notification("CptTemplatesUpdate", "Scala Plugin Update", "message", NotificationType.INFORMATION);
		/*
		, new NotificationListener {
      def hyperlinkUpdate(notification: Notification, event: HyperlinkEvent) {
        notification.expire()
        applicationSettings.ASK_USE_LATEST_PLUGIN_BUILDS = false
        event.getDescription match {
          case "EAP"     => doUpdatePluginHostsAndCheck(EAP)
          case "Nightly" => doUpdatePluginHostsAndCheck(Nightly)
          case "Release" => doUpdatePluginHostsAndCheck(Release)
          case _         => applicationSettings.ASK_USE_LATEST_PLUGIN_BUILDS = true
        }
      }
    })
    */
		//Notifications.Bus.notify(notification);

		List<PostfixTemplate> templates = new ArrayList<>();

		ApplicationManager.getApplication().runReadAction(() -> {
			Project project = ProjectManager.getInstance().getOpenProjects()[0];

			for (VirtualFile vFile : vFiles) {
				CptFile cptFile = (CptFile) PsiManager.getInstance(project).findFile(vFile);
				if (cptFile != null) {
					CptTemplate[] cptTemplates = PsiTreeUtil.getChildrenOfType(cptFile, CptTemplate.class);
					if (cptTemplates != null) {
						for (CptTemplate cptTemplate : cptTemplates) {
							for (CptMapping mapping : cptTemplate.getMappings().getMappingList()) {
								StringBuilder sb = new StringBuilder();
								for (PsiElement element : mapping.getReplacement().getChildren()) {
									sb.append(element.getText());
								}

								templates.add(createTemplate(mapping, mapping.getMatchingClassName(), mapping.getConditionClassName(), cptTemplate.getTemplateName(), cptTemplate.getTemplateDescription(), processEscapes(sb.toString()), this));
							}
						}
					}
				}
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
				String example = templates.stream().distinct().count() > 1 ? theseTemplates.get(0).getExample() : "";
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
		CharSequence sequence = document.getCharsSequence();
		StringBuilder fileContentWithSemicolon = new StringBuilder(sequence);
		if (isSemicolonNeeded(copyFile, realEditor)) {
			fileContentWithSemicolon.insert(currentOffset, ';');
			return PostfixLiveTemplate.copyFile(copyFile, fileContentWithSemicolon);
		}

		return copyFile;
	}

	private static boolean isSemicolonNeeded(@NotNull PsiFile file, @NotNull Editor editor) {
		return JavaCompletionContributor.semicolonNeeded(editor, file, CompletionInitializationContext.calcStartOffset(editor.getCaretModel().getCurrentCaret()));
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
