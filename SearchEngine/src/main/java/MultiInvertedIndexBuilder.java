import java.io.IOException;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Build the given inverted index using multiple threads
 * 
 * @author tiffanyz
 */
public class MultiInvertedIndexBuilder extends InvertedIndexBuilder {
	/** Logger to use for this class. */
	private static final Logger log = LogManager.getRootLogger();
	/** inverted index to store the result */
	private final SafeInvertedIndex index;
	/** the work queue to do the tasks */
	private final WorkQueue queue;
	
	/**
	 * Initializes the instance data
	 * 
	 * @param index the thread safe inverted index to build on
	 * @param queue the work queue to use
	 */
	public MultiInvertedIndexBuilder(SafeInvertedIndex index, WorkQueue queue) {
		super(index);
		this.index = index;
		this.queue = queue;
	}
	
	@Override
	public void build(Path startPath) throws IOException {
		super.build(startPath);
		queue.finish();
	}
	
	@Override
	public void addFile(Path path) {
		queue.execute(new Task(path));
	}
	
	/**
	 * Update the shared index
	 */
	public class Task implements Runnable {
		/** file to be added */
		private final Path file;
		
		/**
		 * Initializes the instance data
		 * 
		 * @param file given file to be added
		 */
		public Task(Path file) {
			this.file = file;
			log.debug("index Task for {} created.", file);
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			try {
				InvertedIndexBuilder.addFile(file, local);
			} catch (IOException e) {
				log.error("Unable to add local index. ");
			}
			
			index.addAll(local);
		}
	}
}