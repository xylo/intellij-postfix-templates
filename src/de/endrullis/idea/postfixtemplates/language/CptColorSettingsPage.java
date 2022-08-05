package de.endrullis.idea.postfixtemplates.language;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class CptColorSettingsPage implements ColorSettingsPage {
	private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
		new AttributesDescriptor("Template name", CptSyntaxHighlighter.TEMPLATE_NAME),
		new AttributesDescriptor("Class name", CptSyntaxHighlighter.CLASS_NAME),
		new AttributesDescriptor("Separator", CptSyntaxHighlighter.SEPARATOR),
		new AttributesDescriptor("Description", CptSyntaxHighlighter.TEMPLATE_DESCRIPTION),
		new AttributesDescriptor("Template code", CptSyntaxHighlighter.TEMPLATE_CODE),
	};

	@Nullable
	@Override
	public Icon getIcon() {
		return CptIcons.FILE;
	}

	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter() {
		return new CptSyntaxHighlighter();
	}

	@NotNull
	@Override
	public String getDemoText() {
		return CptUtil.getDefaultTemplates("java");
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return null;
	}

	@NotNull
	@Override
	public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
		return DESCRIPTORS;
	}

	@NotNull
	@Override
	public ColorDescriptor @NotNull [] getColorDescriptors() {
		return ColorDescriptor.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return "Custom Postfix Templates";
	}
}