package de.endrullis.idea.postfixtemplates.languages.groovy;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelectorBase;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression;

import java.util.*;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.getTopmostExpression;
import static de.endrullis.idea.postfixtemplates.languages.groovy.GroovyPostfixTemplatesUtils.*;
import static de.endrullis.idea.postfixtemplates.languages.java.CustomJavaStringPostfixTemplate.withProjectClassCondition;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Custom postfix template for Groovy.
 */
@SuppressWarnings("WeakerAccess")
public class CustomGroovyStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	/** Contains predefined type-to-psiCondition mappings as well as cached mappings for individual types. */
	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<>() {{
		put(GroovyType.ANY.name(), e -> true);
		//put(GroovyType.VOID.name(), IS_VOID);
		//put(GroovyType.NON_VOID.name(), IS_NON_VOID);
		put(GroovyType.ARRAY.name(), GroovyPostfixTemplatesUtils.IS_ARRAY);
		put(GroovyType.BOOLEAN.name(), e -> e instanceof GrExpression && isCustomClass(((GrExpression) e).getType(), "java.lang.Boolean"));
		put(GroovyType.ITERABLE_OR_ARRAY.name(), GroovyPostfixTemplatesUtils.IS_ITERABLE_OR_ARRAY);
		//put(GroovyType.NOT_PRIMITIVE.name(), IS_NOT_PRIMITIVE);
		put(GroovyType.NUMBER.name(), IS_DECIMAL_NUMBER);
		put(GroovyType.BYTE.name(), isCertainNumberType(PsiTypes.byteType()));
		put(GroovyType.SHORT.name(), isCertainNumberType(PsiTypes.shortType()));
		put(GroovyType.CHAR.name(), isCertainNumberType(PsiTypes.charType()));
		put(GroovyType.INT.name(), isCertainNumberType(PsiTypes.intType()));
		put(GroovyType.LONG.name(), isCertainNumberType(PsiTypes.longType()));
		put(GroovyType.FLOAT.name(), isCertainNumberType(PsiTypes.floatType()));
		put(GroovyType.DOUBLE.name(), isCertainNumberType(PsiTypes.doubleType()));
		//put(GroovyType.BYTE_LITERAL.name(), isCertainNumberLiteral(PsiType.BYTE));
		//put(GroovyType.SHORT_LITERAL.name(), isCertainNumberLiteral(PsiType.SHORT));
		//put(GroovyType.CHAR_LITERAL.name(), isCertainNumberLiteral(PsiType.CHAR));
		//put(GroovyType.INT_LITERAL.name(), isCertainNumberLiteral(PsiType.INT));
		//put(GroovyType.LONG_LITERAL.name(), isCertainNumberLiteral(PsiType.LONG));
		//put(GroovyType.FLOAT_LITERAL.name(), isCertainNumberLiteral(PsiType.FLOAT));
		//put(GroovyType.DOUBLE_LITERAL.name(), isCertainNumberLiteral(PsiType.DOUBLE));
		//put(GroovyType.NUMBER_LITERAL.name(), IS_DECIMAL_NUMBER_LITERAL);
		//put(GroovyType.STRING_LITERAL.name(), STRING_LITERAL);
		put(GroovyType.CLASS.name(), e -> e instanceof GrExpression && isCustomClass(((GrExpression) e).getType(), "java.lang.Class"));
	}};

	private static final Set<IElementType> delimiterTokens = _Set(
		TokenType.WHITE_SPACE,
		GroovyElementTypes.NL,
		GroovyElementTypes.T_LBRACE,
		GroovyElementTypes.T_LBRACK,
		GroovyElementTypes.T_COMMA,
		GroovyElementTypes.T_COLON,
		GroovyElementTypes.T_LPAREN
	);

	public static List<PsiElement> collectExpressions(final PsiFile file,
	                                                  final Document document,
	                                                  final int offset,
	                                                  boolean acceptVoid) {
		val text            = document.getCharsSequence();
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

		while (expression != null && expression.getTextRange().getEndOffset() == elementAtCaret.getTextRange().getEndOffset()) {
			//System.out.println(expression + " - " + expression.getText() + " - " + expression.getTextRange());
			final PsiElement finalExpression = expression;

			if (expression.getPrevSibling() == null || delimiterTokens.contains(expression.getPrevSibling().getNode().getElementType())) {
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
				return new ArrayList<>(collectExpressions(context.getContainingFile(), document, Math.max(offset - 1, 0), false));
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

	public CustomGroovyStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(matchingClass, conditionClass)));
	}

	@NotNull
	public static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		Condition<PsiElement> psiElementCondition = type2psiCondition.get(matchingClass);

		if (psiElementCondition == null) {
			psiElementCondition = GroovyPostfixTemplatesUtils.isCustomClass(matchingClass);
		}

		return withProjectClassCondition(conditionClass, psiElementCondition);
	}

}
