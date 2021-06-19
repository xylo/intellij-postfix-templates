package de.endrullis.idea.postfixtemplates.languages.rust;

import scala.reflect.api.FlagSets;

import java.util.Set;
import java.util.stream.Collectors;

import static de.endrullis.idea.postfixtemplates.utils.CollectionUtils._Set;

/**
 * Special types representing groups of Java types/classes.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public enum RustType {

	ANY, i8, i16, i32, i64, i128, isize, u8, u16, u32, u64, u128, usize, f32, f64, bool, char_, str, tuple, array, integer, unsigned, float_, number;

	private static Set<RustType> INTEGER_TYPES = _Set(i8, i16, i32, i64, i128);
	private static Set<RustType> UNSIGNED_TYPES = _Set(u8, u16, u32, u64, u128);
	private static Set<RustType> FLOAT_TYPES = _Set(f32, f64);

	private static final Set<String> INTEGER_TYPES_AS_STRING = INTEGER_TYPES.stream().map(t -> t.fixedName()).collect(Collectors.toSet());
	private static final Set<String> UNSIGNED_TYPES_AS_STRING = UNSIGNED_TYPES.stream().map(t -> t.fixedName()).collect(Collectors.toSet());
	private static final Set<String> FLOAT_TYPES_AS_STRING = FLOAT_TYPES.stream().map(t -> t.fixedName()).collect(Collectors.toSet());

	public static boolean isInteger(String type) {
		return INTEGER_TYPES_AS_STRING.contains(type);
	}

	public static boolean isUnsigned(String type) {
		return UNSIGNED_TYPES_AS_STRING.contains(type);
	}

	public static boolean isFloat(String type) {
		return FLOAT_TYPES_AS_STRING.contains(type);
	}

	public static boolean isNumber(String type) {
		return isInteger(type) || isUnsigned(type) || isFloat(type);
	}

	public String fixedName() {
		return this.name().replaceFirst("_$", "");
	}

}
