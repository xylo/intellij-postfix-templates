package de.endrullis.idea.postfixtemplates.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Collection utilities.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class CollectionUtils {

	public static <T> Set<T> _Set(Collection<T> values) {
		return new HashSet<>(values);
	}

	public static <T> Set<T> _Set(T... values) {
		return _Set(Arrays.asList(values));
	}

}
