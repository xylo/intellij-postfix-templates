package de.endrullis.idea.postfixtemplates.templates;

import de.endrullis.idea.postfixtemplates.languages.java.CustomJavaStringPostfixTemplate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.parseVariableNames;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.parseVariables;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.removeVariableValues;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link CustomJavaStringPostfixTemplate}.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CustomStringPostfixTemplateTest {

	@Test
	public void testParseVariableNames() {
		String templateText = "a$b$c$d:v$$e:exp:value$fg\\$h\\$i$j*$k";

		ArrayList<String> variableNames = new ArrayList<>(parseVariableNames(templateText));

		assertEquals(_List("b", "d:v", "e:exp:value", "j*"), variableNames);
	}

	@Test
	public void testParseVariables() throws Exception {
		String templateText = "a$b$c$d:v$$e:exp:value$fg\\$h\\$i$j*$k";

		ArrayList<MyVariable> variables = new ArrayList<>(parseVariables(templateText));

		assertEquals(_List(
			new MyVariable("b", "", "", true, false, 0, null),
			new MyVariable("d", "v", "", true, false, 1, null),
			new MyVariable("e", "exp", "value", true, false, 2, null),
			new MyVariable("j", "", "", true, true, 3, null)
		), variables);
	}

	@Test
	public void testRemoveVariableValues() throws Exception {
		String templateText = "a$b$c$d:v$$e:exp:value$fg\\$h\\$i$j*$k";

		Set<MyVariable> variables = new HashSet<>(parseVariables(templateText));
		String newTemplateText = removeVariableValues(templateText, variables);

		assertEquals("a$b$c$d$$e$fg\\$h\\$i$j$k", newTemplateText);
	}

	@Test
	public void testVariableReordering() {
		String templateText = "a$b#2$c$d#1:v$$e#4:exp:value$fg\\$h\\$i$j*#3$k";

		ArrayList<MyVariable> variables = new ArrayList<>(parseVariables(templateText));

		assertEquals(_List(
			new MyVariable("b", "", "", true, false, 2, null),
			new MyVariable("d", "v", "", true, false, 1, null),
			new MyVariable("e", "exp", "value", true, false, 4, null),
			new MyVariable("j", "", "", true, true, 3, null)
		), variables);
	}

}
