import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;


/**
 * The servlet class responsible for setting up a search board.
 * 
 * @author tiffanyz
 */
public class SearchServlet extends HttpServlet {
	/** Default serial version ID (unused) */
	private static final long serialVersionUID = 1L;
	/** The title to use for this webpage. */
	private static final String TITLE = "Searching";
	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();
	/** The thread-safe data structure to use for storing messages. */
	private String input;
	/** Template for starting HTML. */
	private final String headTemplate;
	/** Template for ending HTML. */
	private final String footTemplate;
	/** Template for individual message HTML. */
	private final String textTemplate;
	/** A list storing the user entered queries */
	private final Map<String, ArrayList<String>> entered;
	/** A list storing the results history */
	private final Map<String, ArrayList<String>> history;
	/** A list storing all the saved results */
	private final Map<String, ArrayList<String>> favorites;
	/** The thread-safe inverted index to search on */
	private final SafeInvertedIndex index;
	/** The query entered by user */
	private String query;

	/**
	 * Initializes this message board. Each message board has its own collection
	 * of messages.
	 * 
	 * @param index the thread-safe inverted index to search on
	 * @param history a list storing the results history
	 * @param favorites a list storing all the saved results
	 * @throws IOException if unable to read templates
	 */
	public SearchServlet(SafeInvertedIndex index, Map<String, ArrayList<String>> history, Map<String, ArrayList<String>> favorites) throws IOException {
		super();
		input = null;
		headTemplate = Files.readString(Path.of("html/search-head.html"), StandardCharsets.UTF_8);
		footTemplate = Files.readString(Path.of("html/search-foot.html"), StandardCharsets.UTF_8);
		textTemplate = Files.readString(Path.of("html/search-text.html"), StandardCharsets.UTF_8);
		
		this.entered = new HashMap<>();
		this.history = history;
		this.favorites = favorites;
		this.index = index;
		this.query = null;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());
		values.put("updated", getDate());

		values.put("method", "POST");
		values.put("action", request.getServletPath());

		StringSubstitutor replacer = new StringSubstitutor(values);
		String head = replacer.replace(headTemplate);
		String foot = replacer.replace(footTemplate);

		PrintWriter out = response.getWriter();
		out.println(head);

		if (input == null) {
			out.printf("    <p>No queries.</p>%n");
		}
		else {
			out.println(input);
			
			out.println("  <h1>Search results for <em>\"" + query + "\"</em>:</h1>");
			out.println("    <ul>");
			
			ArrayList<String> links = null;
			synchronized (entered) {
				links = entered.get(query);
			}
			if (links != null) {
				if (links.size() == 0) {
					out.println("No results.");
				}
				else {
					for (String link : links) {
						out.println("<li><a href=\"" + link + "\">" + link + "</a></li>");
					}
				}
			}
			
			out.println("    </ul>");
		}

		out.println(foot);
		out.flush();

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String message = request.getParameter("message");
		String searchType = request.getParameter("searchType");
		String reverse = request.getParameter("reverse");
		String save = request.getParameter("favorite");
		String priv = request.getParameter("private");
		
		boolean exact = false;
		if (searchType.equals("exact")) {
			exact = true;
		}
		message = message == null ? "" : message;

		Set<String> queries = new TreeSet<>();
		ArrayList<SafeInvertedIndex.SearchResult> results = new ArrayList<>();
		ArrayList<String> links = new ArrayList<>();
		
		TreeSet<String> stems = TextStemmer.uniqueStems(message);
		query = String.join(" ", stems);
		queries.add(query);
		
		if (!stems.isEmpty()) {
			results = index.search(stems, exact);
		}
		
		for (SafeInvertedIndex.SearchResult result : results) {
			links.add(result.getPath());
		}
		
		if (reverse != null && reverse.equals("reverse")) {
			Collections.sort(links, Collections.reverseOrder());;
		}
		else {
			Collections.sort(links);
		}
		synchronized (entered) {
			entered.put(query, links);
		}
		
		if (priv == null || !priv.equals("private")) {
			synchronized (history) {
				history.put(query, links);
			}
			if (save != null && save.equals("favorite")) {
				synchronized (favorites) {
					favorites.put(query, links);
				}
			}
		}
		
		message = StringEscapeUtils.escapeHtml4(message);
		Map<String, String> values = new HashMap<>();
		values.put("message", message);
		values.put("timestamp", getDate());
		
		StringSubstitutor replacer = new StringSubstitutor(values);
		input = replacer.replace(textTemplate);

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}
