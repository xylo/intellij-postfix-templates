package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.ui.CheckedTreeNode;
import de.endrullis.idea.postfixtemplates.language.CptLang;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class FileTreeNode extends CheckedTreeNode {
  @NotNull
  private final CptLang        lang;
  @NotNull
  private       CptVirtualFile file;

  @NotNull
  public CptVirtualFile getFile() {
    return file;
  }

  @NotNull
  public CptLang getLang() {
    return lang;
  }

	FileTreeNode(@NotNull CptLang lang, @NotNull CptVirtualFile file) {
    super(file.getName());
    this.lang = lang;
    this.file = file;
  }

  @Override
  public String toString() {
    return file.getName().replaceAll("\\.postfixTemplates", "");
  }

}
