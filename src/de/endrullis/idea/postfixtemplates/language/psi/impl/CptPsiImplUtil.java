package de.endrullis.idea.postfixtemplates.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.endrullis.idea.postfixtemplates.language.CptIcons;
import de.endrullis.idea.postfixtemplates.language.psi.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@UtilityClass
public class CptPsiImplUtil {

	public static String getTemplateName(CptTemplate element) {
		ASTNode keyNode = element.getNode().findChildByType(CptTypes.TEMPLATE_NAME);
		if (keyNode != null) {
			// IMPORTANT: Convert embedded escaped spaces to simple spaces
			return keyNode.getText().replaceAll("\\\\ ", " ");
		} else {
			return null;
		}
	}

	public static String getTemplateDescription(CptTemplate element) {
		ASTNode valueNode = element.getNode().findChildByType(CptTypes.TEMPLATE_DESCRIPTION);
		if (valueNode != null) {
			return valueNode.getText();
		} else {
			return null;
		}
	}

	public static String getMatchingClassName(CptMapping element) {
		ASTNode keyNode = element.getNode().findChildByType(CptTypes.CLASS_NAME);
		if (keyNode != null) {
			// IMPORTANT: Convert embedded escaped spaces to simple spaces
			return keyNode.getText().replaceAll("\\\\ ", " ");
		} else {
			return null;
		}
	}

	public static String getConditionClassName(CptMapping element) {
		ASTNode firstClassNode = element.getNode().findChildByType(CptTypes.CLASS_NAME);
		if (firstClassNode != null) {
			ASTNode secondClassNode = element.getNode().findChildByType(CptTypes.CLASS_NAME, firstClassNode.getTreeNext());
			if (secondClassNode != null) {
				// IMPORTANT: Convert embedded escaped spaces to simple spaces
				return secondClassNode.getText().replaceAll("\\\\ ", " ");
			}
		}
		return null;
	}

	public static String getReplacementString(CptMapping element) {
		ASTNode valueNode = element.getNode().findChildByType(CptTypes.REPLACEMENT);
		if (valueNode != null) {
			return valueNode.getText();
		} else {
			return null;
		}
	}

	public static String getName(CptTemplate element) {
		return getTemplateName(element);
	}

	public static String getName(CptMapping element) {
		return getMatchingClassName(element);
	}

	public static PsiElement setName(CptTemplate element, String newName) {
		ASTNode keyNode = element.getNode().findChildByType(CptTypes.TEMPLATE_NAME);
		if (keyNode != null) {
			CptTemplate property = CptElementFactory.createTemplate(element.getProject(), newName, "");
			ASTNode newKeyNode = property.getFirstChild().getNode();
			element.getNode().replaceChild(keyNode, newKeyNode);
		}
		return element;
	}

	public static PsiElement setName(CptMapping element, String newName) {
		ASTNode keyNode = element.getNode().findChildByType(CptTypes.CLASS_NAME);
		if (keyNode != null) {
			CptMapping property = CptElementFactory.createMapping(element.getProject(), newName, "");
			ASTNode newKeyNode = property.getFirstChild().getNode();
			element.getNode().replaceChild(keyNode, newKeyNode);
		}
		return element;
	}

	public static PsiElement getNameIdentifier(CptNamedElement element) {
		ASTNode keyNode = element.getNode().findChildByType(CptTypes.CLASS_NAME);
		if (keyNode != null) {
			return keyNode.getPsi();
		} else {
			return null;
		}
	}

	public static ItemPresentation getPresentation(final CptTemplate element) {
		return new ItemPresentation() {
			@Nullable
			@Override
			public String getPresentableText() {
				return element.getTemplateName();
			}

			@Nullable
			@Override
			public String getLocationString() {
				PsiFile containingFile = element.getContainingFile();
				return containingFile == null ? null : containingFile.getName();
			}

			@Override
			public Icon getIcon(boolean unused) {
				return CptIcons.FILE;
			}
		};
	}

	public static ItemPresentation getPresentation(final CptMapping element) {
		return new ItemPresentation() {
			@Nullable
			@Override
			public String getPresentableText() {
				return element.getMatchingClassName();
			}

			@Nullable
			@Override
			public String getLocationString() {
				PsiFile containingFile = element.getContainingFile();
				return containingFile == null ? null : containingFile.getName();
			}

			@Override
			public Icon getIcon(boolean unused) {
				return CptIcons.FILE;
			}
		};
	}
}
