import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Indexing the UNIQUE words that were found in a text file.
 *
 * @author tiffanyz
 */
public class InvertedIndex {
	/** Declaration and initialization of the index */
	private final TreeMap<String, Map<String, Set<Integer>>> index;
	/** a map to store the word count */
	private final Map<String, Integer> counts;

	/**
	 * Constructor: creates a new map
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
		this.counts = new TreeMap<>();
	}

	/**
	 * Adds the location and the words with their positions.
	 *
	 * @param location where the words are found
	 * @param words the words found in the given location
	 */
	public void add(String location, ArrayList<String> words) {
		for (int i = 0; i < words.size(); i++) {
			add(location, words.get(i), i);
		}
	}
	
	/**
	 * Adds the location and the word with its position.
	 * 
	 * @param location where the word is found
	 * @param word the word found in the given location
	 * @param position the index of where the word is found in the given location
	 */
	public void add(String location, String word, int position) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(location, new TreeSet<>());

		if (index.get(word).get(location).add(position+1)) {
			counts.put(location, counts.getOrDefault(location, 0) + 1);
		}
	}
	
	/**
	 * Adds all elements in other inverted index into the current index
	 * 
	 * @param other index to be added
	 */
	public void addAll(InvertedIndex other) {
		for (String word : other.index.keySet()) {
			if (!index.containsKey(word)) {
				index.put(word, other.index.get(word));
			}
			else {
				for (String location : other.index.get(word).keySet()) {
					if (!index.get(word).containsKey(location)) {
						index.get(word).put(location, other.index.get(word).get(location));
					}
					else {
						index.get(word).get(location).addAll(other.index.get(word).get(location));
					}
				}
			}
		}

		for (String location : other.counts.keySet()) {
			counts.putIfAbsent(location, other.counts.get(location));
		}
	}
	
	/**
	 * @return number of words in the index
	 */
	public int numWords() {
		return index.size();
	}
	
	/**
	 * @param word used to search
	 * @return number of locations that the given word appears
	 */
	public int numLocations(String word) {
		return hasWord(word) ? index.get(word).size() : 0;
	}
	
	/**
	 * @param word used to search
	 * @param location used to search
	 * @return number of times the word appears in the given location
	 */
	public int numPositions(String word, String location) {
		return hasLocation(word, location) ? index.get(word).get(location).size() : 0;
	}
	
	/**
	 * Checks whether the index contains the word
	 * 
	 * @param word to search on
	 * @return true if word is in the index
	 */
	public boolean hasWord(String word) {
		return index.containsKey(word);
	}
	
	/**
	 * Checks whether the location exists under word
	 * 
	 * @param word used to search
	 * @param location used to search
	 * @return true if the given word appears in the given location
	 */
	public boolean hasLocation(String word, String location) {
		return index.containsKey(word) && index.get(word).containsKey(location);
	}
	
	/**
	 * @param word used to search
	 * @param location used to search
	 * @param position used to search
	 * @return true if position exists in the index under word and location
	 */
	public boolean hasPosition(String word, String location, int position) {
		return index.containsKey(word) && index.get(word).containsKey(location) && index.get(word).get(location).contains(position);
	}
	
	/**
	 * @return a set of all the words in the index
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(index.keySet());
	}
	
	/**
	 * @param word used to search
	 * @return a set of all the locations that the given word appears
	 */
	public Set<String> getLocations(String word) {
		return hasWord(word) ? Collections.unmodifiableSet(index.get(word).keySet()) : Collections.emptySet();
	}
	
	/**
	 * @param word used to search
	 * @param location used to search
	 * @return a set of all the positions that the given word appears in the given location
	 */
	public Set<Integer> getPositions(String word, String location) {
		return hasLocation(word, location) ? Collections.unmodifiableSet(index.get(word).get(location)) : Collections.emptySet();
	}
	
	/**
	 * @return a set of all the locations in the counts
	 */
	public Set<String> getCountsLocations() {
		return Collections.unmodifiableSet(counts.keySet());
	}
	
	/**
	 * @param location to get the count
	 * @return the number of total word count in the location
	 */
	public int getCount(String location) {
		return counts.getOrDefault(location, 0);
	}
	
	/**
	 * Do exact search
	 * 
	 * @param queries to do the search on
	 * @return a list of exact search results
	 */
	public ArrayList<SearchResult> exactSearch(Set<String> queries) {
		ArrayList<SearchResult> entries = new ArrayList<>();
		Map<String, SearchResult> lookupMap = new HashMap<>();
		for (String query : queries) {
			if (index.containsKey(query)) {
				performSearch(entries, query, lookupMap);
			}
		}		
		Collections.sort(entries);
		
		return entries;
	}
	
	/**
	 * Do partial search
	 * 
	 * @param queries to do the search on
	 * @return a list of partial search results
	 */
	public ArrayList<SearchResult> partialSearch(Set<String> queries) {
		ArrayList<SearchResult> entries = new ArrayList<>();
		Map<String, SearchResult> lookupMap = new HashMap<>();
		for (String query : queries) {
			for (var entrySet : index.tailMap(query).entrySet()) {
				String word = entrySet.getKey();
				if (!word.startsWith(query)) {
					break;
				}
				assert word.startsWith(query);
				performSearch(entries, word, lookupMap);
				
			}
		}
		Collections.sort(entries);
		
		return entries;
	}
	
	/**
	 * Do the searching
	 * 
	 * @param queries to do the search on
	 * @param exact indicating whether or not to do a exact search
	 * @return a list of search results
	 */
	public ArrayList<SearchResult> search(Set<String> queries, boolean exact) {
		return exact ? exactSearch(queries) : partialSearch(queries);
	}
	
	/**
	 * Similar algorithm for exact search and partial search
	 * 
	 * @param entries to perform the search
	 * @param word found in the index to perform the search on
	 * @param lookupMap map to look up
	 */
	private void performSearch(ArrayList<SearchResult> entries, String word, Map<String, SearchResult> lookupMap) {
		for (String location : getLocations(word)) {
			SearchResult entry = null;
			
			if (lookupMap.containsKey(location)) {
				entry = lookupMap.get(location);
			}
			else {
				entry = new SearchResult(location);
				entries.add(entry);
				lookupMap.put(location, entry);
			}

			entry.updateValues(word, location);
		}
	}

	@Override
	public String toString() {
		return index.toString();
	}
	
	/**
	 * Write the index into Json format
	 * 
	 * @param file used to output the index in Json format
	 * @throws IOException if exception occurs
	 */
	public void toJsonIndex(Path file) throws IOException {
		JsonWriter.writeIndexJson(index, file);
	}
	
	/**
	 * Write the word count into Json format
	 * 
	 * @param file used to output the index in Json format
	 * @throws IOException if exception occurs
	 */
	public void toJsonCount(Path file) throws IOException {
		JsonWriter.writeCountJson(counts, file);
	}
	

	/**
	 * Storing the search results
	 */
	public class SearchResult implements Comparable<SearchResult> {
		/** The path that is scored in the SearchResult instance */
		final protected String path;
		/** The count that is scored in the SearchResult instance */
		protected int count;
		/** The score that is scored in the SearchResult instance */
		protected double score;
		
		/**
		 * Initializes the instance data
		 * @param path that the result is storing for
		 */
		public SearchResult(String path) {
			this.path = path;
			count = 0;
			score = 0.0;
		}
		
		/**
		 * Update the values of count and score
		 * @param word to be updated
		 * @param location to be updated
		 */
		private void updateValues(String word, String location) {
			this.count += index.get(word).get(path).size();

			int totalCount = counts.get(path);
			double score = (double) count / totalCount;
			this.score = score;
		}
		
		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}
		
		/**
		 * @return the count
		 */
		public int getCount() {
			return count;
		}
		
		/**
		 * @return the score
		 */
		public double getScore() {
			return score;
		}
		
		@Override
		public int compareTo(SearchResult other) {
			if (Double.compare(this.score, other.score) == 0) {
				if (Integer.compare(this.count, other.count) == 0) {
					return String.CASE_INSENSITIVE_ORDER.compare(this.path, other.path);
				}
				return Integer.compare(other.count, this.count);
			}
			return Double.compare(other.score, this.score);
		}
	}

}