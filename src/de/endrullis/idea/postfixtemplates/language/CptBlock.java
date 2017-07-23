package de.endrullis.idea.postfixtemplates.language;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import de.endrullis.idea.postfixtemplates.language.psi.CptTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CptBlock extends AbstractBlock {
	private SpacingBuilder spacingBuilder;

	private Indent indent;
	private Indent childIndent;

	private Alignment mapAlignment = Alignment.createAlignment(false, Alignment.Anchor.LEFT);

	protected CptBlock(@NotNull ASTNode node, @Nullable Wrap wrap, Indent indent, Indent childIndent, @Nullable Alignment alignment,
	                   SpacingBuilder spacingBuilder) {
		super(node, wrap, alignment);
		this.indent = indent;
		this.childIndent = childIndent;
		this.spacingBuilder = spacingBuilder;
	}

	@Override
	protected List<Block> buildChildren() {
		List<Block> blocks = new ArrayList<Block>();
		ASTNode child = myNode.getFirstChildNode();
		while (child != null) {
			IElementType elementType = child.getElementType();

			Indent indent = elementType == CptTypes.MAPPINGS
				? Indent.getNormalIndent()
				: Indent.getNoneIndent();

			Indent childIndent = elementType == CptTypes.TEMPLATE
				? Indent.getNormalIndent()
				: Indent.getNoneIndent();

			if (elementType != TokenType.WHITE_SPACE) {
				Alignment alignment = null; //Alignment.createAlignment();

				/*
				if (elementType == CptTypes.MAP) {
					alignment = mapAlignment;
				}
				*/

				Block block = new CptBlock(child, Wrap.createWrap(WrapType.NONE, false), indent, childIndent, alignment, spacingBuilder);
				blocks.add(block);
			}

			child = child.getTreeNext();
		}
		return blocks;
	}

	@Nullable
	@Override
	protected Indent getChildIndent() {
		return childIndent;
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
