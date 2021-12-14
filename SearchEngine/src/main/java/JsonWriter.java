import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Outputs a map in JSON format
 * 
 * @author tiffanyz
 *
 */
public class JsonWriter
{
	/**
	 * Writes the elements of index in JSON format.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndexJson(Map<String, Map<String, Set<Integer>>> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterate = elements.keySet().iterator();
		writer.write("{");
		if (iterate.hasNext()) {
			writer.write("\n");
			String key = iterate.next();
			indent(key, writer, level + 1);
			writer.write(": ");
			Map<String, Set<Integer>> values = elements.get(key);
			asNestedCollection(values, writer, level + 1);
		}
		while (iterate.hasNext()) {
			writer.write(",\n");
			String key = iterate.next();
			indent(key, writer, level + 1);
			writer.write(": ");
			Map<String, Set<Integer>> values = elements.get(key);
			asNestedCollection(values, writer, level + 1);
		}
		writer.write("\n}");
	}
	
	/**
	 * Writes the elements of results in JSON format.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeResultJson(Map<String, ArrayList<InvertedIndex.SearchResult>> elements, Writer writer, int level) throws IOException {
		writer.write("{");
		Iterator<String> iterate = elements.keySet().iterator();
		if (iterate.hasNext()) {
			writer.write("\n");
			String key = iterate.next();
			indent(key, writer, level + 1);
			writer.write(": ");
			ArrayList<InvertedIndex.SearchResult> values = elements.get(key);
			asNestedResult(values, writer, level + 1);
		}
		while (iterate.hasNext()) {
			writer.write(",\n");
			String key = iterate.next();
			indent(key, writer, level + 1);
			writer.write(": ");
			ArrayList<InvertedIndex.SearchResult> values = elements.get(key);
			asNestedResult(values, writer, level + 1);
		}
		writer.write("\n}");
	}
	
	/**
	 * Write the result in Json format
	 * 
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asResult(InvertedIndex.SearchResult elements, Writer writer, int level) throws IOException {
		indent(writer, level);
		writer.write("{");
		writer.write("\n");
		indent("where", writer, level + 1);
		writer.write(": ");
		indent(elements.getPath(), writer, 0);
		writer.write(",\n");
		indent("count", writer, level + 1);
		writer.write(": ");
		writer.write(Integer.toString(elements.getCount()));
		writer.write(",\n");
		indent("score", writer, level + 1);
		writer.write(": ");
		writer.write(String.format("%.8f", elements.getScore()));
		writer.write("\n");
		indent(writer, level);
		writer.write("}");
	}
	
	/**
	 * Write the nested result in Json format
	 * 
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedResult(ArrayList<InvertedIndex.SearchResult> elements, Writer writer, int level) throws IOException {
		Iterator<InvertedIndex.SearchResult> iterate = elements.iterator();
		writer.write("[");
		if (iterate.hasNext()) {
			writer.write("\n");
			asResult(iterate.next(), writer, level + 1);
		}
		while (iterate.hasNext()) {
			writer.write(",\n");
			asResult(iterate.next(), writer, level + 1);
		}
		writer.write("\n");
		indent(writer, level);
		writer.write("]");
	}
	
	/**
	 * Writes the elements of results in JSON format.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeCountJson(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterate = elements.keySet().iterator();
		writer.write("{");
		if (iterate.hasNext()) {
			writer.write("\n");
			String key = iterate.next();
			System.out.println(key);
			indent(key, writer, level + 1);
			writer.write(": ");
			Integer value = elements.get(key);
			writer.write(value.toString());
		}
		while (iterate.hasNext()) {
			writer.write(",\n");
			String key = iterate.next();
			indent(key, writer, level + 1);
			writer.write(": ");
			Integer value = elements.get(key);
			writer.write(value.toString());
		}
		writer.write("\n}");
	}
	
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asCollection(Collection<Integer> elements, Writer writer, int level) throws IOException {
		Iterator<Integer> iterate = elements.iterator();
		writer.write("[");
		if (iterate.hasNext()) {
			writer.write("\n");
			indent(iterate.next(), writer, level + 1);
		}
		
		while (iterate.hasNext()) {
			writer.write(",\n");
			indent(iterate.next(), writer, level + 1);

		}
		writer.write("\n");
		indent(writer, level);
		writer.write("]");
	}
	
	/**
	 * Writes the elements as a pretty JSON object with a nested array. The
	 * generic notation used allows this method to be used for any type of map
	 * with any type of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedCollection(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level) throws IOException {
		Iterator <String> iterate = elements.keySet().iterator();
		writer.write("{");
		if (iterate.hasNext()) {
			writer.write("\n");
			String key = iterate.next();
			indent(key, writer, level + 1);
			Collection<Integer> values = elements.get(key);
			writer.write(": ");
			asCollection(values, writer, level + 1);
		}
		
		while (iterate.hasNext()) {
			writer.write(",\n");
			String key = iterate.next();
			indent(key, writer, level + 1);
			Collection<Integer> values = elements.get(key);
			writer.write(": ");
			asCollection(values, writer, level + 1);
		}
		writer.write("\n");
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * Indents using a tab character by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times the number of times to write a tab symbol
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "}
	 * quotation marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param times the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the integer element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param times the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(Number element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element.toString());
	}

	/**
	 * Writes the elements of index in JSON format to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndexJson(Map<String, Map<String, Set<Integer>>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeIndexJson(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements of index in JSON format.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String writeIndexJson(Map<String, Map<String, Set<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeIndexJson(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Writes the elements of results in JSON format to file.
	 *
	 * @param results the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void writeResultJson(Map<String, ArrayList<InvertedIndex.SearchResult>> results, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeResultJson(results, writer, 0);
		}
	}

	/**
	 * Returns the elements of results in JSON format.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String writeResultJson(Map<String, ArrayList<InvertedIndex.SearchResult>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeResultJson(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Writes the elements of word count in JSON format to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void writeCountJson(Map<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeCountJson(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements of word count in JSON format.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String writeCountJson(Map<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeCountJson(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
}