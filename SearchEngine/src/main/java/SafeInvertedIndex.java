import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;

/**
 * Indexing the UNIQUE words that were found in a text file.
 *
 * @author tiffanyz
 */
public class SafeInvertedIndex extends InvertedIndex {
	/** The lock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock lock;

	/**
	 * Constructor: creates a new map
	 */
	public SafeInvertedIndex() {
		super();
		this.lock = new ReadWriteLock();
	}

	@Override
	public void add(String location, ArrayList<String> words) {
		lock.writeLock().lock();
		try {
			super.add(location, words);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void add(String location, String word, int position) {
		lock.writeLock().lock();
		try {
			super.add(location, word, position);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void addAll(InvertedIndex other) {
		lock.writeLock().lock();
		try {
			super.addAll(other);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public int numWords() {
		lock.readLock().lock();

		try {
			return super.numWords();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int numLocations(String word) {
		lock.readLock().lock();

		try {
			return super.numLocations(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int numPositions(String word, String location) {
		lock.readLock().lock();

		try {
			return super.numPositions(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();

		try {
			return super.hasWord(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean hasLocation(String word, String location) {
		lock.readLock().lock();

		try {
			return super.hasLocation(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public boolean hasPosition(String word, String location, int position) {
		lock.readLock().lock();

		try {
			return super.hasPosition(word, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Set<String> getWords() {
		lock.readLock().lock();

		try {
			return super.getWords();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Set<String> getLocations(String word) {
		lock.readLock().lock();

		try {
			return super.getLocations(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Set<Integer> getPositions(String word, String location) {
		lock.readLock().lock();

		try {
			return super.getPositions(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int getCount(String location) {
		lock.readLock().lock();

		try {
			return super.getCount(location);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public ArrayList<SearchResult> exactSearch(Set<String> queries) {
		lock.readLock().lock();
		
		try {
			return super.exactSearch(queries);
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public ArrayList<SearchResult> partialSearch(Set<String> queries) {
		lock.readLock().lock();
		
		try {
			return super.partialSearch(queries);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		
		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public void toJsonIndex(Path file) throws IOException {
		lock.readLock().lock();

		try {
			super.toJsonIndex(file);;
		}
		finally {
			lock.readLock().unlock();
		};
	}
	
	@Override
	public void toJsonCount(Path file) throws IOException {
		lock.readLock().lock();

		try {
			super.toJsonCount(file);;
		}
		finally {
			lock.readLock().unlock();
		}
	}

}