import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Build the given inverted index from web
 * 
 * @author tiffanyz
 */
public class WebInvertedIndexBuilder {
	/** Logger to use for this class. */
	private static final Logger log = LogManager.getRootLogger();
	/** inverted index to store the result */
	private final SafeInvertedIndex index;
	/** the work queue to do the tasks */
	private final WorkQueue queue;
	/** the total number of URLs to crawl */
	private final int max;
	/** the list of URLs to crawl */
	private final Set<URL> urls;
	
	/**
	 * Initializes the instance data
	 * 
	 * @param index the thread safe inverted index to build
	 * @param queue the work queue to use
	 * @param max the total number of URLs to crawl
	 */
	public WebInvertedIndexBuilder(SafeInvertedIndex index, WorkQueue queue, int max) {
		this.index = index;
		this.queue = queue;
		this.max = max;
		this.urls = new HashSet<>();
	}
	
	/**
	 * Build from the seed URL
	 * 
	 * @param seed URL to build from
	 * @throws IOException if an IO error occurs
	 */
	public void build(URL seed) throws IOException {
		urls.add(seed);
		queue.execute(new Task(seed));
		queue.finish();
	}
	
	/**
	 * Update the shared index
	 */
	public class Task implements Runnable {
		/** url to crawl */
		private final URL seed;
		
		/**
		 * Initializes the instance data
		 * 
		 * @param seed the seed URL to crawl
		 */
		public Task(URL seed) {
			this.seed = seed;
			log.debug("Task for {}. created.", seed);
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			String html = HtmlFetcher.fetch(seed, 3);
			if (html == null) {
				return;
			}
			html = HtmlCleaner.stripBlockElements(html);
			
			int size = 0;
			synchronized (urls) {
				size = urls.size();
			}
			if (size < max) {
				ArrayList<URL> links = LinkParser.getValidLinks(seed, html);
				synchronized (urls) {
					for (URL link : links) {
						if (urls.size() == max) {
							break;
						}
						if (!urls.contains(link)) {
							urls.add(link);
							queue.execute(new Task(link));
						}
					}
				}
			}
			
			String striped = HtmlCleaner.stripHtml(html);
			ArrayList<String> stemmed = TextStemmer.listStems(striped);
			for (int i = 0; i < stemmed.size(); i++) {
				local.add(seed.toString(), stemmed.get(i), i);
			}
			
			index.addAll(local);
		}
	}
}
