package de.endrullis.idea.postfixtemplates.utils;

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Result;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author yarkizhang
 * @date 2024/8/12
 */
public class GoTypeElementUtils {

    public static String getPresentableName(@NotNull String name) {
        return name+"()";
    }

    public static String findElementGenericName(@NotNull Expression @NotNull [] params, ExpressionContext context, Function<PsiElement, String> f) {
        if (params.length == 0) {
            return "";
        }

        Expression param = ArrayUtil.getFirstElement(params);
        Result paramResult = param != null ? param.calculateResult(context) : null;
        String paramText = paramResult != null ? paramResult.toString() : null;
        PsiElement element = context.getPsiElementAtStartOffset();

        PsiElement[] psiElements = PsiTreeUtil.collectElements(element.getContext(),
                psiElement -> psiElement.getText().equals(paramText));

        if (psiElements.length > 0) {
            element = psiElements[0];
            if (element instanceof LeafPsiElement) {
                element = element.getParent();
            }
            return f.apply(element);
        }
        return "";

    }

    public static <T, V> V nullObjectToDefault(T t, V v, Function<T, V> f) {
        if (Objects.isNull(t)) {
            return v;
        }
        return f.apply(t);
    }

}
