package de.endrullis.idea.postfixtemplates.languages.javascript;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelectorBase;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.getTopmostExpression;

/**
 * Custom postfix template for JavaScript.
 */
@SuppressWarnings("WeakerAccess")
public class CustomJavaScriptStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
		put(SpecialType.ANY.name(), e -> true);
	}};

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
	  /*
	  for (PsiElement element : statementsInRange) {
     if (element instanceof PsiExpressionStatement) {
       final PsiExpression expression = ((PsiExpressionStatement)element).getExpression();
       if (expression.getType() != PsiType.VOID) {
         expressions.add(expression);
       }
     }
    }*/

		PsiElement expression = PsiTreeUtil.getParentOfType(elementAtCaret, PsiElement.class);

		/*
		while (expression != null) {
			if (!expressions.contains(expression) && !(expression instanceof PsiParenthesizedExpression) && !(expression instanceof PsiSuperExpression) &&
				(acceptVoid || !PsiType.VOID.equals(expression.getType()))) {
				if (expression instanceof PsiMethodReferenceExpression) {
					expressions.add(expression);
				} else if (!(expression instanceof PsiAssignmentExpression)) {
					if (!(expression instanceof PsiReferenceExpression)) {
						expressions.add(expression);
					} else {
						if (!(expression.getParent() instanceof PsiMethodCallExpression)) {
							final PsiElement resolve = ((PsiReferenceExpression) expression).resolve();
							if (!(resolve instanceof PsiClass) && !(resolve instanceof PsiPackage)) {
								expressions.add(expression);
							}
						}
					}
				}
			}
			expression = PsiTreeUtil.getParentOfType(expression, PsiExpression.class);
		}
		*/

		while (expression != null && expression instanceof JSExpression && expression.getTextRange().getEndOffset() == elementAtCaret.getTextRange().getEndOffset()) {
			//System.out.println(expression + " - " + expression.getText() + " - " + expression.getTextRange());
			final PsiElement finalExpression = expression;

			if (expression.getPrevSibling() == null || expression.getPrevSibling().getNode().getElementType() == TokenType.WHITE_SPACE) {
				if (expressions.stream().noneMatch(pe -> finalExpression.getTextRange().equals(pe.getTextRange()))) {
					expressions.add(expression);
				}
			} else {
				System.out.println("prevSilbing: " + expression.getPrevSibling().getNode().getElementType());
			}

			//expression = PsiTreeUtil.getParentOfType(expression, KtExpression.class);
			expression = expression.getParent();
		}

		/*
		while (expression != null && expression.getTextRange().getEndOffset() == elementAtCaret.getTextRange().getEndOffset()) {
			if (expression instanceof JSExpression) {
				final PsiElement finalExpression = expression;
				
				if (expressions.stream().noneMatch(pe -> finalExpression.getTextRange().equals(pe.getTextRange()))) {
					expressions.add(expression);
				}
			}
			expression = expression.getParent();
		}
		*/

		/*
		for (PsiElement psiElement : expressions) {
			System.out.println("parent: " + psiElement + " at " + psiElement.getTextRange());
		}
		*/

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
				return ContainerUtil.newArrayList(collectExpressions(context.getContainingFile(), document,
					Math.max(offset - 1, 0), false));
			}

			@NotNull
			@Override
			public List<PsiElement> getExpressions(@NotNull PsiElement context, @NotNull Document document, int offset) {
				if (DumbService.getInstance(context.getProject()).isDumb()) return Collections.emptyList();

				List<PsiElement> expressions = super.getExpressions(context, document, offset);
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

	public CustomJavaScriptStringPostfixTemplate(String clazz, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(clazz)));
	}

	@NotNull
	public static Condition<PsiElement> getCondition(String clazz) {
		return type2psiCondition.get(clazz);
	}

}
