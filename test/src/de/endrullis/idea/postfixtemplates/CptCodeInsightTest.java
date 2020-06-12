package de.endrullis.idea.postfixtemplates;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.List;
import java.util.regex.Pattern;

public class CptCodeInsightTest extends LightJavaCodeInsightFixtureTestCase {
	@Override
	protected String getTestDataPath() {
		return "test/src-test";
	}

	/*
	ProjectDescriptor projectDescriptor = new ProjectDescriptor(LanguageLevel.JDK_1_8, true) {
		@Override
		public Sdk getSdk() {
			JavaSdk.getInstance().createJdk()
			return super.getSdk();
		}
	};
	 */

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		LanguageLevelProjectExtension.getInstance(getProject()).setLanguageLevel(LanguageLevel.JDK_1_8);
	}

	public void testWrapWithArray() throws Exception {
		assertWrapping("((double) (123))", "123.toDouble\t");
		assertWrapping("((int) (123))", "123.toInt\t");
		//assertWrapping("Integer.parseInt(\"123\")", "\"123\".toInt\t");
	}

	/**
	 * Asserts that the expansion of {@code content} by .o equals {@code expected}.
	 */
	private void assertWrapping(String expected, String content) {
		String  prefix = "class A { void f() { ";
		PsiFile file   = myFixture.configureByText(JavaFileType.INSTANCE, prefix + "<caret> }}");
		myFixture.type(content);
		//myFixture.complete(CompletionType.BASIC, 1);
		//List<String> strings = myFixture.getLookupElementStrings();
		//System.out.println(strings);
		String result =
			file.getText()
			.replaceFirst("^.*?class A \\{ void f\\(\\) \\{", "")
			.replaceFirst("}}$", "")
			.trim();

		assertEquals(expected, result);
	}

}
