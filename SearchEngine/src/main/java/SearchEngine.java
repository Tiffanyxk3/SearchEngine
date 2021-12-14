import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Demonstrates how to create a simple message board using Jetty and servlets,
 * as well as how to initialize servlets when you need to call its constructor.
 */
public class SearchEngine {
	/**
	 * Sets up a Jetty server with different servlet instances.
	 * 
	 * @param index the thread safe inverted index to build
	 * @param resultBuilder the result build used to build the search result
	 * @param port the port to run this server
	 * 
	 * @throws Exception if unable to start and run server
	 */
	public static void build(SafeInvertedIndex index, int port) throws Exception {
		Server server = new Server(port);
		ServletHandler handler = new ServletHandler();
		Map<String, ArrayList<String>> history = new HashMap<>();
		Map<String, ArrayList<String>> favorites = new HashMap<>();
		
		handler.addServletWithMapping(new ServletHolder(new SearchServlet(index, history, favorites)), "/search");
		handler.addServletWithMapping(new ServletHolder(new ResultServlet(history)), "/result");
		handler.addServletWithMapping(new ServletHolder(new FavoritesServlet(favorites)), "/favorites");
		handler.addServletWithMapping(new ServletHolder(new IndexServlet(index)), "/index");
		handler.addServletWithMapping(new ServletHolder(new LocationServlet(index)), "/location");

		server.setHandler(handler);
		server.start();
		server.join();
	}
}
