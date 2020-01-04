package teamalpha3240;
import java.util.Iterator;

import teamalpha3240.errorhandling.SyntaxException;

/**
 * Scans regular expression definitions to return tokens.
 * 
 * @author David Esposito
 * @version 1.0
 */
public class Scanner {

	/** The char it. */
	private CharIterator charIt;

	/** The curr. */
	private StringBuilder curr;
	
	/** The next. */
	private StringBuilder next;
	
	/**
	 * Instantiates a new scanner.
	 *
	 * @param str the str
	 */
	public Scanner(String str) {
		if (str == null)
			throw new IllegalArgumentException(
					"Iterator cannot be null or empty");
		charIt = new CharIterator(str.toCharArray());
		curr = new StringBuilder();
		next = new StringBuilder();
		try {
			setIterators();
		} catch (SyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the iterators.
	 *
	 * @return the string
	 * @throws SyntaxException the syntax exception
	 */
	private String setIterators() throws SyntaxException {
		curr = next;
		next = new StringBuilder();
		Character c = ' ';
		boolean isBuildingIdentifier = false;
		boolean isEscaped = false;
		while (c != null) {
			c = getNextChar();
			if (c != null && (!isWhiteSpace(c)
					|| (isWhiteSpace(c) && isEscaped))) {
				if (isEscapeChar(c) && !isEscaped) {
					isEscaped = true;
					next.append(c);
				} else if (isIdentifierStarter(c) && !isBuildingIdentifier
						&& !isEscaped) {
					isBuildingIdentifier = true;
					next.append(c);
				} else if (isIdentifierStarter(c) && !isEscaped)
					throw new SyntaxException(
							"The '$' character must be escaped inside an identifier.");
				else if (isBuildingIdentifier) {
					next.append(c);
					if (!isEscaped && (peekNextChar() == null || isRegexChar(peekNextChar())))
						break;
					isEscaped = false;
				} else {
					next.append(c);
					break;
				}
			} else if (next.length() > 0)
				break;
		}
		if (next.toString().equals("I") && charIt.peekNext() == 'N' && (charIt.peekFuture(2) == ' ' || charIt.peekFuture(2) == '\t')){
			next.append(charIt.next());
		}
		return curr.toString();
	}

	/**
	 * Gets the next char.
	 * 
	 * @return the next char
	 */
	private Character getNextChar() {
		return charIt.next();
	}
	
	/**
	 * Peek next char.
	 *
	 * @return the character
	 */
	private Character peekNextChar() {
		return charIt.peekNext();
	}

	/**
	 * Checks for next token.
	 * 
	 * FUNCTIONAL!!!!
	 * 
	 * @return true, if successful
	 */
	public boolean hasNextToken() {
		return curr.length() > 0 || next.length() > 0;

	}

	/**
	 * Looks at the next token without advancing the iterator.
	 *
	 * @return the string
	 */
	public String peekNextToken() {
		return next.toString();
	}

	/**
	 * Gets the next token.
	 * 
	 * NOT FUNCTIONAL!!!
	 *
	 * @return the next token
	 * @throws SyntaxException the syntax exception
	 */
	public String getNextToken() throws SyntaxException {
		return setIterators();
	}
	
	/**
	 * Peek potential range.
	 *
	 * @return the string
	 */
	public String peekPotentialRange() {
		return next.toString() + charIt.peekPotentialRange();
	}

	/**
	 * Checks if is escape char.
	 * 
	 * @param c
	 *            the c
	 * @return true, if is escape char
	 */
	private boolean isEscapeChar(char c) {
		return c == '\\';
	}

	/**
	 * Checks if is white space.
	 * 
	 * @param c
	 *            the c
	 * @return true, if is white space
	 */
	private boolean isWhiteSpace(char c) {
		return c == ' ' || c == '\t' || c == '\n';
	}

	/**
	 * Checks if char is '$'.
	 * 
	 * @param c
	 *            the c
	 * @return true if is identifier starter
	 */
	private boolean isIdentifierStarter(char c) {
		return c == '$';
	}

	/**
	 * Checks if is regex char.
	 * 
	 * @param c
	 *            the c
	 * @return true, if is regex char
	 */
	private boolean isRegexChar(char c) {
		return c == '*' || c == '+' || c == '|' || c == '[' || c == ']'
				|| c == '(' || c == ')' || c == '\'' || c == '"' || c == '.' || c == ' ';
	}
}
