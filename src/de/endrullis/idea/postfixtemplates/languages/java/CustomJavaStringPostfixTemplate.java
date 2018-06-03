package de.endrullis.idea.postfixtemplates.languages.java;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.codeInsight.template.impl.Variable;
import com.intellij.codeInsight.template.postfix.templates.*;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.templates.NavigatableTemplate;
import de.endrullis.idea.postfixtemplates.templates.MyJavaPostfixTemplatesUtils;
import de.endrullis.idea.postfixtemplates.templates.MyVariable;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.IS_BOOLEAN;
import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.IS_NON_VOID;
import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.IS_NOT_PRIMITIVE;
import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.getTopmostExpression;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.parseVariables;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.removeVariableValues;
import static de.endrullis.idea.postfixtemplates.templates.MyJavaPostfixTemplatesUtils.*;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Custom postfix template for Java.
 */
@SuppressWarnings("WeakerAccess")
public class CustomJavaStringPostfixTemplate extends StringBasedPostfixTemplate implements NavigatableTemplate {

	public static final Set<String> PREDEFINED_VARIABLES = _Set("expr", "END");

	private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
		put(SpecialType.ANY.name(), IS_ANY);
		put(SpecialType.VOID.name(), IS_VOID);
		put(SpecialType.NON_VOID.name(), IS_NON_VOID);
		put(SpecialType.ARRAY.name(), IS_ARRAY);
		put(SpecialType.BOOLEAN.name(), IS_BOOLEAN);
		put(SpecialType.ITERABLE_OR_ARRAY.name(), IS_ITERABLE_OR_ARRAY);
		put(SpecialType.NOT_PRIMITIVE.name(), IS_NOT_PRIMITIVE);
		put(SpecialType.NUMBER.name(), IS_DECIMAL_NUMBER);
		put(SpecialType.BYTE.name(), isCertainNumberType(PsiType.BYTE));
		put(SpecialType.SHORT.name(), isCertainNumberType(PsiType.SHORT));
		put(SpecialType.CHAR.name(), isCertainNumberType(PsiType.CHAR));
		put(SpecialType.INT.name(), isCertainNumberType(PsiType.INT));
		put(SpecialType.LONG.name(), isCertainNumberType(PsiType.LONG));
		put(SpecialType.FLOAT.name(), isCertainNumberType(PsiType.FLOAT));
		put(SpecialType.DOUBLE.name(), isCertainNumberType(PsiType.DOUBLE));
		put(SpecialType.BYTE_LITERAL.name(), isCertainNumberLiteral(PsiType.BYTE));
		put(SpecialType.SHORT_LITERAL.name(), isCertainNumberLiteral(PsiType.SHORT));
		put(SpecialType.CHAR_LITERAL.name(), isCertainNumberLiteral(PsiType.CHAR));
		put(SpecialType.INT_LITERAL.name(), isCertainNumberLiteral(PsiType.INT));
		put(SpecialType.LONG_LITERAL.name(), isCertainNumberLiteral(PsiType.LONG));
		put(SpecialType.FLOAT_LITERAL.name(), isCertainNumberLiteral(PsiType.FLOAT));
		put(SpecialType.DOUBLE_LITERAL.name(), isCertainNumberLiteral(PsiType.DOUBLE));
		put(SpecialType.NUMBER_LITERAL.name(), IS_DECIMAL_NUMBER_LITERAL);
		put(SpecialType.STRING_LITERAL.name(), STRING_LITERAL);
		put(SpecialType.CLASS.name(), IS_CLASS);
		/*
		put(SpecialType.FIELD.name(), IS_FIELD);
		put(SpecialType.LOCAL_VARIABLE.name(), IS_LOCAL_VARIABLE);
		put(SpecialType.VARIABLE.name(), IS_VARIABLE);
		put(SpecialType.ASSIGNMENT.name(), IS_ASSIGNMENT);
		*/
	}};

	private final String          template;
	private final Set<MyVariable> variables = new OrderedSet<>();
	private final PsiElement      psiElement;
	private final boolean         useStaticImports;

	public static List<PsiExpression> collectExpressions(final PsiFile file,
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
		final List<PsiExpression> expressions = new ArrayList<>();
	 /*for (PsiElement element : statementsInRange) {
     if (element instanceof PsiExpressionStatement) {
       final PsiExpression expression = ((PsiExpressionStatement)element).getExpression();
       if (expression.getType() != PsiType.VOID) {
         expressions.add(expression);
       }
     }
   }*/
		PsiExpression expression = PsiTreeUtil.getParentOfType(elementAtCaret, PsiExpression.class);
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

		/*
		for (PsiElement psiElement : expressions) {
			System.out.println("parent: " + psiElement + " at " + psiElement.getTextRange());
		}
		*/

		return expressions;
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

	public CustomJavaStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name.substring(1), name, example, selectorAllExpressionsWithCurrentOffset(getCondition(matchingClass, conditionClass)), provider);
		this.psiElement = psiElement;

		useStaticImports = template.contains("[USE_STATIC_IMPORTS]");
		template = template.replaceAll("\\[USE_STATIC_IMPORTS\\]", "");

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

	/**
	 * Returns a function that returns true if
	 * <ul>
	 * <li>the PSI element satisfies the type condition regarding {@code matchingClass} and</li>
	 * <li>{@code conditionClass} is either {@code null} or available in the current module.</li>
	 * </ul>
	 *
	 * @param matchingClass  required type of the psi element to satisfy this condition
	 * @param conditionClass required class in the current module to satisfy this condition, or {@code null}
	 * @return PSI element condition
	 */
	@NotNull
	public static Condition<PsiElement> getCondition(final @NotNull String matchingClass, final @Nullable String conditionClass) {
		Condition<PsiElement> psiElementCondition = type2psiCondition.get(matchingClass);

		if (psiElementCondition == null) {
			psiElementCondition = MyJavaPostfixTemplatesUtils.isCustomClass(matchingClass);
		}

		return withProjectClassCondition(conditionClass, psiElementCondition);
	}

	public static Condition<PsiElement> withProjectClassCondition(@Nullable String conditionClass, Condition<PsiElement> psiElementCondition) {
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

					if (module != null) {
						return JavaPsiFacade.getInstance(project).findClass(conditionClass, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true)) != null;
					} else {
						return JavaPsiFacade.getInstance(project).findClass(conditionClass, GlobalSearchScope.projectScope(project)) != null;
					}
				} else {
					return false;
				}
			};
		}
	}

	@Nullable
	@Override
	public String getTemplateString(@NotNull PsiElement element) {
		return template;
	}

	public PsiElement getNavigationElement() {
		return psiElement;
	}

	@Override
	public void expandForChooseExpression(@NotNull PsiElement expr, @NotNull Editor editor) {
		Project project = expr.getProject();
		Document document = editor.getDocument();
		PsiElement elementForRemoving = getElementToRemove(expr);
		document.deleteString(elementForRemoving.getTextRange().getStartOffset(), elementForRemoving.getTextRange().getEndOffset());
		TemplateManager manager = TemplateManager.getInstance(project);

		String templateString = getTemplateString(expr);
		if (templateString == null) {
			PostfixTemplatesUtils.showErrorHint(expr.getProject(), editor);
			return;
		}

		Template template = createTemplate(manager, templateString);

		if (useStaticImports) {
			template.setValue(Template.Property.USE_STATIC_IMPORT_IF_POSSIBLE, true);
		}

		template.addVariable("expr", new TextExpression(expr.getText()), false);
		setVariables(template, expr);
		manager.startTemplate(editor, template);
	}

}
