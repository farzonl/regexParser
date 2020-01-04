package teamalpha3240;
import java.io.*;
import java.util.*;
import java.util.Scanner;

/**
 * The Class FileHandler.
 *
 * @author flotfi
 */
public class FileHandler {
	
	/** The scanner. */
	private Scanner scanner;

	/**
	 * Instantiates a new file handler.
	 *
	 * @param filename the filename
	 */
	public FileHandler(String filename) {
		this(filename == null ? null : new File(filename));
	}

	/**
	 * Instantiates a new file handler.
	 *
	 * @param f the f
	 */
	public FileHandler(File f) {
		if (f != null) {
			try {
				this.scanner = new Scanner(f);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else
			throw new IllegalArgumentException("File or filename cannot be null.");
	}

	/**
	 * Read. Reads the file to a single string and returns the entire contents.
	 * Each line in the file is seperated by a new-line character ("\n") in the 
	 * returned string.
	 *
	 * @return the string
	 */
	public String[] read() {
		List<String> retVal = new ArrayList<String>();
		while (scanner.hasNextLine())
			retVal.add(scanner.nextLine());
		String[] ret_Str_Arr = new String[retVal.size()];
		for(int i = 0; i < ret_Str_Arr.length;i++)
			ret_Str_Arr[i] = retVal.get(i);
		return ret_Str_Arr;
	}

	/**
	 * Read. Reads the file to a single string and returns the entire contents.
	 * Each line in the file is seperated by a new-line character ("\n") in the 
	 * returned string.
	 *
	 * @return the string
	 */
	public String readString() {
		StringBuilder sb = new StringBuilder();
		while (scanner.hasNext())
			sb.append(scanner.nextLine()).append("\n");
		return sb.toString();
	}
}
