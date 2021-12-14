import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringSubstitutor;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * The servlet class responsible for setting up an index board.
 * 
 * @author tiffanyz
 */
public class IndexServlet extends HttpServlet {
	/** Default serial version ID (unused) */
	private static final long serialVersionUID = 1L;
	/** The title to use for this webpage. */
	private static final String TITLE = "Inverted Index";
	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();
	/** Template for starting HTML. **/
	private final String headTemplate;
	/** Template for ending HTML. **/
	private final String footTemplate;
	/** The thread-safe inverted index to display */
	private final SafeInvertedIndex index;
	
	/**
	 * Initializes this index boards.
	 * 
	 * @param index the thread-safe inverted index to search on
	 * @throws IOException if an IO error occurs
	 */
	public IndexServlet(SafeInvertedIndex index) throws IOException {
		super();
		this.index = index;
		headTemplate = Files.readString(Path.of("html/index-head.html"), StandardCharsets.UTF_8);
		footTemplate = Files.readString(Path.of("html/index-foot.html"), StandardCharsets.UTF_8);
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
		
		for (String word : index.getWords()) {
			out.println("<h3>" + word + "</h3>");
			out.println("  <ul>");
			for (String link : index.getLocations(word)) {
				out.println("    <li><a href=\"" + link + "\">" + link + "</a></li>");
			}
			out.println("  </ul>");
			out.print("<br/>");
		}
		
		out.println(foot);
		out.flush();
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
