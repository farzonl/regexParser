package teamalpha3240.datastructures;

import java.util.*;

import teamalpha3240.errorhandling.SyntaxException;

// TODO: Auto-generated Javadoc
/**
 * The Class ParseTree.
 * 
 * Used to build parse trees from a recursive decent parser. Built in toString()
 * method but use only for small parse tree.
 * 
 * @param <T>
 *            the generic type
 * @author David Esposito
 */
public class ParseTree<T> {

	/** The root. */
	private ParseNode<T> root;
	
	/** The name. */
	private String name;

	/** The level width. */
	private int levelWidth = 12;

	/** The classes defined by this. */
	private Collection<String> classesDefinedByThis;

	/**
	 * Gets the root.
	 * 
	 * @return the root
	 */
	public ParseNode<T> getRoot() {
		return root;
	}

	/**
	 * Instantiates a new parses the tree.
	 *
	 * @param root the root
	 * @param name the name
	 */
	public ParseTree(ParseNode<T> root, String name) {
		this.root = root;
		classesDefinedByThis = new HashSet<String>();
	}

	/**
	 * Adds the dependency.
	 * 
	 * @param definedClassName
	 *            the defined class name
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	public void addDependency(String definedClassName) throws SyntaxException {
		if (definedClassName != null) {
			if (classesDefinedByThis.contains(definedClassName)
					|| definedClassName.equals(this.name))
				throw new SyntaxException(
						"Circular definition causes infinite recursion.");
			classesDefinedByThis.add(definedClassName);
		}
	}

	/**
	 * Adds the dependencies.
	 * 
	 * @param definedClassNames
	 *            the defined class names
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	public void addDependencies(Collection<String> definedClassNames)
			throws SyntaxException {
		if (!(definedClassNames == null || definedClassNames.size() == 0))
			for (String dcName : definedClassNames)
				addDependency(dcName);
	}

	/**
	 * Deep copy.
	 * 
	 * @return the parses the tree
	 */
	public ParseTree<T> deepCopy() {
		return new ParseTree<T>(deepCopyNode(root), this.name);
	}

	/**
	 * Deep copy node.
	 * 
	 * @param curr
	 *            the curr
	 * @return the parses the node
	 */
	private ParseNode<T> deepCopyNode(ParseNode<T> curr) {
		ParseNode<T> newNode = new ParseNode<T>(curr.getData(), curr.getType());
		for (ParseNode<T> child : curr.getChildren())
			newNode.addChild(deepCopyNode(child));
		return newNode;
	}

	/**
	 * Leaf traversal.
	 * 
	 * @return the list
	 */
	public List<T> leafTraversal() {
		List<T> list = new LinkedList<T>();
		if (root != null) {
			leafTraversal(root, list);
		}
		return list;
	}

	/**
	 * Leaf traversal.
	 * 
	 * @param curr
	 *            the curr
	 * @param list
	 *            the list
	 */
	private void leafTraversal(ParseNode<T> curr, List<T> list) {
		if (curr == null)
			return;
		if (curr.isLeaf())
			list.add(curr.getData());
		else
			for (ParseNode<T> pn : curr.getChildren())
				leafTraversal(pn, list);
	}

	/**
	 * Leaf traversal parse nodes.
	 * 
	 * @return the list of leaf nodes
	 */
	public List<ParseNode<T>> leafTraversalParseNodes() {
		List<ParseNode<T>> list = new LinkedList<ParseNode<T>>();
		if (root != null) {
			leafTraversalPN(root, list);
		}
		return list;
	}

	/**
	 * Leaf traversal pn.
	 * 
	 * @param curr
	 *            the curr
	 * @param list
	 *            the list
	 */
	private void leafTraversalPN(ParseNode<T> curr, List<ParseNode<T>> list) {
		if (curr == null)
			return;
		if (curr.isLeaf())
			list.add(curr);
		else
			for (ParseNode<T> pn : curr.getChildren())
				leafTraversalPN(pn, list);
	}
	
	/**
	 * Pre order parse nodes.
	 *
	 * @return the iterator
	 */
	public Iterator<ParseNode<T>> preOrderParseNodes() {
		List<ParseNode<T>> list = new LinkedList<ParseNode<T>>();
		if (root != null)
			preOrderParseNodes(root, list);
		return list.iterator();
	}
	
	/**
	 * Pre order parse nodes.
	 *
	 * @param curr the curr
	 * @param list the list
	 */
	private void preOrderParseNodes(ParseNode<T> curr, List<ParseNode<T>> list) {
		for(ParseNode<T> child : curr.getChildren()) {
			list.add(child);
			preOrderParseNodes(child, list);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		MyStack<PrintingParseNode<T>> stk = new MyStack<PrintingParseNode<T>>();
		stk.push(new PrintingParseNode<T>(root.getData(), root.getChildren()
				.iterator()));
		while (!stk.isEmpty()) {
			PrintingParseNode<T> curr = stk.peek();
			if (curr.isLeaf()) {
				stk.resetIterator();
				while (stk.hasNext()) {
					PrintingParseNode<T> ppNode = stk.next();
					if (!ppNode.beenPrinted()) {
						sb.append(ppNode);
						if (!ppNode.isLeaf()) {
							sb.append("-");
							ppNode.setPrintedStatuc(true);
							sb.append(ppNode.onLastChild() ? "-" : "|");
						}
					} else {
						for (int i = 0; i <= ppNode.getWidth(); i++)
							sb.append(" ");
						sb.append(ppNode.onLastChild() ? !stk.peekForIterator()
								.beenPrinted() ? "\\" : " " : "|");
					}
					if (stk.hasNext() && !stk.peekForIterator().beenPrinted()) {
						for (int i = 0; i < levelWidth - ppNode.getWidth(); i++)
							sb.append("-");
						// sb.append(":");
					} else if (stk.hasNext()
							&& stk.peekForIterator().beenPrinted())
						for (int i = 0; i < levelWidth - ppNode.getWidth(); i++)
							sb.append(" ");
				}
				sb.append("\n");
				stk.pop();
			} else {
				ParseNode<T> nextChild = curr.getNextChild();
				if (nextChild != null)
					stk.push(new PrintingParseNode<T>(nextChild.getData(),
							nextChild.getChildren().iterator()));
				else
					stk.pop();
			}
		}
		return sb.toString();
	}

	// /**
	// * The main method.
	// *
	// * @param args the arguments
	// */
	// public static void main(String[] args) {
	// ParseNode<String> a = new ParseNode<String>("A");
	// ParseTree<String> tree = new ParseTree<String>(a);
	// a.addChild("AA");
	// a.addChild("AB");
	// ParseNode<String> b = a.getChildren().get(1);
	// a = a.getChildren().get(0);
	// a.addChild("AAA");
	// a.addChild("AAB");
	// a.addChild("AAC");
	// a.addChild("AAD");
	// a.addChild("AAE");
	// b.addChild("ABA");
	// b.addChild("ABB");
	// b.addChild("ABC");
	// b.addChild("ABD");
	// System.out.println(tree);
	// System.out.println();
	// System.out.println();
	// System.out.println();
	// }
}
