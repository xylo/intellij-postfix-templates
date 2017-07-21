package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.JavaCompletionContributor;
import com.intellij.codeInsight.template.postfix.templates.PostfixLiveTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.OrderedSet;
import com.intellij.util.messages.MessageBusConnection;
import de.endrullis.idea.postfixtemplates.settings.CptApplicationSettings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class CustomPostfixTemplateProvider implements PostfixTemplateProvider, CptApplicationSettings.SettingsChangedListener {
	private Set<PostfixTemplate> templates;

	public CustomPostfixTemplateProvider() {
		MessageBusConnection settingsConnection = ApplicationManager.getApplication().getMessageBus().connect();
		settingsConnection.subscribe(CptApplicationSettings.SettingsChangedListener.TOPIC, this);

		reloadTemplates();
	}

	private void reloadTemplates() {
		String templatesText = CptApplicationSettings.getInstance().getPluginSettings().getTemplatesText();
		templates = loadTemplatesFrom(templatesText);
	}

	private Set<PostfixTemplate> loadTemplatesFrom(String templatesText) {
		Set<PostfixTemplate> templates = new OrderedSet<>();

		try (BufferedReader reader = new BufferedReader(new StringReader(templatesText))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty() && !line.startsWith("//")) {
					String[] split = line.split("→");
					if (split.length == 4) {
						templates.add(new CustomStringPostfixTemplate(split[0].trim(), split[1].trim(), split[2].trim(), split[3].trim()));
					}
				}
			}
		} catch (IOException ignored) {
		}

		return combineTemplatesWithSameName(templates);
	}

	private Set<PostfixTemplate> combineTemplatesWithSameName(Set<PostfixTemplate> templates) {
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
				combinedTemplates.add(new CombinedPostfixTemplate(theseTemplates.get(0).getKey(), example, theseTemplates));
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

	@Override
	public void onSettingsChange(@NotNull CptApplicationSettings settings) {
		reloadTemplates();
	}
}
