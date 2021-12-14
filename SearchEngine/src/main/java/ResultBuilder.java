import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Building the search result
 * 
 * @author tiffanyz
 */
public class ResultBuilder implements ResultBuilderInterface {
	/** the inverted index to build the search result */
	final private InvertedIndex index;
	/** search result instance used to store */
	final private Map<String, ArrayList<InvertedIndex.SearchResult>> result;
	
	/**
	 * Initializes the instance data
	 * 
	 * @param index inverted index to use to build the result
	 */
	public ResultBuilder(InvertedIndex index) {
		this.index = index;
		this.result = new TreeMap<String, ArrayList<InvertedIndex.SearchResult>>();
	}
	
	/**
	 * Build a line in the file
	 * 
	 * @param line to build the search result
	 * @param exact indicates whether to do an exact search or partial search
	 */
	public void addLine(String line, boolean exact) {		
		TreeSet<String> stems = TextStemmer.uniqueStems(line);
		String joined = String.join(" ", stems);
		
		if (!stems.isEmpty() && !result.containsKey(joined)) {
			ArrayList<InvertedIndex.SearchResult> entries = index.search(stems, exact);
			result.put(joined, entries);
		}
	}
	
	/**
	 * Write the result into Json format
	 * 
	 * @param file used to output the index in Json format
	 * @throws IOException if exception occurs
	 */
	public void toJsonResult(Path file) throws IOException {
		JsonWriter.writeResultJson(result, file);
	}
}
