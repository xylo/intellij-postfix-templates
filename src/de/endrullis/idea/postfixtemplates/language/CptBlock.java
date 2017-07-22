package de.endrullis.idea.postfixtemplates.language;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CptBlock extends AbstractBlock {
	private SpacingBuilder spacingBuilder;

	private Indent indent;

	protected CptBlock(@NotNull ASTNode node, @Nullable Wrap wrap, Indent indent, @Nullable Alignment alignment,
	                   SpacingBuilder spacingBuilder) {
		super(node, wrap, alignment);
		this.indent = indent;
		this.spacingBuilder = spacingBuilder;
	}

	@Override
	protected List<Block> buildChildren() {
		List<Block> blocks = new ArrayList<Block>();
		ASTNode child = myNode.getFirstChildNode();
		while (child != null) {
			Indent indent = child.getElementType() == CptTypes.MAPPINGS
				? Indent.getIndent(Indent.Type.NORMAL, true, true)
				: Indent.getNoneIndent();

			if (child.getElementType() != TokenType.WHITE_SPACE) {
				Block block = new CptBlock(child, Wrap.createWrap(WrapType.NONE, false), indent, Alignment.createAlignment(),
					spacingBuilder);
				blocks.add(block);
			}
			child = child.getTreeNext();
		}
		return blocks;
	}

	@Override
	public Indent getIndent() {
		return indent;
	}

	@Nullable
	@Override
	public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
		return spacingBuilder.getSpacing(this, child1, child2);
	}

	@Override
	public boolean isLeaf() {
		return myNode.getFirstChildNode() == null;
	}
}
