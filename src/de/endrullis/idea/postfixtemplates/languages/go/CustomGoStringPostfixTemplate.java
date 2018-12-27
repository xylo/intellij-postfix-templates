package de.endrullis.idea.postfixtemplates.languages.go;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import java.util.*;

import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.*;
import static com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils.IS_NOT_PRIMITIVE;
import static de.endrullis.idea.postfixtemplates.languages.java.MyJavaPostfixTemplatesUtils.*;
import static de.endrullis.idea.postfixtemplates.languages.java.MyJavaPostfixTemplatesUtils.IS_ARRAY;
import static de.endrullis.idea.postfixtemplates.languages.java.MyJavaPostfixTemplatesUtils.IS_ITERABLE_OR_ARRAY;

/**
 * Custom postfix template for Go.
 */
@SuppressWarnings("WeakerAccess")
public class CustomGoStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

    private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
        put(SpecialType.ANY.name(), e -> true);
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
    }};

    public CustomGoStringPostfixTemplate(String clazz, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
        super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(clazz)));
    }

    @NotNull
    public static Condition<PsiElement> getCondition(String clazz) {
        return type2psiCondition.get(clazz);
    }

}
