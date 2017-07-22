package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptCompletionUtil {

	public static void addCompletions(@NotNull CompletionParameters params, @NotNull CompletionResultSet resultSet) {
		final PsiElement originalPosition = params.getOriginalPosition();
		final PsiElement originalParent = originalPosition == null ? null : originalPosition.getParent();
		if (originalParent == null) {
			return;
		}
		final Module module = ModuleUtilCore.findModuleForPsiElement(originalParent);
		if (module == null) {
			return;
		}
		final PsiFile containingFile = originalParent.getContainingFile();
		if (containingFile == null) {
			return;
		}
		final String packagePrefix = getPackagePrefix(originalParent, params.getOffset());
		//fillAliases(resultSet, packagePrefix, originalPosition, module, originalParent);
		fillClassNames(resultSet, packagePrefix, module);
		/*
		JavaClassNameCompletionContributor.addAllClasses(params, true, resultSet.getPrefixMatcher(), element -> {
			System.out.println(packagePrefix + "." + element.getLookupString());
			resultSet.addElement(LookupElementBuilder.create(packagePrefix + "." + element.getLookupString()));
		});
		*/
		resultSet.stopHere();
	}

	private static void fillClassNames(@NotNull CompletionResultSet resultSet, @NotNull String packagePrefix, @NotNull Module module) {
		final Project project = module.getProject();
		JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
		PsiPackage basePackage = javaPsiFacade.findPackage(packagePrefix);
		//System.out.println("basePackage = " + basePackage);
		if (basePackage == null) {
			System.out.println("basePackage = " + basePackage);
			PsiClass aClass = javaPsiFacade.findClass(packagePrefix, module.getModuleWithDependenciesAndLibrariesScope(false));
			if (aClass != null) {
				PsiClass[] innerClasses = aClass.getInnerClasses();
				for (PsiClass innerClass : innerClasses) {
					resultSet.addElement(new JavaPsiClassReferenceElement(innerClass));
				}
			}
			// TODO: add completions for java.lang classes
		} else {
			GlobalSearchScope scope = module.getModuleWithDependenciesAndLibrariesScope(false);
			PsiPackage[] subPackages = basePackage.getSubPackages(scope);
			//System.out.println("subPackages = " + Arrays.toString(subPackages));
			for (PsiPackage pkg : subPackages) {
				// For some reason, we see some invalid packages here - eg. META-INF. Filter them out.
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

			for (PsiClass psiClass : basePackage.getClasses(scope)) {
				LookupElementBuilder element = LookupElementBuilder.create(psiClass.getQualifiedName()).withIcon(psiClass.getIcon(0));
				resultSet.addElement(element);
			}
		}
	}

	@NotNull
	private static JavaPsiClassReferenceElement getClassReferenceElement(String alias, PsiClass referenceClass) {
		JavaPsiClassReferenceElement element = new JavaPsiClassReferenceElement(referenceClass);
		element.setForcedPresentableName(alias);
		element.setInsertHandler(new InsertHandler<LookupElement>() {
			@Override
			public void handleInsert(InsertionContext context, LookupElement item) {
				// Override the default InsertHandler to prevent adding the FQCN.
			}
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

