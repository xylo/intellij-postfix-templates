package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.completion.AllClassesGetter;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.JavaPsiClassReferenceElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * Completion utils for Java class names.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@UtilityClass
public class CptCompletionUtil {

	public static void addCompletions(@NotNull CompletionParameters params, @NotNull CompletionResultSet resultSet) {
		final PsiElement originalPosition = params.getOriginalPosition();
		final PsiElement originalParent = originalPosition == null ? null : originalPosition.getParent();
		if (originalParent == null) {
			return;
		}
		final Project project = CptUtil.findProject(originalParent);
		/*
		final Module module = ModuleUtilCore.findModuleForPsiElement(originalParent);
		if (module == null) {
			return;
		}
		*/
		final PsiFile containingFile = originalParent.getContainingFile();
		if (containingFile == null) {
			return;
		}
		final String packagePrefix = getPackagePrefix(originalParent, params.getOffset());
		//fillAliases(resultSet, packagePrefix, originalPosition, module, originalParent);
		fillClassNames(resultSet, packagePrefix, project);
		/*
		JavaClassNameCompletionContributor.addAllClasses(params, true, resultSet.getPrefixMatcher(), element -> {
			System.out.println(packagePrefix + "." + element.getLookupString());
			resultSet.addElement(LookupElementBuilder.create(packagePrefix + "." + element.getLookupString()));
		});
		*/
		resultSet.stopHere();
	}

	private static void fillClassNames(@NotNull CompletionResultSet resultSet, @NotNull String packagePrefix, @NotNull Project project) {
		JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
		PsiPackage basePackage = javaPsiFacade.findPackage(packagePrefix);
		if (basePackage != null) {
			PsiPackage[] subPackages = basePackage.getSubPackages();
			for (PsiPackage pkg : subPackages) {
				// For some reason, we see some invalid packages here - e.g. META-INF. Filter them out.
				String name = pkg.getName();
				boolean invalidPkg = false;
				assert name != null;  // can only be null for default package, which this is not, as it's a subpackage.
				for (int i = 0; i < name.length(); i++) {
					if (!Character.isJavaIdentifierPart(name.charAt(i))) {
						invalidPkg = true;
						break;
					}
				}
				if (invalidPkg) {
					continue;  // skip adding this package.
				}
				LookupElementBuilder element = LookupElementBuilder.create(pkg.getQualifiedName()).withIcon(pkg.getIcon(0));
				/*
				LookupElement element = new TailTypeDecorator<LookupElement>(LookupElementBuilder.createWithIcon(pkg)) {
					@Nullable
					@Override
					protected TailType computeTailType(InsertionContext context) {
						return TailType.DOT;
					}

					@Override
					public void handleInsert(InsertionContext context) {
						super.handleInsert(context);
						System.out.println("DataBindingCompletionUtil.handleInsert");
						AutoPopupController.getInstance(project).scheduleAutoPopup(context.getEditor());
					}
				};
				//System.out.println("element = " + element);
				*/
				resultSet.addElement(element);
			}

			for (PsiClass psiClass : basePackage.getClasses()) {
				LookupElementBuilder element = LookupElementBuilder.create(psiClass.getQualifiedName()).withIcon(psiClass.getIcon(0));
				resultSet.addElement(element);
				//resultSet.addElement(new JavaPsiClassReferenceElement(psiClass));
			}
		}
	}

	@NotNull
	private static JavaPsiClassReferenceElement getClassReferenceElement(String alias, PsiClass referenceClass) {
		JavaPsiClassReferenceElement element = new JavaPsiClassReferenceElement(referenceClass);
		element.setForcedPresentableName(alias);
		element.setInsertHandler((context, item) -> {
			// Override the default InsertHandler to prevent adding the FQCN.
		});
		return element;
	}

	/**
	 * Copied from {@link AllClassesGetter}#getPackagePrefix(PsiElement, int), since that method is private.
	 */
	private static String getPackagePrefix(@NotNull final PsiElement context, final int offset) {
		final CharSequence fileText = context.getContainingFile().getViewProvider().getContents();
		int i = offset - 1;
		while (i >= 0) {
			final char c = fileText.charAt(i);
			if (!Character.isJavaIdentifierPart(c) && c != '.') break;
			i--;
		}
		String prefix = fileText.subSequence(i + 1, offset).toString();
		final int j = prefix.lastIndexOf('.');
		return j > 0 ? prefix.substring(0, j) : "";
	}

}

