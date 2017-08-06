package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.codeInsight.template.impl.Variable;
import org.junit.Test;

import java.util.Set;

import static de.endrullis.idea.postfixtemplates.templates.CustomStringPostfixTemplate.*;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link CustomStringPostfixTemplate}.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CustomStringPostfixTemplateTest {

	@Test
	public void testParseVariableNames() {
		String templateText = "a$b$c$d:v$$e:exp:value$fg\\$h\\$i";

		Set<String> variableNames = parseVariableNames(templateText);

		assertEquals(_Set("b", "d:v", "e:exp:value"), variableNames);

		System.out.println(variableNames);
	}

	@Test
	public void testParseVariables() throws Exception {
		String templateText = "a$b$c$d:v$$e:exp:value$fg\\$h\\$i";

		Set<MyVariable> variables = parseVariables(templateText);

		assertEquals(_Set(
			new Variable("b", "", "", true),
			new Variable("d", "", "v", true),
			new Variable("e", "exp", "value", true)
		), variables);
	}

	@Test
	public void testRemoveVariableValues() throws Exception {
		String templateText = "a$b$c$d:v$$e:exp:value$fg\\$h\\$i";

		Set<MyVariable> variables = parseVariables(templateText);
		String newTemplateText = removeVariableValues(templateText, variables);

		assertEquals("a$b$c$d$$e$fg\\$h\\$i", newTemplateText);
	}

}
