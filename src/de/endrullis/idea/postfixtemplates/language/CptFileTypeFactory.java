package de.endrullis.idea.postfixtemplates.language;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class CptFileTypeFactory extends FileTypeFactory {
  @Override
  public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
    fileTypeConsumer.consume(CptFileType.INSTANCE);
  }
}