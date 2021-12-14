import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Creating a recursive file listing.
 * 
 * @author tiffanyz
 */
public class DirectoryTraverser {
	
	/**
	 * Traverses through the directory and its subdirectories, returning a list of all the files
	 * 
	 * @param root root directory to traverse on
	 * @return a list of all the paths
	 * @throws IOException if an I/O error occurs
	 */
	public static ArrayList<Path> traverse(Path root) throws IOException {
		ArrayList<Path> paths = new ArrayList<>();
		traverseDirectory(root, paths);
		return paths;
	}
	
	/**
	 * Traverses through the directory and its subdirectories, adding all files to the list
	 * 
	 * @param directory directory to traverse
	 * @param paths a list of all the paths
	 * @throws IOException if an I/O error occurs
	 */
	private static void traverseDirectory(Path directory, List<Path> paths) throws IOException {
		if (Files.isDirectory(directory)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
				for (Path path : listing) {
					if (Files.isDirectory(path)) {
						traverseDirectory(path, paths);
					}
					else {
						String lower = path.toString().toLowerCase();
						if (lower.endsWith(".txt") || lower.endsWith(".text")) {
							paths.add(path);
						}
					}
				}
			}
		}
		else {
			paths.add(directory);
		}
	}
}