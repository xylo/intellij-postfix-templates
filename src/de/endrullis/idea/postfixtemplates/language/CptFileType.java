package de.endrullis.idea.postfixtemplates.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CptFileType extends LanguageFileType {
	public static final CptFileType INSTANCE = new CptFileType();

	private CptFileType() {
		super(CptLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public String getName() {
		return "Postfix templates file type";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Postfix templates file";
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return "postfixTemplates";
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return CptIcons.FILE;
	}

	@Override
	public String getCharset(@NotNull VirtualFile file, byte @NotNull [] content) {
		return "UTF-8";
	}
}
