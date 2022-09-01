package de.endrullis.idea.postfixtemplates.language.psi;

import com.intellij.psi.tree.IElementType;
import de.endrullis.idea.postfixtemplates.language.CptLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CptElementType extends IElementType {
	public CptElementType(@NotNull @NonNls String debugName) {
		super(debugName, CptLanguage.INSTANCE);
	}
}
