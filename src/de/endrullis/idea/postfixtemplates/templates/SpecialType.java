package de.endrullis.idea.postfixtemplates.templates;

/**
 * Special types representing groups of Java types/classes.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public enum SpecialType {

	ANY, VOID, NON_VOID, ARRAY, BOOLEAN, ITERABLE_OR_ARRAY, NOT_PRIMITIVE, NUMBER, CLASS,

	//FIELD, LOCAL_VARIABLE, VARIABLE, ASSIGNMENT,

	BYTE, SHORT, CHAR, INT, LONG, FLOAT, DOUBLE,
	BYTE_LITERAL, SHORT_LITERAL, CHAR_LITERAL, INT_LITERAL, LONG_LITERAL, FLOAT_LITERAL, DOUBLE_LITERAL,
	NUMBER_LITERAL, STRING_LITERAL,

	MATH, TEXT

}
