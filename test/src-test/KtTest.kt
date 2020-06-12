package de.endrullis.idea.postfixtemplates.templates

class KtTest {
	fun invert(a: Boolean) = !a

	fun f() {
		invert(true.exm<caret>)
	}
}
