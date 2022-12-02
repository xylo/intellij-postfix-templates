package de.endrullis.idea.postfixtemplates.language;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import org.jetbrains.annotations.NotNull;

/**
 * CPT context.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CptContext extends TemplateContextType {
	public CptContext() {
		super("Custom postfix templates");
	}

	@Override
	public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
		return templateActionContext.getFile().getFileType() == CptFileType.INSTANCE;
	}
}
