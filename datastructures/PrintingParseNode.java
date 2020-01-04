package teamalpha3240.datastructures;

import java.util.*;

/**
 * The Class PrintingParseNode.
 * 
 * Data holder for maintain the state machine required for 
 * printing a parse tree in the ParseTree.toString() method.
 *
 * @param <T> the generic type
 * @author David Esposito
 */
public class PrintingParseNode<T> {

	/** The child it. */
	private Iterator<ParseNode<T>> childIt;
	
	/** The data. */
	private T data;
	
	/** The been printed. */
	private boolean beenPrinted;
	
	/** The current child. */
	private ParseNode<T> currentChild;

	/**
	 * Checks if is leaf.
	 *
	 * @return true, if is leaf
	 */
	public boolean isLeaf() {
		return childIt == null;
	}

	/**
	 * Been printed.
	 *
	 * @return true, if successful
	 */
	public boolean beenPrinted() {
		return beenPrinted;
	}
	
	/**
	 * Sets the printed statuc.
	 *
	 * @param printed the new printed statuc
	 */
	public void setPrintedStatuc(boolean printed) {
		beenPrinted = printed;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return data.toString().length();
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * Instantiates a new printing parse node.
	 *
	 * @param data the data
	 * @param it the it
	 */
	public PrintingParseNode(T data, Iterator<ParseNode<T>> it) {
		this.data = data;
		childIt = it.hasNext() ? it : null;
		beenPrinted = false;
	}

	/**
	 * On last child.
	 *
	 * @return true, if successful
	 */
	public boolean onLastChild() {
		if (childIt != null)
			return !childIt.hasNext();
		return false;
	}
	
	/**
	 * Gets the current child.
	 *
	 * @return the current child
	 */
	public ParseNode<T> getCurrentChild() {
		return currentChild;
	}

	/**
	 * Gets the next child.
	 *
	 * @return the next child
	 */
	public ParseNode<T> getNextChild() {
		if (childIt != null && childIt.hasNext()) {
			currentChild = childIt.next();
			return currentChild;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return data == null ? null : data.toString();
	}
}
