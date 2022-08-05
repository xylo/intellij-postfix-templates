/*
 * Copyright 2000-2017 JetBrains s.r.o.
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
package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CombinedPostfixTemplate extends PostfixTemplate implements Navigatable {

	private final List<PostfixTemplate>     myTemplates;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private       Optional<PostfixTemplate> myApplicableTemplate;

	public CombinedPostfixTemplate(@NotNull String name,
	                               @NotNull String example,
	                               @NotNull List<PostfixTemplate> templates,
	                               @NotNull PostfixTemplateProvider provider) {
		super(CombinedPostfixTemplate.class.getCanonicalName() + "#" + name.substring(1), name.substring(1), name, example, provider);
		this.myTemplates = templates;
	}

	@Override
	public boolean isApplicable(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
		myApplicableTemplate = myTemplates.stream().filter(t -> t.isApplicable(context, copyDocument, newOffset)).findFirst();

		return myApplicableTemplate.map(t -> !t.getExample().equals("[SKIP]")).orElse(false);
	}

	@Override
	public void expand(@NotNull PsiElement context, @NotNull Editor editor) {
		myApplicableTemplate.ifPresent(t -> t.expand(context, editor));
	}

	@Override
	public void navigate(boolean b) {
		if (canNavigate() && myApplicableTemplate.isPresent()) {
			Navigatable navigatable = (Navigatable) myApplicableTemplate.get();
			navigatable.navigate(b);
		}
	}

	@Override
	public boolean canNavigate() {
		return myApplicableTemplate.map(t -> {
			if (t instanceof Navigatable navigatable) {
				return navigatable.canNavigate();
			} else {
				return false;
			}
		}).orElse(false);
	}

	@Override
	public boolean canNavigateToSource() {
		return canNavigate();
	}

}
