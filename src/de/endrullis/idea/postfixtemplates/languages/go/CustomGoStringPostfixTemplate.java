package de.endrullis.idea.postfixtemplates.languages.go;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelectorBase;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.util.PsiExpressionTrimRenderer;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static de.endrullis.idea.postfixtemplates.languages.go.GoPostfixTemplatesUtils.*;

/**
 * Custom postfix template for Go.
 */
@SuppressWarnings("WeakerAccess")
public class CustomGoStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<>() {{
		put(GoSpecialType.ANY.name(), e -> true);
		put(GoSpecialType.BOOLEAN.name(), IS_BOOL);
		put(GoSpecialType.INT.name(), IS_INT);
		put(GoSpecialType.INT64.name(), IS_INT64);
		put(GoSpecialType.UINT.name(), IS_UINT);
		put(GoSpecialType.FLOAT32.name(), IS_FLOAT32);
		put(GoSpecialType.FLOAT64.name(), IS_FLOAT64);
		put(GoSpecialType.FLOAT.name(), IS_FLOAT);
		put(GoSpecialType.BYTESLICE.name(), IS_BYTESLICE);
		put(GoSpecialType.ERROR.name(), IS_ERROR);
		put(GoSpecialType.ARRAY.name(), IS_ARRAY);
		put(GoSpecialType.COMPLEX.name(), IS_COMPLEX);
		put(GoSpecialType.NIL.name(), IS_NIL);
		put(GoSpecialType.STRING.name(), IS_STRING);
	}};

	public static @NotNull List<PsiElement> collectExpressions(final PsiFile file,
	                                                           final int offset) {
		val elementAtCaret = file.findElementAt(offset);
		val expressions    = new ArrayList<PsiElement>();

		PsiElement expression = PsiTreeUtil.getParentOfType(elementAtCaret, PsiElement.class);

		while (expression != null && !(expression instanceof PsiFile) && expression.getTextRange().getEndOffset() == elementAtCaret.getTextRange().getEndOffset()) {
			final PsiElement finalExpression = expression;

			if (expression.getPrevSibling() == null || expression.getPrevSibling().getNode().getElementType() == TokenType.WHITE_SPACE) {
				if (expressions.stream().noneMatch(pe -> finalExpression.getTextRange().equals(pe.getTextRange()))) {
					expressions.add(expression);
				}
			}

			expression = expression.getParent();
		}

		// TODO: For an unknown reason this code completion works only with a single expression and not with multiple ones.
		// TODO: Therefore we have to cut our list to a singleton list.
		if (expressions.isEmpty()) {
			return expressions;
		}
		ArrayList<PsiElement> es = new ArrayList<>();
		es.add(expressions.get(0));
		return es;
	}

	public CustomGoStringPostfixTemplate(String clazz, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(clazz)));
	}

	public static PostfixTemplateExpressionSelector selectorAllExpressionsWithCurrentOffset(final Condition<PsiElement> additionalFilter) {
		return new PostfixTemplateExpressionSelectorBase(additionalFilter) {
			@Override
			protected List<PsiElement> getNonFilteredExpressions(@NotNull PsiElement context, @NotNull Document document, int offset) {
				return new ArrayList<>(collectExpressions(context.getContainingFile(), Math.max(offset - 1, 0)));
			}

			@NotNull
			@Override
			public List<PsiElement> getExpressions(@NotNull PsiElement context, @NotNull Document document, int offset) {
				if (DumbService.getInstance(context.getProject()).isDumb()) return Collections.emptyList();

				List<PsiElement> expressions = super.getExpressions(context, document, offset);

				if (!expressions.isEmpty()) return expressions;

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

	@NotNull
	public static Condition<PsiElement> getCondition(String clazz) {
		return type2psiCondition.get(clazz);
	}

}
