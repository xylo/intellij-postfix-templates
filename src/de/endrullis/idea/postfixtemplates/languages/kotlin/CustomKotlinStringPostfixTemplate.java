package de.endrullis.idea.postfixtemplates.languages.kotlin;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;
import org.jetbrains.kotlin.analysis.api.types.KaClassType;
import org.jetbrains.kotlin.idea.codeInsight.postfix.ExpressionTypeFilter;
import org.jetbrains.kotlin.idea.codeInsight.postfix.KotlinPostfixTemplateKt;
import org.jetbrains.kotlin.psi.KtConstantExpression;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.psi.KtNameReferenceExpression;
import org.jetbrains.kotlin.psi.KtStringTemplateExpression;

import java.util.HashMap;
import java.util.Map;


/**
 * Custom postfix template for Kotlin.
 */
@SuppressWarnings("WeakerAccess")
public class CustomKotlinStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<>() {{
		put(SpecialType.ANY.name(), e -> e instanceof KtExpression);
		put(SpecialType.STRING_LITERAL.name(), e -> e instanceof KtStringTemplateExpression);
		put(SpecialType.FLOAT_LITERAL.name(), e -> e instanceof KtConstantExpression && ((KtConstantExpression) e).getNode().getElementType() == KtNodeTypes.FLOAT_CONSTANT);
		put(SpecialType.INT_LITERAL.name(), e -> e instanceof KtConstantExpression && ((KtConstantExpression) e).getNode().getElementType() == KtNodeTypes.INTEGER_CONSTANT);
		put(SpecialType.CHAR_LITERAL.name(), e -> e instanceof KtConstantExpression && ((KtConstantExpression) e).getNode().getElementType() == KtNodeTypes.CHARACTER_CONSTANT);
	}};

	@SuppressWarnings("unchecked")
	public CustomKotlinStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, KotlinPostfixTemplateKt.allExpressions());
	}

	@NotNull
	public static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		final Condition<PsiElement> psiElementCondition = type2psiCondition.get(matchingClass);

		if (psiElementCondition != null) {
			return psiElementCondition;
		} else {
			return psiElement -> classMatches(matchingClass, psiElement);
		}

		/*
		if (conditionClass == null) {
			return psiElementCondition;
		} else {
			final Condition<PsiElement> finalPsiElementCondition = psiElementCondition;

			return psiElement -> {
				if (finalPsiElementCondition.value(psiElement)) {
					final Project project = psiElement.getProject();
					PsiFile psiFile = psiElement.getContainingFile().getOriginalFile();
					VirtualFile virtualFile = psiFile.getVirtualFile();
					Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
					assert module != null;
					return JavaPsiFacade.getInstance(project).findClass(conditionClass, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true)) != null;
				} else {
					return false;
				}
			};
		}
		return psiElement -> {
			if (psiElement instanceof KtExpression) {
				/*
				final KtExpression ktExpression = (KtExpression) psiElement;
				final BindingContext bindingContext = analyze(ktExpression, BodyResolveMode.PARTIAL);
				final KotlinTypeInfo typeInfo = bindingContext.get(BindingContext.EXPRESSION_TYPE_INFO, ktExpression);
				final KotlinType type = typeInfo.getType();
				System.out.println(type);
				System.out.println(type.getMemberScope().toString());
				*/
				//bindingContext[BindingContext.EXPRESSION_TYPE_INFO, element];
				//val expressionType = element.getType(bindingContext);
				//return true;
			//}
			//return false;
		//};
	}

	private static boolean classMatches(String matchingClass, PsiElement psiElement) {
		if (psiElement instanceof final KtNameReferenceExpression ktRef) {
			final ExpressionTypeFilter filter = new ExpressionTypeFilter((session, type) ->
				type instanceof KaClassType classType
					&& matchingClass.equals(classType.getClassId().asFqNameString())
			);
			return filter.invoke(ktRef);
		}

		return false;
	}

}
