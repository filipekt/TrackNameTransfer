package cz.filipekt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Provides the track name transferring functionality, without the GUI
 * interface, which is defined in {@link GUIWrapper}.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class Transfer {

	/**
	 * The directory from where the track names will be extracted
	 */
	private final String sourceDir;

	/**
	 * The directory where the track names will be replaced with new values
	 */
	private final String targetDir;

	/**
	 * @param sourceDir
	 *            The directory from where the track names will be extracted
	 * @param targetDir
	 *            The directory where the track names will be replaced with new
	 *            values
	 */
	public Transfer(String sourceDir, String targetDir) {
		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
	}

	/**
	 * 
	 * @throws IOException
	 *             Thrown when any file I/O error occurs
	 */
	public void work() throws IOException {
		Collection<Path> filesA = getMusicFiles(sourceDir);
		Collection<Path> filesB = getMusicFiles(targetDir);
		Map<String, String> mappingA = getMappings(filesA);
		Map<String, String> mappingB = getMappings(filesB);
		if (mappingA.keySet().equals(mappingB.keySet())) {
			map(mappingA, filesB);
		}
	}

	/**
	 * Finds any music files inside the specified directory
	 * 
	 * @param dir
	 *            The directory where music files will be searched for
	 * @return Collection of all the music files present in the specified
	 *         directory
	 * @throws IOException
	 *             Thrown when any file I/O error occurs
	 */
	private Collection<Path> getMusicFiles(String dir) throws IOException {
		Path path = Paths.get(dir);
		Collection<Path> res = new HashSet<>();
		String filter = getFilenameFilter();
		Files.newDirectoryStream(path, filter).forEach(p -> {
			res.add(p);
		});
		return res;
	}

	/**
	 * Recognized music filename extensions
	 */
	private final String[] musicFormats = new String[] { "flac", "mp3", "wav", "ape", "wma", "ogg", "aac", "aiff" };

	/**
	 * Constructs a "globbing pattern", as required by the
	 * {@link Files#newDirectoryStream(Path, java.nio.file.DirectoryStream.Filter)}
	 * . The pattern filters away all files with filename extension not
	 * contained in {@link Transfer#musicFormats}.
	 */
	private String getFilenameFilter() {
		StringBuilder sb = new StringBuilder();
		sb.append("*.{");
		for (int i = 0; i < musicFormats.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(musicFormats[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * For each music file contained in the provided file collection, this
	 * method assumes that the filename follows the pattern "TRACK_NO NAME.EXT",
	 * where TRACK_NO is a track number, NAME is a track name, EXT is a filename
	 * extension. For each of the files, the returned map contains a mapping of
	 * the TRACK_NO to a corresponding NAME.
	 * 
	 * @param files
	 *            Music files
	 */
	private Map<String, String> getMappings(Collection<Path> files) {
		Map<String, String> res = new HashMap<>();
		files.forEach(path -> {
			String filename = path.getFileName().toString();
			String[] splitted = filename.split(" ", 2);
			String prefix = splitted[0];
			int dot = splitted[1].lastIndexOf(".");
			String innerName = splitted[1].substring(0, dot);
			res.put(prefix, innerName);
		});
		return res;
	}

	/**
	 * Given a mapping of TRACK_NOs to NAMEs, obtained from the source
	 * directory, and a collection of music files from the target directory,
	 * this method renames all the music files in the target directory so that
	 * their names follow the "TRACK_NO NAME.EXT" pattern and moreover the NAME
	 * values are equal to those in the source directory
	 * 
	 * @throws IOException
	 *             Thrown when any file I/O error occurs
	 */
	private void map(Map<String, String> mappings, Collection<Path> files) throws IOException {
		for (Path path : files) {
			String filename = path.getFileName().toString();
			String[] splitted = filename.split(" ", 2);
			String prefix = splitted[0];
			int dot = splitted[1].lastIndexOf(".");
			String postfix = splitted[1].substring(dot + 1);
			String newname = prefix + " " + mappings.get(prefix) + "." + postfix;
			Files.move(path, Paths.get(targetDir, newname));
		}
	}

}
