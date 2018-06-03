package de.endrullis.idea.postfixtemplates.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Simple tuple.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Tuple2<T1, T2> {

	public T1 _1;
	public T2 _2;

}
