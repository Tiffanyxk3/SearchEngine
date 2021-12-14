import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Build the given inverted index
 * 
 * @author tiffanyz
 */
public class InvertedIndexBuilder {
	/** inverted index to store the result */
	final private InvertedIndex index;
	
	/**
	 * Initializes the instance data
	 * 
	 * @param index the inverted index to build on
	 */
	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}
	
	/**
	 * Build from the startPath
	 * 
	 * @param startPath used to build the index
	 * @throws IOException if an IO error occurs
	 */
	public void build(Path startPath) throws IOException {
		for (Path path : DirectoryTraverser.traverse(startPath)) {
			addFile(path);
		}
	}
	
	/**
	 * Read through the file, parse and stem each word in the file, and add them to the index
	 * 
	 * @param path file to read
	 * @throws IOException if an IO error occurs
	 */
	public void addFile(Path path) throws IOException {
		addFile(path, this.index);
	}
	
	/**
	 * Read through the file, parse and stem each word in the file, and add them to the index
	 * 
	 * @param path file to read
	 * @param index the index to add things on
	 * @throws IOException if an IO error occurs
	 */
	public static void addFile(Path path, InvertedIndex index) throws IOException {
		String file = path.toString();
		Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line = null;
			int n = 0;
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					index.add(file, stemmer.stem(word).toString(), n);
					n++;
				}
			}
		}
	}
}