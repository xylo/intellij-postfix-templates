package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.Variable;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelectorBase;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiExpressionTrimRenderer;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import de.endrullis.idea.postfixtemplates.settings.CustomPostfixTemplates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.parseVariables;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.removeVariableValues;

/**
 * Common abstract class for simple string based postfix templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public abstract class SimpleStringBasedPostfixTemplate extends StringBasedPostfixTemplate implements NavigatableTemplate {

	public static final Set<String> PREDEFINED_VARIABLES = CustomPostfixTemplates.PREDEFINED_VARIABLES;

	protected final String          template;
	protected final Set<MyVariable> variables = new OrderedSet<>();
	protected final PsiElement      psiElement;

	public SimpleStringBasedPostfixTemplate(String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement, PostfixTemplateExpressionSelector selector) {
		super(name.substring(1), name, example, selector, provider);
		this.psiElement = psiElement;

		List<MyVariable> allVariables = parseVariables(template).stream().filter(v -> {
			return !PREDEFINED_VARIABLES.contains(v.getName());
		}).collect(Collectors.toList());

		this.template = removeVariableValues(template, allVariables);

		// filter out variable duplicates
		Set<String> foundVarNames = new HashSet<>();
		for (MyVariable variable : allVariables) {
			if (!foundVarNames.contains(variable.getName())) {
				variables.add(variable);
				foundVarNames.add(variable.getName());
			}
		}
	}

	@Override
	protected PsiElement getElementToRemove(PsiElement expr) {
		return expr;
	}

	@Override
	public void setVariables(@NotNull Template template, @NotNull PsiElement psiElement) {
		super.setVariables(template, psiElement);

		List<MyVariable> sortedVars = variables.stream().sorted(Comparator.comparing(s -> s.getNo())).collect(Collectors.toList());

		for (Variable variable : sortedVars) {
			template.addVariable(variable.getName(), variable.getExpression(), variable.getDefaultValueExpression(),
				variable.isAlwaysStopAt(), variable.skipOnStart());
		}
	}

	@Nullable
	@Override
	public String getTemplateString(@NotNull PsiElement element) {
		return template;
	}

	@Override
	public PsiElement getNavigationElement() {
		return psiElement;
	}

	public static List<PsiElement> collectExpressions(final PsiFile file,
	                                                  final Document document,
	                                                  final int offset,
	                                                  boolean acceptVoid) {
		CharSequence text = document.getCharsSequence();
		int correctedOffset = offset;
		int textLength = document.getTextLength();
		if (offset >= textLength) {
			correctedOffset = textLength - 1;
		} else if (!Character.isJavaIdentifierPart(text.charAt(offset))) {
			correctedOffset--;
		}
		if (correctedOffset < 0) {
			correctedOffset = offset;
		} else if (!Character.isJavaIdentifierPart(text.charAt(correctedOffset))) {
			if (text.charAt(correctedOffset) == ';') {//initially caret on the end of line
				correctedOffset--;
			}
			if (correctedOffset < 0 || text.charAt(correctedOffset) != ')') {
				correctedOffset = offset;
			}
		}
		final PsiElement elementAtCaret = file.findElementAt(correctedOffset);
		final List<PsiElement> expressions = new ArrayList<>();

		PsiElement expression = PsiTreeUtil.getParentOfType(elementAtCaret, PsiElement.class);

		while (expression != null && !(expression instanceof PsiFile) && expression.getTextRange().getEndOffset() == elementAtCaret.getTextRange().getEndOffset()) {
			//System.out.println(expression + " - " + expression.getText() + " - " + expression.getTextRange());
			final PsiElement finalExpression = expression;

			if (expression.getPrevSibling() == null || expression.getPrevSibling().getNode().getElementType() == TokenType.WHITE_SPACE) {
				if (expressions.stream().noneMatch(pe -> finalExpression.getTextRange().equals(pe.getTextRange()))) {
					expressions.add(expression);
				}
			} else {
				//System.out.println("prevSilbing: " + expression.getPrevSibling().getNode().getElementType());
			}

			//expression = PsiTreeUtil.getParentOfType(expression, KtExpression.class);
			expression = expression.getParent();
		}

		// TODO: For an unknown reason this code completion works only with a single expression and not with multiple ones.
		// TODO: Therefore we have to cut our list to a singleton list.
		if (expressions.isEmpty()) {
			return expressions;
		}
		ArrayList<PsiElement> es = new ArrayList<>();
		es.add(expressions.get(0));
		//es.add(expressions.get(expressions.size()-1));
		return es;
	}

	public static PostfixTemplateExpressionSelector selectorAllExpressionsWithCurrentOffset(final Condition<PsiElement> additionalFilter) {
		return new PostfixTemplateExpressionSelectorBase(additionalFilter) {
			@Override
			protected List<PsiElement> getNonFilteredExpressions(@NotNull PsiElement context, @NotNull Document document, int offset) {
				return ContainerUtil.newArrayList(collectExpressions(context.getContainingFile(), document, Math.max(offset - 1, 0), false));
			}

			@NotNull
			@Override
			public List<PsiElement> getExpressions(@NotNull PsiElement context, @NotNull Document document, int offset) {
				if (DumbService.getInstance(context.getProject()).isDumb()) return Collections.emptyList();

				List<PsiElement> expressions = super.getExpressions(context, document, offset);

				for (PsiElement expression : expressions) {
					//System.out.println(expression);
				}

				if (!expressions.isEmpty()) return expressions;

				//final PsiExpression topmostExpression = getTopmostExpression(context);
				final PsiExpression topmostExpression = null;
				return ContainerUtil.filter(ContainerUtil.<PsiElement>createMaybeSingletonList(topmostExpression), getFilters(offset));
			}

			@NotNull
			@Override
			public Function<PsiElement, String> getRenderer() {
				return element -> {
					assert element instanceof PsiExpression;
					
					return (new PsiExpressionTrimRenderer.RenderFunction()).fun((PsiExpression) element);
				};
			}
		};
	}

}
