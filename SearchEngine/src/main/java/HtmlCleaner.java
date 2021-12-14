import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleans simple, validating HTML 4/5 into plain text.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class HtmlCleaner {

	/**
	 * Removes all HTML tags and certain block elements from the provided text.
	 * The block elements removed include: head, style, script, noscript, iframe,
	 * and svg.
	 *
	 * @param html the HTML to strip tags and elements from
	 * @return text clean of any HTML tags and certain block elements
	 */
	public static String stripHtml(String html) {
		html = stripBlockElements(html);
		html = stripTags(html);
		html = stripEntities(html);
		return html;
	}

	/**
	 * Removes comments and certain block elements from the provided html. The block
	 * elements removed include: head, style, script, noscript, iframe, and svg.
	 *
	 * @param html the HTML to strip comments and block elements from
	 * @return text clean of any comments and certain HTML block elements
	 */
	public static String stripBlockElements(String html) {
		html = stripComments(html);
		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");
		html = stripElement(html, "noscript");
		html = stripElement(html, "iframe");
		html = stripElement(html, "svg");
		return html;
	}

	/**
	 * Replaces all HTML entities with an empty string. For example,
	 * "2010&ndash;2012" will become "20102012".
	 *
	 * @param html text including HTML entities to remove
	 * @return text without any HTML entities
	 */
	public static String stripEntities(String html) {
		String result = html;
		Pattern pattern = Pattern.compile("&[^ ]*?;", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(html);
		
		while (matcher.find()) {
			result = matcher.replaceAll("");
		}
		
		return result;
	}

	/**
	 * Replaces all HTML tags with an empty string. For example, "A<b>B</b>C" will
	 * become "ABC".
	 *
	 * @param html text including HTML tags to remove
	 * @return text without any HTML tags
	 */
	public static String stripTags(String html) {
		String result = html;
		Pattern pattern = Pattern.compile("(?s)<.*?>", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(html);
		
		while (matcher.find()) {
			result = matcher.replaceAll("");
		}
		
		return result;
	}

	/**
	 * Replaces all HTML comments with a single space if the comment tags span
	 * multiple lines. Otherwise, replaces with an empty string. For example:
	 *
	 * <pre>
	 * A&lt;!-- B --&gt;C
	 * </pre>
	 *
	 * ...will become "AC" but this comment:
	 *
	 * <pre>
	 * A&lt;!--
	 * B --&gt;C
	 * </pre>
	 *
	 * ...will become"A C" instead because it spanned multiple lines. Note that this
	 * only considers newlines within the comment, not the surrounding HTML.
	 *
	 * @param html text including HTML comments to remove
	 * @return text without any HTML comments
	 *
	 * @see Matcher#replaceAll(java.util.function.Function)
	 */
	public static String stripComments(String html) {
		String result = html;
		Pattern pattern = Pattern.compile("(?s)(<!--.*?-->)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(html);
		
		while (matcher.find()) {
			String match = matcher.group(1);
			if (match.contains("\n")) {
				result = result.replace(match, " ");
			}
			else {
				result = result.replace(match, "");
			}
		}
		
		return result;
	}

	/**
	 * Replaces everything between the element tags and the element tags
	 * themselves with a single space if the tags span multiple lines. Otherwise,
	 * replaces with an empty string. For example, consider the html code: *
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed, and
	 * replaced with an empty string. Note that this only considers newlines
	 * within the element tags, not the surrounding HTML.
	 *
	 * @param html text including HTML elements to remove
	 * @param name name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 *
	 * @see Matcher#replaceAll(java.util.function.Function)
	 */
	public static String stripElement(String html, String name) {
		String result = html;
		Pattern pattern = Pattern.compile("(?s)(<" + name + ".*?</" + name + " *?>)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(html);
		
		while (matcher.find()) {
			String match = matcher.group(1);
			if (match.contains("\n")) {
				result = result.replace(match, " ");
			}
			else {
				result = result.replace(match, "");
			}
		}

		return result;
	}
	
	/**
	 * Returns true if the provided input has one or more newlines.
	 * 
	 * @param input the input text to test against
	 * @return true if the input has one or more newlines
	 */
	public static boolean hasNewline(String input) {
		return input.matches(".*[\n\r]+.*");
	}
}
