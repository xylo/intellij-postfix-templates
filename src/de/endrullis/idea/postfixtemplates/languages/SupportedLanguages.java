package de.endrullis.idea.postfixtemplates.languages;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import de.endrullis.idea.postfixtemplates.languages.csharp.CsharpLang;
import de.endrullis.idea.postfixtemplates.languages.dart.DartLang;
import de.endrullis.idea.postfixtemplates.languages.groovy.GroovyLang;
import de.endrullis.idea.postfixtemplates.languages.java.JavaLang;
import de.endrullis.idea.postfixtemplates.languages.javascript.JavaScriptLang;
import de.endrullis.idea.postfixtemplates.languages.kotlin.KotlinLang;
import de.endrullis.idea.postfixtemplates.languages.php.PhpLang;
import de.endrullis.idea.postfixtemplates.languages.python.PythonLang;
import de.endrullis.idea.postfixtemplates.languages.rust.RustLang;
import de.endrullis.idea.postfixtemplates.languages.scala.ScalaLang;
import de.endrullis.idea.postfixtemplates.languages.sql.SqlLang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.language.CptUtil.getVirtualFile;
import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._List;

/**
 * All supported languages of this plugin.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class SupportedLanguages {

	public static final List<CptLang> supportedLanguages = _List(
		//new CsharpLang(),
		new DartLang(),
		new GroovyLang(),
		new KotlinLang(),
		new JavaLang(),
		new JavaScriptLang(),
		new ScalaLang(),
		new SqlLang(),
		new PhpLang(),
		new PythonLang(),
		new RustLang()
	);

	public static final Set<String> supportedLanguageIds = supportedLanguages.stream().map(cl -> cl.getLanguage()).collect(Collectors.toSet());

	private static final HashMap<String, CptLang> languageToCptLang = new HashMap<String, CptLang>() {{
		supportedLanguages.forEach(lang -> put(lang.getLanguage(), lang));
	}};

	@Nullable
	public static CptLang getCptLang(final String language) {
		return languageToCptLang.get(language);
	}

	@NotNull
	public static Optional<CptLang> getCptLang(@NotNull final PsiElement element) {
		return getCptLang(getVirtualFile(element));
	}

	@NotNull
	public static Optional<CptLang> getCptLang(VirtualFile vFile) {
		if (vFile == null) {
			return Optional.empty();
		}

		return Optional.ofNullable(CptUtil.getLanguageOfTemplateFile(vFile));
	}

}
