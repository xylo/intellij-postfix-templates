package de.endrullis.idea.postfixtemplates.languages.php;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Utilities for PHP postfix templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
class PhpPostfixTemplatesUtils {

	static final Set<PhpType> PHP_TYPES = _Set(
		PhpType.EMPTY,
		//PhpType.MIXED,
		PhpType.NULL,
		PhpType.STRING,
		PhpType.BOOLEAN,
		PhpType.INT,
		PhpType.FLOAT,
		PhpType.OBJECT,
		PhpType.CLOSURE,
		PhpType.CALLABLE,
		PhpType.RESOURCE,
		PhpType.ARRAY,
		PhpType.ITERABLE,
		PhpType.NUMBER,
		PhpType.VOID,
		//PhpType.NUMERIC,
		//PhpType.SCALAR,
		//PhpType.FLOAT_INT,
		PhpType.UNSET,
		PhpType.STATIC,
		PhpType.EXCEPTION,
		PhpType.THROWABLE
		//PhpType.$THIS
	);

	static boolean isInstanceOf(@NotNull PhpType subType, @NotNull PhpType superType, PsiElement psiElement) {
		return superType.isConvertibleFrom(subType, PhpIndex.getInstance(psiElement.getProject()));
	}

	static boolean isProjectClass(@NotNull String conditionClass, PsiElement e) {
		return PhpIndex.getInstance(e.getProject()).getClassByName(conditionClass) != null;
	}

	static void addCompletions(CompletionParameters parameters, CompletionResultSet resultSet) {
		// TODO
	}

}
