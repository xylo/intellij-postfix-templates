/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.endrullis.idea.postfixtemplates.templates.macro;

import com.goide.psi.GoConstDefinition;
import com.goide.psi.GoType;
import com.goide.psi.GoTypeOwner;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.macro.MacroUtil;
import com.intellij.psi.*;
import de.endrullis.idea.postfixtemplates.utils.GoTypeElementUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoArrayElementTypeNameMacro extends Macro {

    public static final String NAME = "goArrayElementTypeName";
    public static final String PRESENT_TABLE_NAME = GoTypeElementUtils.getPresentableName(NAME);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getPresentableName() {
        return PRESENT_TABLE_NAME;
    }

    @Override
    @NotNull
    public String getDefaultValue() {
        return "any";
    }

    @Override
    public Result calculateResult(@NotNull Expression @NotNull [] params, ExpressionContext context) {
        String typeName = GoTypeElementUtils.findElementGenericName(params, context, element -> {
            String name = "";
            if (element instanceof GoTypeOwner) {

                ResolveState resolveState = !(element instanceof GoConstDefinition) && context != null ?
                        GoPsiImplUtil.createContextOnElement(element.getContext()) : null;

                GoType type = ((GoTypeOwner) element).getGoType(resolveState);
                if (type == null) {
                    return name;
                }
                return GoTypeElementUtils.nullObjectToDefault(GoTypeUtil.getArrayOrSliceElementType(type), "", GoType::getText);
            }
            return name;
        });

        return new TextResult(typeName);
    }


    @Nullable
    @Override
    public Result calculateQuickResult(@NotNull Expression @NotNull [] params, ExpressionContext context) {
        return calculateResult(params, context);
    }

    @Override
    public LookupElement[] calculateLookupItems(@NotNull Expression @NotNull [] params, final ExpressionContext context) {
        PsiFile file = PsiDocumentManager.getInstance(context.getProject()).getPsiFile(context.getEditor().getDocument());
        PsiElement e = file.findElementAt(context.getStartOffset());
        PsiVariable[] vars = MacroUtil.getVariablesVisibleAt(e, "");
        LookupElement[] items = new LookupElement[0];
        return items;
    }


    @Override
    public boolean isAcceptableInContext(TemplateContextType context) {
        return context instanceof JavaCodeContextType;
    }

}
