package de.endrullis.idea.postfixtemplates.language;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import de.endrullis.idea.postfixtemplates.language.psi.CptFile;
import org.jetbrains.annotations.NotNull;

public class CptStructureViewModel extends StructureViewModelBase implements
    StructureViewModel.ElementInfoProvider {
  public CptStructureViewModel(PsiFile psiFile) {
    super(psiFile, new CptStructureViewElement(psiFile));
  }

  @NotNull
  public Sorter[] getSorters() {
    return new Sorter[]{Sorter.ALPHA_SORTER};
  }


  @Override
  public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
    return false;
  }

  @Override
  public boolean isAlwaysLeaf(StructureViewTreeElement element) {
    return element instanceof CptFile;
  }
}