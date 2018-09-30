package de.endrullis.idea.postfixtemplates.languages.kotlin;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService;
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.resolve.BindingContext;
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Custom postfix template for Kotlin.
 */
@SuppressWarnings("WeakerAccess")
public class CustomKotlinStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	public static final Set<String> PREDEFINED_VARIABLES = _Set("expr", "END");

	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
		put(SpecialType.ANY.name(), e -> true);
	}};

	public CustomKotlinStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(matchingClass, conditionClass)));
	}

	@Override
	protected PsiElement getElementToRemove(PsiElement expr) {
		return expr;
	}

	@NotNull
	public static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		/*
		Condition<PsiElement> psiElementCondition = type2psiCondition.get(matchingClass);

		if (psiElementCondition == null) {
			psiElementCondition = MyJavaPostfixTemplatesUtils.isCustomClass(matchingClass);
		}

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
		*/
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
				return true;
			}
			return false;
		};
	}

	private static BindingContext analyze(KtExpression ktExpression, BodyResolveMode bodyResolveMode) {
		final ResolutionFacade resolutionFacade = KotlinCacheService.Companion.getInstance(ktExpression.getProject()).getResolutionFacade(Collections.singletonList(ktExpression));
		return resolutionFacade.analyze(ktExpression, bodyResolveMode);
	}

}
