package de.endrullis.idea.postfixtemplates.language.psi;

import com.intellij.psi.tree.IElementType;
import de.endrullis.idea.postfixtemplates.language.CptLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CptTokenType extends IElementType {
	public CptTokenType(@NotNull @NonNls String debugName) {
		super(debugName, CptLanguage.INSTANCE);
	}

	@Override
	public String toString() {
		return "CptTokenType." + super.toString();
	}
}
