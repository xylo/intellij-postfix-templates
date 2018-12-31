package de.endrullis.idea.postfixtemplates.languages.go;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.templates.SimpleStringBasedPostfixTemplate;
import de.endrullis.idea.postfixtemplates.templates.SpecialType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static de.endrullis.idea.postfixtemplates.languages.go.GoPostfixTemplatesUtils.*;

/**
 * Custom postfix template for Go.
 */
@SuppressWarnings("WeakerAccess")
public class CustomGoStringPostfixTemplate extends SimpleStringBasedPostfixTemplate {

    private static final Map<String, Condition<PsiElement>> type2psiCondition = new HashMap<String, Condition<PsiElement>>() {{
        put(SpecialType.ANY.name(), e -> true);
        put(SpecialType.BOOLEAN.name(), IS_BOOL);
        put(SpecialType.INT.name(), IS_INT);
        put(SpecialType.INT64.name(), IS_INT64);
        put(SpecialType.UINT.name(), IS_UINT);
        put(SpecialType.FLOAT32.name(), IS_FLOAT32);
        put(SpecialType.FLOAT64.name(), IS_FLOAT64);
        put(SpecialType.FLOAT.name(), IS_FLOAT);
        put(SpecialType.BYTESLICE.name(), IS_BYTESLICE);
        put(SpecialType.ERROR.name(), IS_ERROR);
        put(SpecialType.ARRAY.name(), IS_ARRAY);
        put(SpecialType.COMPLEX.name(), IS_COMPLEX);
        put(SpecialType.NIL.name(), IS_NIL);
        put(SpecialType.STRING.name(), IS_STRING);
    }};

    public CustomGoStringPostfixTemplate(String clazz, String name, String example, String template, PostfixTemplateProvider provider, PsiElement psiElement) {
        super(name, example, template, provider, psiElement, selectorAllExpressionsWithCurrentOffset(getCondition(clazz)));
    }

    @NotNull
    public static Condition<PsiElement> getCondition(String clazz) {
        return type2psiCondition.get(clazz);
    }

}
