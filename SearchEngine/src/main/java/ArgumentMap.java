import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

/**
 * Parses and stores command-line arguments into simple key-value pairs.
 * 
 * @author tiffanyz
 *
 */
public class ArgumentMap {

	/** The map to store key-value pairs **/
	private final Map<String, String> map;

	/**
	 * Constructing a new map
	 */
	public ArgumentMap() {
		this.map = new HashMap<>();
	}

	/**
	 * Initializes this argument map and then parsers the arguments into
	 * flag/value pairs where possible. Some flags may not have associated values.
	 * If a flag is repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public ArgumentMap(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may
	 * not have associated values. If a flag is repeated, its value is
	 * overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public void parse(String[] args) {
		int pos = 0;
		while (pos < args.length) {
			if (isFlag(args[pos])) {
				if (pos + 1 >= args.length || isFlag(args[pos + 1])) {
					map.put(args[pos], null);
					pos++;
				}
				else {
					map.put(args[pos], args[pos + 1]);
					pos += 2;
				}
			}
			else {
				pos++;
			}
		}
	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-digit character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 * @throws IndexOutOfBoundsException if exception occurs
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 * @see String#charAt(int)
	 * @see Character#isDigit(char)
	 */
	public static boolean isFlag(String arg) {
		if (arg == null || arg.length() == 0 || arg.charAt(0) != '-') {
			return false;
		}
		return !Character.isDigit(arg.charAt(1));
	}

	/**
	 * Determines whether the argument is a value. Anything that is not a flag is
	 * considered a value.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 */
	public static boolean isValue(String arg) {
		return !isFlag(arg);
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		return map.size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag find
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		return map.containsKey(flag);
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		return (map.get(flag) != null);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a
	 * {@link String}, or null if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping
	 */
	public String getString(String flag) {
		return map.get(flag);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a
	 * {@link String}, or the default value if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping
	 * @return the value to which the specified flag is mapped, or the default
	 *         value if there is no mapping
	 */
	public String getString(String flag, String defaultValue) {
		if (map.get(flag) != null) {
			return map.get(flag);
		}
		return defaultValue;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path},
	 * or {@code null} if unable to retrieve this mapping (including being unable
	 * to convert the value to a {@link Path} or no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping
	 *
	 * @see Path#of(String, String...)
	 */
	public Path getPath(String flag) {
		if (hasFlag(flag) && hasValue(flag)) {
			return Path.of(map.get(flag));
		}
		return null;
	}
	
	/**
	 * Returns the value to which the specified flag is mapped as a
	 * {@link Path}, or the default value if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping
	 * @return the value to which the specified flag is mapped, or the default
	 *         value if there is no mapping
	 */
	public Path getPath(String flag, String defaultValue) {
		if (map.get(flag) != null) {
			return Path.of(map.get(flag));
		}
		return Path.of(defaultValue);
	}

	/**
	 * Returns the value the specified flag is mapped as a {@link Path}, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or if no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid
	 *        mapping
	 * @return the value the specified flag is mapped as a {@link Path}, or the
	 *         default value if there is no valid mapping
	 */
	public Path getPath(String flag, Path defaultValue) {
		if (hasFlag(flag) && hasValue(flag)) {
			return Path.of(map.get(flag));
		}
		return defaultValue;
	}
	
	/**
	 * Returns the value to which the specified flag is mapped as a {@link URL},
	 * or {@code null} if unable to retrieve this mapping (including being unable
	 * to convert the value to a {@link URL} or no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping
	 * @throws MalformedURLException if an error occurs
	 */
	public URL getURL(String flag) throws MalformedURLException {
		if (hasFlag(flag) && hasValue(flag)) {
			return new URL(map.get(flag));
		}
		return null;
	}

	/**
	 * Returns the value the specified flag is mapped as an int value, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to an int or if no value exists).
	 *
	 * @param flag the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid
	 *        mapping
	 * @return the value the specified flag is mapped as a int, or the default
	 *         value if there is no valid mapping
	 */
	public int getInteger(String flag, int defaultValue) {
		if (map.get(flag) == null) {
			return defaultValue;
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(map.get(flag));
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
		return intValue;
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}