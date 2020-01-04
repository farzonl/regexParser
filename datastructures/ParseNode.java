package teamalpha3240.datastructures;

import java.util.*;

import teamalpha3240.Parser3240;

// TODO: Auto-generated Javadoc
/**
 * The Class ParseNode. Data holder for ParseTree data structure.
 *
 * @param <T> the generic type
 * 
 * @author David Esposito
 */
public class ParseNode<T> {
	
	/** The data. */
	private T data;
	
	/** The node type. */
	private Parser3240.ProductionStates nodeType;
	
	/** The children. */
	private List<ParseNode<T>> children;

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public T getData() {
		return data;
	}
	
	public Character getChar() {
		String s = data.toString();
		if (s.length() == 1)
			return s.charAt(0);
		if (s.length() == 2 && s.charAt(0) == '\\')
			return s.charAt(1);
		return s.charAt(0);
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(T data) {
		this.data = data;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Parser3240.ProductionStates getType() {
		return nodeType;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<ParseNode<T>> getChildren() {
		return children;
	}

	/**
	 * Instantiates a new parses the node.
	 *
	 * @param data the data
	 * @param type the type
	 */
	public ParseNode(T data, Parser3240.ProductionStates type) {
		if (data == null) {
			try {
				throw new IllegalArgumentException();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		this.data = data;
		this.nodeType = type;
		children = new LinkedList<ParseNode<T>>();
	}
	
	/**
	 * Sets the internal data of this object to the internal data 
	 * of the node providedls
	 * .
	 *
	 * @param copy the copy
	 * @return true, if successful
	 */
	public boolean setInternalData(ParseNode<T> copy) {
		if (copy != null) {
			this.children = copy.children;
			this.data = copy.data;
			this.nodeType = copy.nodeType;
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if is leaf.
	 *
	 * @return true, if is leaf
	 */
	public boolean isLeaf() {
		return children.size() == 0;
	}
	
	/**
	 * Adds the child.
	 *
	 * @param data the data
	 * @param nodeType the node type
	 * @return true, if successful
	 */
	public boolean addChild(T data, Parser3240.ProductionStates nodeType) {
		if (data != null)
			return addChild(new ParseNode<T>(data, nodeType));
		return false;
	}
	
	/**
	 * Adds the child.
	 *
	 * @param child the child
	 * @return true, if successful
	 */
	public boolean addChild(ParseNode<T> child) {
		if (child != null){
			children.add(child);
			return true;
		}
		return false;
	}
	
	/**
	 * Clear children.
	 */
	public void clearChildren() {
		children.clear();
	}
	
	@Override
	public String toString() {
		return this.data.toString();
	}
}
