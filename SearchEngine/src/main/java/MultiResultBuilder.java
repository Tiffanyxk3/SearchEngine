import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Build the search result using multiple threads
 * 
 * @author tiffanyz
 */
public class MultiResultBuilder implements ResultBuilderInterface {
	/** Logger to use for this class. */
	private static final Logger log = LogManager.getRootLogger();
	/** the inverted index to build the search result */
	final private SafeInvertedIndex index;
	/** search result instance used to store */
	final private Map<String, ArrayList<InvertedIndex.SearchResult>> result;
	/** the work queue to do the tasks */
	final private WorkQueue queue;

	/**
	 * Initializes the instance data
	 *  
	 * @param index the thread safe inverted index to use to build the result
	 * @param queue the work queue to use
	 */
	public MultiResultBuilder(SafeInvertedIndex index, WorkQueue queue) {
		this.index = index;
		this.result = new TreeMap<String, ArrayList<InvertedIndex.SearchResult>>();
		this.queue = queue;
	}
	
	@Override
	public void build(Path file, boolean exact) throws IOException {
		ResultBuilderInterface.super.build(file, exact);
		queue.finish();
	}
	
	@Override
	public void addLine(String line, boolean exact) {
		queue.execute(new Task(line, exact));
	}
	
	@Override
	public void toJsonResult(Path file) throws IOException {
		synchronized (result) {
			JsonWriter.writeResultJson(result, file);
		}
		
	}
	
	/**
	 * Update the shared search result
	 */
	public class Task implements Runnable {
		/** line to be added */
		private final String line;
		/** indicates whether to do an exact search */
		private final boolean exact;
		
		/**
		 * Initializes the instance data
		 * 
		 * @param line to be searched on
		 * @param exact indicates whether to do an exact search
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
			log.debug("Task for {} created.", line, exact);
		}

		@Override
		public void run() {
			TreeSet<String> stems = TextStemmer.uniqueStems(line);
			String joined = String.join(" ", stems);
			
			if (!stems.isEmpty()) {
				synchronized (result) {
					if (result.containsKey(joined)) {
						return;
					}
				}
				ArrayList<InvertedIndex.SearchResult> entries = index.search(stems, exact);
				synchronized (result) {
					result.put(joined, entries);
				}
			}
		}
	}
}
