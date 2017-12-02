package de.endrullis.idea.postfixtemplates.languages.kotlin;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.Variable;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelectorBase;
import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import de.endrullis.idea.postfixtemplates.templates.MyJavaPostfixTemplatesUtils;
import de.endrullis.idea.postfixtemplates.templates.MyVariable;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtExpression;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.getTopmostExpression;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.parseVariables;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.removeVariableValues;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Custom postfix template for Kotlin.
 */
@SuppressWarnings("WeakerAccess")
public class CustomKotlinStringPostfixTemplate extends StringBasedPostfixTemplate {

	public static final Set<String> PREDEFINED_VARIABLES = _Set("expr", "END");

	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
		put(SpecialType.ANY.name(), e -> true);
	}};

	private final String template;
	private final Set<MyVariable> variables = new OrderedSet<>();

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

		KtExpression expression = PsiTreeUtil.getParentOfType(elementAtCaret, KtExpression.class);

		expressions.add(expression);
		// TODO find more relevant parent expressions

		return expressions;
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
					System.out.println(expression);
				}

				if (!expressions.isEmpty()) return expressions;

				return ContainerUtil.filter(ContainerUtil.<PsiElement>createMaybeSingletonList(getTopmostExpression(context)), getFilters(offset));
			}

			@NotNull
			@Override
			public Function<PsiElement, String> getRenderer() {
				return JavaPostfixTemplatesUtils.getRenderer();
			}
		};
	}

	public CustomKotlinStringPostfixTemplate(String clazz, String name, String example, String template) {
		super(name.substring(1), example, selectorAllExpressionsWithCurrentOffset(getCondition(clazz)));

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

	@NotNull
	public static Condition<PsiElement> getCondition(String clazz) {
		Condition<PsiElement> psiElementCondition = type2psiCondition.get(clazz);

		if (psiElementCondition != null) {
			return psiElementCondition;
		} else {
			return MyJavaPostfixTemplatesUtils.isCustomClass(clazz);
		}
	}

	@Nullable
	@Override
	public String getTemplateString(@NotNull PsiElement element) {
		return template;
	}

}
