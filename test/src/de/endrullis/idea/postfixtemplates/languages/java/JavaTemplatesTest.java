package de.endrullis.idea.postfixtemplates.languages.java;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

public class JavaTemplatesTest extends LightJavaCodeInsightFixtureTestCase {
	@Override
	protected String getTestDataPath() {
		return "test/src-test";
	}

	ProjectDescriptor projectDescriptor = new ProjectDescriptor(LanguageLevel.JDK_1_8, true) {
		@Override
		public Sdk getSdk() {
			return JavaSdk.getInstance().createJdk("1.8", "/usr/lib/jvm/java-8-oracle/jre");
			//return super.getSdk();
		}
	};

	@NotNull
	@Override
	protected LightProjectDescriptor getProjectDescriptor() {
		return projectDescriptor;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		LanguageLevelProjectExtension.getInstance(getProject()).setLanguageLevel(LanguageLevel.JDK_1_8);
	}

	public void testWrapWithArray() {
		// int.toDouble
		assertExpansion("((double) (123))", "123.toDouble\t");
		// int.toInt
		assertExpansion("((int) (123))", "123.toInt\t");
		// String.toInt
		assertExpansion("Integer.parseInt(\"123\")", "\"123\".toInt\t");

		// File.lines
		assertExpansion("Files.readAllLines(new File(\".\").toPath(), Charset.forName(\"UTF-8\"))", "new java.io.File(\".\").lines\t",
			"java.io.File", "java.nio.file.Files", "java.nio.charset.Charset");
		// String.lines
		assertExpansion("\"abc\".split(\"\\r?\\n\")", "\"abc\".lines\t");
	}

	/**
	 * Asserts that the expansion of {@code content} by .o equals {@code expected}.
	 */
	private void assertExpansion(String expected, String content, String... expectedImports) {
		String  prefix = "class A { void f() { ";
		PsiFile file   = myFixture.configureByText(JavaFileType.INSTANCE, prefix + "<caret> }}");
		myFixture.type(content);
		//myFixture.complete(CompletionType.BASIC, 1);
		//List<String> strings = myFixture.getLookupElementStrings();
		//System.out.println(strings);
		String text = file.getText();

		val actualImports = Arrays.stream(text.split("\r?\n"))
			.filter(s -> s.startsWith("import "))
			.map(s -> s.replaceFirst("import (.*);", "$1"))
			.collect(Collectors.toSet());

		assertEquals(_Set(expectedImports), actualImports);

		String result =
			text
			.replaceAll("import .*;", "")
			.replaceFirst("class A \\{ void f\\(\\) \\{", "")
			.replaceFirst("}}$", "")
			.trim();

		assertEquals(expected, result);
	}

}
