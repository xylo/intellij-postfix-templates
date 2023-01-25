package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupActionProvider;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementAction;
import com.intellij.codeInsight.template.postfix.completion.PostfixTemplateLookupElement;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.util.Consumer;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link LookupActionProvider} allows you to jump directly to a postfix template definition.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptLookupActionProvider implements LookupActionProvider {

	@Override
	public void fillActions(@NotNull LookupElement element, @NotNull Lookup lookup, @NotNull Consumer<? super @NotNull LookupElementAction> consumer) {
		if (element instanceof final PostfixTemplateLookupElement templateLookupElement) {
			final PostfixTemplate template = templateLookupElement.getPostfixTemplate();

			if (template instanceof Navigatable && ((Navigatable) template).canNavigate()) {
				consumer.consume(new LookupElementAction(PlatformIcons.EDIT, "Edit '" + template.getKey() + "' template") {
					@Override
					public Result performLookupAction() {
						final Project project = lookup.getProject();
						ApplicationManager.getApplication().invokeLater(() -> {
							if (project.isDisposed()) return;

							((Navigatable) template).navigate(true);
						});
						return Result.HIDE_LOOKUP;
					}
				});
			}
		}
	}

}
