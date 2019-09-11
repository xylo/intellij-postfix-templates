package de.endrullis.idea.postfixtemplates.templates;

import de.endrullis.idea.postfixtemplates.languages.java.CustomJavaStringPostfixTemplate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.*;
import static de.endrullis.idea.postfixtemplates.templates.CustomPostfixTemplateUtils.processEscapes;
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
		String templateText = processEscapes("a$b$c$d:v$$e:exp:value$fg\\$h\\$i$j*$k");

		ArrayList<String> variableNames = new ArrayList<>(parseVariableNames(templateText));

		assertEquals(_List("b", "d:v", "e:exp:value", "j*"), variableNames);
	}

	@Test
	public void testParseVariableNames2() {
		String templateText = processEscapes("Timber.d(\"$expr$ = \\$$expr$$END$\")");

		ArrayList<String> variableNames = new ArrayList<>(parseVariableNames(templateText));

		assertEquals(_List("expr", "END"), variableNames);
	}

	@Test
	public void testParseVariableNames3() {
		String templateText = processEscapes("$x::\"\\\"\\$\\\"\"$");

		ArrayList<String> variableNames = new ArrayList<>(parseVariableNames(templateText));

		assertEquals(_List("x::\"\\\"$\\\"\""), variableNames);
	}

	@Test
	public void testParseVariables() {
		String templateText = processEscapes("a$b$c$d:v$$e:exp:value$fg\\$h\\$i$j*$k");

		ArrayList<MyVariable> variables = new ArrayList<>(parseVariables(templateText));

		assertEquals(_List(
			new MyVariable("b", "", "", true, false, 0, null),
			new MyVariable("d", "v", "", true, false, 1, null),
			new MyVariable("e", "exp", "value", true, false, 2, null),
			new MyVariable("j", "", "", true, true, 3, null)
		), variables);
	}

	@Test
	public void testParseVariables2() {
		String templateText = processEscapes("Timber.d(\"$expr$ = \\$$expr$$END$\")");

		ArrayList<MyVariable> variables = new ArrayList<>(parseVariables(templateText));

		assertEquals(_List(
			new MyVariable("expr", "", "", true, false, 0, null),
			new MyVariable("END", "", "", true, false, 1, null)
		), variables);
	}

	@Test
	public void testParseVariables3() {
		String templateText = processEscapes("$x::\"\\\"\\$\\\"\"$");

		ArrayList<MyVariable> variables = new ArrayList<>(parseVariables(templateText));

		assertEquals(_List(
			new MyVariable("x", "", "\"\\\"$\\\"\"", true, false, 0, null)
		), variables);
	}

	@Test
	public void testRemoveVariableValues() {
		String templateText = processEscapes("a$b$c$d:v$$e:exp:value$fg\\$h\\$i$j*$k");

		Set<MyVariable> variables = new HashSet<>(parseVariables(templateText));
		String newTemplateText = removeVariableValues(templateText, variables);

		assertEquals("a$b$c$d$$e$fg$$h$$i$j$k", newTemplateText);
	}

	@Test
	public void testRemoveVariableValues2() {
		String templateText = processEscapes("Timber.d(\"$expr$ = \\$$expr$$END$\")");

		Set<MyVariable> variables = new HashSet<>(parseVariables(templateText));

		String newTemplateText = removeVariableValues(templateText, variables);
		assertEquals("Timber.d(\"$expr$ = $$$expr$$END$\")", newTemplateText);
	}

	@Test
	public void testRemoveVariableValues3() {
		String templateText = processEscapes("$x::\"\\\"\\$\\\"\"$");

		Set<MyVariable> variables = new HashSet<>(parseVariables(templateText));

		String newTemplateText = removeVariableValues(templateText, variables);
		assertEquals("$x$", newTemplateText);
	}

	@Test
	public void testVariableReordering() {
		String templateText = processEscapes("a$b#2$c$d#1:v$$e#4:exp:value$fg\\$h\\$i$j*#3$k");

		ArrayList<MyVariable> variables = new ArrayList<>(parseVariables(templateText));

		assertEquals(_List(
			new MyVariable("b", "", "", true, false, 2, null),
			new MyVariable("d", "v", "", true, false, 1, null),
			new MyVariable("e", "exp", "value", true, false, 4, null),
			new MyVariable("j", "", "", true, true, 3, null)
		), variables);
	}

	@Test
	public void testQuoteEscape() {
		String templateText = processEscapes("encode($encoding::\"\\\"UTF-8\\\"\"$");

		ArrayList<MyVariable> variables = new ArrayList<>(parseVariables(templateText));

		assertEquals(_List(
			new MyVariable("encoding", "", "\"\\\"UTF-8\\\"\"", true, false, 0, null)
		), variables);
	}

}
