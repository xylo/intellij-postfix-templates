package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptLiveTemplatesProvider implements DefaultLiveTemplatesProvider {
	@Override
	public String[] getDefaultLiveTemplateFiles() {
		return new String[]{"liveTemplates/CustomPostfixTemplates"};
	}

	@Nullable
	@Override
	public String[] getHiddenLiveTemplateFiles() {
		return new String[0];
	}
}
