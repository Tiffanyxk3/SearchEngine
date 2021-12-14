import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;

/**
 * Driver class for CS212 - Fall2020 projects
 *
 * @author tiffanyz
 *
 */
public class Driver {	
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		LogManager.shutdown();;
		if (args.length == 0) {
			return;
		}
		ArgumentMap inputMap = new ArgumentMap(args);
		
		InvertedIndex index = new InvertedIndex();
		SafeInvertedIndex safeIndex = new SafeInvertedIndex();
		InvertedIndexBuilder indexBuilder;
		ResultBuilderInterface resultBuilder;
		
		WorkQueue queue = null;
		
		if (inputMap.hasFlag("-threads") || inputMap.hasFlag("-url") || inputMap.hasFlag("-server")) {
			int threads = inputMap.getInteger("-threads", 5);
			if (threads < 1) {
				System.out.println("Invalid number of threads. ");
				return;
			}
			queue = new WorkQueue(threads);
			index = safeIndex;
			indexBuilder = new MultiInvertedIndexBuilder(safeIndex, queue);
			resultBuilder = new MultiResultBuilder(safeIndex, queue);
		}
		else {
			indexBuilder = new InvertedIndexBuilder(index);
			resultBuilder = new ResultBuilder(index);
		}
		
		if (inputMap.hasFlag("-url")) {			
			URL inputURL = null;
			try {
				inputURL = inputMap.getURL("-url");
			} catch (MalformedURLException e) {
				System.out.println("Invalid input of URL. ");
			}
			int max = 1;
			if (inputMap.hasFlag("-max")) {
				max = inputMap.getInteger("-max", 1);
			}
			WebInvertedIndexBuilder webCrawler = new WebInvertedIndexBuilder(safeIndex, queue, max);
			try {
				webCrawler.build(inputURL);
			}
			catch (Exception e) {
				System.out.println("Unable to work on the given input URL: " + inputURL);
			}
		}
		
		if (inputMap.hasFlag("-path")) {
			Path inputPath = inputMap.getPath("-path");
			try {
				indexBuilder.build(inputPath);
			}
			catch (Exception e) {
				System.out.println("Unable to work on the given input directory: " + inputPath);
			}
		}
		
		if (inputMap.hasFlag("-queries")) {
			Path queryPath = inputMap.getPath("-queries");
			try {
				resultBuilder.build(queryPath, inputMap.hasFlag("-exact"));
			}
			catch (Exception e) {
				System.out.println("Unable to work on the given queries file: " + queryPath);
			}
		}
		
		if (inputMap.hasFlag("-index")) {
			Path indexPath = inputMap.getPath("-index", "index.json");
			try {
				index.toJsonIndex(indexPath);
			}
			catch (IOException e) {
				System.out.println("Unable to write index in Json format into:  " + indexPath);
			}
		}
		
		if (inputMap.hasFlag("-counts")) {
			Path countsPath = inputMap.getPath("-counts", "counts.json");
			try {
				index.toJsonCount(countsPath);
			}
			catch (IOException e) {
				System.out.println("Unable to write counts in Json format into: " + countsPath);
			}
		}
		
		if (inputMap.hasFlag("-results")) {
			Path resultsPath = inputMap.getPath("-results", "results.json");
			try {
				resultBuilder.toJsonResult(resultsPath);
			}
			catch (IOException e) {
				System.out.println("Unable to write resuls in Json format into: " + resultsPath);
			}
		}
		
		if (inputMap.hasFlag("-server")) {
			int port = inputMap.getInteger("-server", 8080);
			try {
				SearchEngine.build(safeIndex, port);
			} catch (Exception e) {
				System.out.println("Unable to work on the server. ");
			}
		}	

		if (queue != null) {
			queue.shutdown();
		}
	}
}