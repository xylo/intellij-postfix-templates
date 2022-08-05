package de.endrullis.idea.postfixtemplates.languages.rust;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import org.jetbrains.annotations.NotNull;
import org.rust.ide.template.postfix.RsExprParentsSelector;
import org.rust.lang.core.psi.RsExpr;
import org.rust.lang.core.types.ExtensionsKt;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom postfix template for Rust.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@SuppressWarnings("WeakerAccess")
public class CustomRustStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

	/** Contains predefined type-to-psiCondition mappings as well as cached mappings for individual types. */
	private static final Map<String, Condition<RsExpr>> type2psiCondition = new HashMap<String, Condition<RsExpr>>() {{
		for (RustType value : RustType.values()) {
			if (value == RustType.ANY) {
				put(value.fixedName(), e -> true);
			} else if (value == RustType.tuple) {
				put(value.fixedName(), e -> ExtensionsKt.getType(e).toString().startsWith("("));
			} else if (value == RustType.array) {
				put(value.fixedName(), e -> ExtensionsKt.getType(e).toString().startsWith("["));
			} else if (value == RustType.integer) {
				put(value.fixedName(), e -> RustType.isInteger(ExtensionsKt.getType(e).toString()));
			} else if (value == RustType.unsigned) {
				put(value.fixedName(), e -> RustType.isUnsigned(ExtensionsKt.getType(e).toString()));
			} else if (value == RustType.float_) {
				put(value.fixedName(), e -> RustType.isFloat(ExtensionsKt.getType(e).toString()));
			} else if (value == RustType.number) {
				put(value.fixedName(), e -> RustType.isNumber(ExtensionsKt.getType(e).toString()));
			} else {
				put(value.fixedName(), filterByType(value.fixedName()));
			}
		}
	}};

	@NotNull
	private static Condition<RsExpr> filterByType(String type) {
		return expr -> {
			System.out.println("ExtensionsKt.getType(expr) = " + ExtensionsKt.getType(expr));
			return ExtensionsKt.getType(expr).toString().replaceFirst("^&", "").equals(type);
		};
	}


	public CustomRustStringPostfixTemplate(String matchingClass, String conditionClass, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
		super(name, example, template, provider, psiElement, getRsExprSelector(matchingClass));
	}

	public static RsExprParentsSelector getRsExprSelector(String matchingClass) {
		final Condition<RsExpr> condition = getCondition(matchingClass);

		return new RsExprParentsSelector(expr -> condition.value(expr));
	}

	@NotNull
	public static Condition<RsExpr> getCondition(final @NotNull String matchingClass) {
		return type2psiCondition.getOrDefault(matchingClass, e -> true);
	}

}
