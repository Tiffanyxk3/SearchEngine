import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An interface containing the common methods of single and multithreaded result builder
 * 
 * @author tiffanyz
 */
public interface ResultBuilderInterface {
	
	/**
	 * Begin building the search result
	 * 
	 * @param file to build the queries list
	 * @param exact indicates whether to do an exact search or partial search
	 * @throws IOException if an I/O error occurs
	 */
	public default void build(Path file, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				addLine(line, exact);
			}
		}
	}
	
	/**
	 * Build a line in the file
	 * 
	 * @param line to build the search result
	 * @param exact indicates whether to do an exact search or partial search
	 */
	public void addLine(String line, boolean exact);
	
	/**
	 * Write the result into Json format
	 * 
	 * @param file used to output the index in Json format
	 * @throws IOException if exception occurs
	 */
	public void toJsonResult(Path file) throws IOException;
}
