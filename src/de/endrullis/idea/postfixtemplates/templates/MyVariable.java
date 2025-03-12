package de.endrullis.idea.postfixtemplates.templates;

import com.intellij.codeInsight.template.impl.Variable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Template variable.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@EqualsAndHashCode(of = {"skipOnStart", "no"}, callSuper = true)
public class MyVariable extends Variable {
	@Getter
	private final String  varCode;
	private final boolean skipOnStart;
	@Getter
	private final int     no;

	public MyVariable(@NotNull String name, @Nullable String expression, @Nullable String defaultValue,
	                  boolean alwaysStopAt, boolean skipOnStart, int no, String varCode) {
		super(name, expression, defaultValue, !skipOnStart);
		this.skipOnStart = skipOnStart;
		this.no = no;
		this.varCode = varCode;
	}

	@Override
	public boolean skipOnStart() {
		return skipOnStart;
	}
}
