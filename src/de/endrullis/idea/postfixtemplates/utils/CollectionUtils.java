package de.endrullis.idea.postfixtemplates.utils;

import java.util.*;

/**
 * Collection utilities.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CollectionUtils {

	public static <T> Set<T> _Set(Collection<T> values) {
		return new HashSet<>(values);
	}

	@SafeVarargs
	public static <T> Set<T> _Set(T... values) {
		return _Set(Arrays.asList(values));
	}

	@SafeVarargs
	public static <T> List<T> _List(T... values) {
		return Arrays.asList(values);
	}

}
