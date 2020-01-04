package teamalpha3240.errorhandling;

/**
 * The Class SyntaxException.
 * 
 * Custom exception for astetic purposes.
 * 
 * @author David Esposito
 */
public class SyntaxException extends Exception {

	/**
	 * Instantiates a new syntax exception.
	 */
	public SyntaxException() {
		super();
	}
	
	/**
	 * Instantiates a new syntax exception.
	 *
	 * @param errorMessage the error message
	 */
	public SyntaxException(String errorMessage) {
		super(errorMessage);
	}
	
}
