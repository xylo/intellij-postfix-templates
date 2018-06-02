package de.endrullis.idea.postfixtemplates.templates;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;
import static de.endrullis.idea.postfixtemplates.utils.StringUtils.replace;
import static org.junit.Assert.assertTrue;

/**
 * Application that tests all templates by automatically applying them to the Java file "TemplatesTestTemplate".
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class TemplateExtractionTestApp {

	public static void main(String[] args) throws IOException {
		File javaTemplateFile = new File("test/src/de/endrullis/idea/postfixtemplates/templates/TemplatesTestTemplate.java");
		File javaOutputFile   = new File("test/src/de/endrullis/idea/postfixtemplates/templates/TemplatesTestOutput.java");

		List<String> javaTemplateLines = Files.readAllLines(javaTemplateFile.toPath(), Charset.forName("UTF-8"));

		try (PrintStream out = new PrintStream(javaOutputFile)) {
			for (String javaLine : javaTemplateLines) {
				if (javaLine.contains("// PLACEHOLDER")) {
					printTemplates(out);
				} else {
					out.println(replace(javaLine, "TemplatesTestTemplate", "TemplatesTestOutput"));
				}
			}
		}
	}

	private static void printTemplates(PrintStream out) {
		InputStream templatesIn = TemplateExtractionTestApp.class.getResourceAsStream("/de/endrullis/idea/postfixtemplates/language/defaulttemplates/java.postfixTemplates");
		Stream<String> templateLines = new BufferedReader(new InputStreamReader(templatesIn)).lines().filter(s -> s.contains("→"));

		InputStream typeMappingIn = TemplateExtractionTestApp.class.getResourceAsStream("/de/endrullis/idea/postfixtemplates/templates/typeMapping.txt");
		Map<String, String> type2var = new BufferedReader(new InputStreamReader(typeMappingIn)).lines().filter(s -> s.contains("→"))
			.map(l -> l.split("→")).collect(Collectors.toMap(a -> a[0].trim(), a -> a[1].trim()));

		templateLines.forEach(line -> {
			String[] parts = line.split("→");

			assertTrue(parts[1].contains("$expr$"));

			String expr = type2var.get(parts[0].trim());
			if (expr == null) {
				throw new RuntimeException("No type mapping found for " + parts[0]);
			}

			String s = replace(parts[1], "$expr$", expr);
			for (String from : _List("$keyF$", "$valueF$", "$attributeF$", "$f$")) {
				s = replace(s, from, "x -> x");
			}
			s = replace(s, "$key$", "\"key\"");
			s = replace(s, "$conditionF$", "x -> true");
			s = replace(s, "$stream$", "stream");
			s = replace(s, "$endActionF$", "x -> {}");
			//s = replace(s, "$actionF$", "x -> {}");
			s = replace(s, "$separator$", "\", \"");
			s = replace(s, "$accumulatorF$", "(a, b) -> b");
			s = replace(s, "$neutralElement$", "\"\"");
			s = replace(s, "$i$", "0");
			s = replace(s, "$encoding::\"\\\"UTF-8\\\"\"$", "\"UTF-8\"");
			s = replace(s, "$var:suggestVariableName()$", "x");

			s = replace(s, "$object$", "object");
			s = replace(s, "$doubleValue$", "0d");
			s = replace(s, "$intValue$", "0");
			s = replace(s, "$longValue$", "0L");


			String res = "f(() -> " + s + ");";

			out.println(res);
		});
	}

}
