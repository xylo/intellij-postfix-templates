package de.endrullis.idea.postfixtemplates.languages;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import de.endrullis.idea.postfixtemplates.language.CptLang;
import de.endrullis.idea.postfixtemplates.language.CptUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.language.CptUtil.getVirtualFile;

/**
 * All supported languages of this plugin.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class SupportedLanguages {

	private static List<CptLang> getLangs() {
		Reflections reflections = new Reflections("de.endrullis.idea.postfixtemplates.languages");

		Set<Class<? extends CptLang>> langClasses = reflections.getSubTypesOf(CptLang.class);

		List<CptLang> langs = langClasses.stream().map(c -> {
			try {
				return Optional.of((CptLang) c.getConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
				return Optional.<CptLang>empty();
			}
		}).filter(o -> o.isPresent()).map(o -> o.get())
			.sorted(Comparator.comparing(l -> l.getNiceName().toLowerCase())).collect(Collectors.toList());

		return langs;
	}

	public static final List<CptLang> supportedLanguages = getLangs();

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
