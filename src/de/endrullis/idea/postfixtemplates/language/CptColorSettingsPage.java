package de.endrullis.idea.postfixtemplates.language;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.*;
import org.jetbrains.annotations.*;

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
    return "# You are reading the \".properties\" entry.\n" +
	    ".test : My description\n" +
	    "  java.lang.Integer   -> test\n" +
	    "  yxcvyxc.yxc         -> aa$END$";
  }

  @Nullable
  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return null;
  }

  @NotNull
  @Override
  public AttributesDescriptor[] getAttributeDescriptors() {
    return DESCRIPTORS;
  }

  @NotNull
  @Override
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Custom Postfix Templates";
  }
}