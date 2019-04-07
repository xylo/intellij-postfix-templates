package de.endrullis.idea.postfixtemplates.languages.scala;

import lombok.val;
import org.junit.Test;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;
import static org.junit.Assert.*;

public class CustomScalaStringPostfixTemplateTest {

	@Test
	public void extractImport() {
		val template = "[IMPORT a] b[c]de[IMPORT fg.h$i][IMPORT j]";

		val imports = CustomScalaStringPostfixTemplate.extractImport(template);

		assertEquals(_Set("a", "fg.h$i", "j"), imports);
	}

	@Test
	public void removeImports() {
		val template = "[IMPORT a] b[c]de[IMPORT fg.h$i][IMPORT j]";

		val newTemplate = CustomScalaStringPostfixTemplate.removeImports(template);

		assertEquals(" b[c]de", newTemplate);
	}

}
