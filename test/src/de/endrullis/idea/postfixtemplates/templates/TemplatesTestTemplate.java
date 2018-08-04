package de.endrullis.idea.postfixtemplates.templates;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Template file for testing all postfix templates.
 *
 * @author Stefan Endrullis &lt;stefan@endrullis.de&gt;
 */
public class TemplatesTestTemplate {

	public static void main(String[] args) throws FileNotFoundException, MalformedURLException {
		String[] array = new String[0];
		Set<String> set = new HashSet<>();
		List<String> list = new ArrayList<>();
		Map<String,String> map = new HashMap<>();
		Collection<String> collection = list;
		Iterable<String> iterable = list;
		Optional<String> optional = Optional.empty();
		Stream<String> stream = list.stream();
		String string = "";
		Integer number = 1;
		int n = 1;
		Object object = null;
		File file = new File(".");
	  Path path = file.toPath();
		InputStream in = new FileInputStream(file);
		URL url = new URL("");
		Runnable runnable = () -> {};
		Supplier             supplier = null;
		BooleanSupplier      booleanSupplier = null;
		DoubleSupplier       doubleSupplier = null;
		IntSupplier          intSupplier = null;
		LongSupplier         longSupplier = null;
		Consumer             consumer = null;
		BiConsumer           biConsumer = null;
		DoubleConsumer       doubleConsumer = null;
		IntConsumer          intConsumer = null;
		LongConsumer         longConsumer = null;
		ObjDoubleConsumer    objDoubleConsumer = null;
		ObjIntConsumer       objIntConsumer = null;
		ObjLongConsumer      objLongConsumer = null;
		Predicate            predicate = null;
		BiPredicate          biPredicate = null;
		DoublePredicate      doublePredicate = null;
		IntPredicate         intPredicate = null;
		LongPredicate        longPredicate = null;
		
	  // PLACEHOLDER
	}

	private static void f(ExceptionRunnable runnable) {}
	private static void f(ExceptionSupplier runnable) {}

	public interface ExceptionRunnable {
		void run() throws Exception;
	}

	public interface ExceptionSupplier {
		Object run() throws Exception;
	}

}
