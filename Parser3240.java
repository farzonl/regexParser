package teamalpha3240;

import teamalpha3240.datastructures.*;
import teamalpha3240.errorhandling.ParsingException;
import teamalpha3240.errorhandling.SyntaxException;
import java.util.*;
import java.util.Map.Entry;

// TODO: Auto-generated Javadoc
/**
 * The Class Parser.
 * 
 * @author David Esposito
 */
public class Parser3240 {

	/**
	 * The Enum ProductionStates.
	 */
	public static enum ProductionStates {

		/** States within the grammar. */
		reg_ex, rexp, rexpPrime, rexp1, rexp1Tail, rexp1Tail1, rexp2, charClass, charClass1, charSet, charSet1, range, excludeSet, excludeSetTail, definedClass,

		/** Terminal values within the grammar. */
		regexClass, literal, emptyString
	}

	/** The scan. */
	private Scanner scan;

	/** The Regex chars. */
	public static String[] RegexChars = { " ", "\\", "*", "+", "?", "|", "[",
			"]", "(", ")", ".", "'", "\"" };

	/** The Set escape chars. */
	public static String[] SetEscapeChars = { "\\", "^", "-", "[", "]" };

	/** The defined classes. */
	private static Map<String, ParseTree<String>> definedClasses;

	static boolean Debug = false;

	/** The used classes. */
	private List<String> usedClasses;

	/**
	 * Instantiates a new parser.
	 * 
	 * @param str
	 *            the str
	 */
	public Parser3240(Scanner scan) {
		// TODO Pass in the correct string
		this.scan = scan;
		// definedClasses = new HashMap<String, String>();
		usedClasses = new LinkedList<String>();
	}

	public static Map<String, ParseTree<String>> build_hashmap(String[] file_arr)
			throws SyntaxException {
		Map<String, ParseTree<String>> definedClasses = new HashMap<String, ParseTree<String>>();

		for (int i = 0; i < file_arr.length; i++) {
			Scanner s = new Scanner(file_arr[i]);
			String key = s.getNextToken();
			if (key.length() > 0 && key.charAt(0) == '$') {
				Parser3240 p = new Parser3240(s);
				ParseTree<String> plist = p.reg_ex(key);

				if (Debug)
					System.out.println("the key" + key);

				if (!key.isEmpty() && p.isDefinedClass(key.substring(0, 1)))
					definedClasses.put(key, plist);
			}
		}
		return definedClasses;
	}

	public static ParseTree<String> build_big_parsetree(
			Map<String, ParseTree<String>> definedClasses, String startingClass)
			throws SyntaxException {

		for (Entry<String, ParseTree<String>> entry : definedClasses.entrySet()) {

			ParseTree<String> first = entry.getValue();
			boolean madeChange = true;
			int count = 0;
			while (madeChange && count < 100) {
				List<ParseNode<String>> parse_list = first
						.leafTraversalParseNodes();
				madeChange = false;
				count++;
				for (ParseNode<String> nodes : parse_list) {
					if (nodes.getType() == ProductionStates.regexClass) {
						madeChange = true;
						ParseTree<String> tree = definedClasses.get(nodes.getData());
						if (tree == null)
							throw new SyntaxException("The class " + nodes.getData() + " is not defined.");
						ParseNode<String> parse_node2 = tree.deepCopy().getRoot();
					if (Debug)
						System.out.println(nodes.getData());
					
						nodes.setInternalData(parse_node2);
					}
				}
			}
			if (count == 100)
				throw new SyntaxException("Recursive class definition.");
		}
		return definedClasses.get(startingClass);
	}

	/**
	 * Reg_ex.
	 * 
	 * @return the parses the tree
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	public ParseTree<String> reg_ex(String definedClassName)
			throws SyntaxException {
		return new ParseTree<String>(rexp(), definedClassName);
	}

	/**
	 * Rexp.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	public ParseNode<String> rexp() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<rexp>",
				ProductionStates.rexp);
		curr.addChild(rexp1());
		curr.addChild(rexpPrime());
		return curr;
	}

	/**
	 * Rexp prime.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> rexpPrime() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<rexpPrime>",
				ProductionStates.rexpPrime);
		if (scan.hasNextToken() && scan.peekNextToken().equals("|")) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(rexp1());
			curr.addChild(rexpPrime());
		} else
			curr.addChild("", ProductionStates.emptyString);
		return curr;
	}

	/**
	 * Rexp1.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> rexp1() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<rexp1>",
				ProductionStates.rexp1);
		if (scan.hasNextToken() && scan.peekNextToken().equals("(")) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(rexp());
			if (scan.hasNextToken() && scan.peekNextToken().equals(")")) {
				curr.addChild(scan.getNextToken(), ProductionStates.literal);
				curr.addChild(rexp1Tail());
			} else
				throw new SyntaxException("Missing Closing Parentheses");
		} else
			curr.addChild(rexp2());
		return curr;
	}

	/**
	 * Rexp1 tail.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> rexp1Tail() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<rexp1Tail>",
				ProductionStates.rexp1Tail);
		// TODO: changing the "rexp" to "rexp1"
		if (scan.hasNextToken() && scan.peekNextToken().equals("*")) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(rexp1());
		} else if (scan.hasNextToken() && scan.peekNextToken().equals("+")) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(rexp1());
		} else
			curr.addChild("", ProductionStates.emptyString);
		return curr;
	}

	/**
	 * Rexp2.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> rexp2() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<rexp2>",
				ProductionStates.rexp2);
		if (scan.hasNextToken() && !isRegexChar(scan.peekNextToken())
				&& scan.peekNextToken().length() > 0
				&& !isCharClass(scan.peekNextToken())) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(rexp1Tail());
			curr.addChild(rexp1());
		} else if (scan.hasNextToken() && isCharClass(scan.peekNextToken())) {
			curr.addChild(charClass());
			curr.addChild(rexp1());
		} else
			curr.addChild("", ProductionStates.emptyString);
		return curr;
	}

	/**
	 * Char class.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> charClass() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<charClass>",
				ProductionStates.charClass);
		if (scan.hasNextToken() && scan.peekNextToken().equals(".")) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
		} else if (scan.hasNextToken() && scan.peekNextToken().equals("[")) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(charClass1());
		} else if (scan.hasNextToken() && isDefinedClass(scan.peekNextToken())) {
			curr.addChild(definedClass());
		} else
			throw new SyntaxException("Expecting '.' or '[' or '$'");
		return curr;
	}

	/**
	 * Char class1.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> charClass1() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<charClass1>",
				ProductionStates.charClass1);
		if (scan.hasNextToken() && scan.peekNextToken().equals("^")) {
			curr.addChild(excludeSet());
		} else
			curr.addChild(charSet());
		return curr;
	}

	/**
	 * Char set.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> charSet() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<charSet>",
				ProductionStates.charSet);
		if (scan.hasNextToken() && isRange(scan.peekPotentialRange())) {
			curr.addChild(range());
			curr.addChild(charSet1());
		} else if (scan.hasNextToken()
				&& !isSetEscapeChar(scan.peekNextToken())) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(charSet1());
		} else
			throw new SyntaxException(
					"Atleast one character must be used to define a set. ex. [a]");
		return curr;
	}

	/**
	 * Char set1.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> charSet1() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<charSet1>",
				ProductionStates.charSet1);
		if (scan.hasNextToken() && isRange(scan.peekPotentialRange())) {
			curr.addChild(range());
			curr.addChild(charSet1());
		} else if (scan.hasNextToken()
				&& !isSetEscapeChar(scan.peekNextToken())) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(charSet1());
		} else if (scan.hasNextToken() && scan.peekNextToken().equals("]"))
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
		else
			throw new SyntaxException("Malformed character set.");
		return curr;
	}

	/**
	 * Range.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> range() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<range>",
				ProductionStates.range);
		if (scan.hasNextToken() && isRange(scan.peekPotentialRange())) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
		} else
			throw new SyntaxException("Range was expected.");
		return curr;
	}

	/**
	 * Exclude set.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> excludeSet() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<excludeSet>",
				ProductionStates.excludeSet);
		if (scan.hasNextToken() && scan.peekNextToken().equals("^")) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(charSet());
			if (scan.hasNextToken() && scan.peekNextToken().equals("IN")) {
				curr.addChild(scan.getNextToken(), ProductionStates.literal);
				curr.addChild(excludeSetTail());
			} else {
				throw new SyntaxException(
						"Exclusion Sets must be defined as: \n\t[^...] IN $CLASS\n\t--or--\n\t[^...] IN [...]");
			}
		} else
			throw new SyntaxException(
					"Exclusion set expected. Exclusion sets must start as: [^...] ...");
		return curr;
	}

	/**
	 * Exclude set tail.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> excludeSetTail() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<excludeSetTail>",
				ProductionStates.excludeSetTail);
		if (scan.hasNextToken() && scan.peekNextToken().equals("[")) {
			curr.addChild(scan.getNextToken(), ProductionStates.literal);
			curr.addChild(charSet());
		} else if (scan.hasNextToken() && isDefinedClass(scan.peekNextToken())) {
			curr.addChild(definedClass());
		} else
			throw new SyntaxException(""); // TODO: defined exception
		return curr;
	}

	/**
	 * Defined class.
	 * 
	 * @return the parses the node
	 * @throws SyntaxException
	 *             the syntax exception
	 */
	private ParseNode<String> definedClass() throws SyntaxException {
		ParseNode<String> curr = new ParseNode<String>("<definedClass>",
				ProductionStates.definedClass);
		if (scan.hasNextToken() && isDefinedClass(scan.peekNextToken())) {
			String theDefinedClass = scan.getNextToken();
			curr.addChild(theDefinedClass, ProductionStates.regexClass);
			usedClasses.add(theDefinedClass);
		} else
			throw new SyntaxException("Expecting a defined class");
		return curr;
	}

	/**
	 * Checks if is a regex char.
	 * 
	 * @param s
	 *            the s
	 * @return true, if is regex char
	 */
	private boolean isRegexChar(String s) {
		if (s == null || s.length() != 1)
			return false;
		for (String rc : RegexChars)
			if (rc.equals(s))
				return true;
		return false;
	}

	/**
	 * Checks if is a char class.
	 * 
	 * @param s
	 *            the s
	 * @return true, if is char class
	 */
	private boolean isCharClass(String s) {
		if (s == null || s.length() == 0)
			return false;
		return s.equals(".") || s.equals("[") || s.charAt(0) == '$';
	}

	/**
	 * Checks if is a multiplier character.
	 * 
	 * @param s
	 *            the s
	 * @return true, if is multiplier
	 */
	private boolean isMultiplier(String s) {
		if (s == null || s.length() == 0)
			return false;
		return s.equals("+") || s.equals("*");
	}

	/**
	 * Checks if is a defined class.
	 * 
	 * @param s
	 *            the s
	 * @return true, if is defined class
	 */
	public boolean isDefinedClass(String s) {
		if (s == null || s.length() == 0)
			return false;
		return s.charAt(0) == '$';
	}

	/**
	 * Checks if defined class exists.
	 * 
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	private boolean definedClassExists(String s) {
		if (!isDefinedClass(s))
			return false;
		return definedClasses.containsKey(s);
	}

	/**
	 * Checks if is a range.
	 * 
	 * @param s
	 *            the s
	 * @return true, if is range
	 */
	private boolean isRange(String s) {
		if (s == null || s.length() != 3)
			return false;
		if (s.indexOf('-') > 0 && s.indexOf('-') < s.length() - 1) {
			String[] arr = s.split("-");
			if (arr.length == 2) {
				if (arr[0].length() == 1 && isSetEscapeChar(arr[0]))
					return false;
				return !(arr[1].length() == 1 && isSetEscapeChar(arr[1]));
			}
		}
		return false;
	}

	/**
	 * Checks if the string is a set escape char.
	 * 
	 * @param s
	 *            the s
	 * @return true, if is sets the escape char
	 */
	private boolean isSetEscapeChar(String s) {
		if (s == null || s.length() != 1)
			return false;
		for (String sec : SetEscapeChars)
			if (sec.equals(s))
				return true;
		return false;
	}

	public static void test() {
		for (Entry<String, ParseTree<String>> ME : definedClasses.entrySet()) {
			System.out.println(ME.getKey() + " -\t" + ME.getValue().toString());
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		try {
			// ParseTree<String> tree = new Parser3240(new
			// Scanner("([ab]c)*|def[l-z]a")).reg_ex("$nums");
//			ParseTree<String> tree = new Parser3240(new Scanner(
//			"([ab]c)*|def[l-z]a")).reg_ex("$nums");
//			ParseTree<String> tree = new Parser3240(new Scanner(
//			"ab|bc")).reg_ex("$nums");
//			ParseTree<String> tree = new Parser3240(new Scanner(
//			"([ab]c)*")).reg_ex("$nums");
			ParseTree<String> tree = new Parser3240(new Scanner(
			"a*(\\ ([a-c])*)*")).reg_ex("$nums");
			
			System.out.println(tree);
			State_Table st = new State_Table();
			st.fill(tree);
			System.out.println(st);
			Generic_Scanner gs = new Generic_Scanner(st);
			String conf = "";
			System.out.println("*" + conf + "* " + gs.evaluate(conf));
			conf = "ac";
			System.out.println("*" + conf + "* " + gs.evaluate(conf));
			conf = "bcac";
			System.out.println("*" + conf + "* " + gs.evaluate(conf));
			conf = "defa";
			System.out.println("*" + conf + "* " + gs.evaluate(conf));
			conf = "defla";
			System.out.println("*" + conf + "* " + gs.evaluate(conf));
			conf = "defta";
			System.out.println("*" + conf + "* " + gs.evaluate(conf));
			conf = "j";
			System.out.println("*" + conf + "* " + gs.evaluate(conf));
			conf = "acdefla";
			System.out.println("*" + conf + "* " + gs.evaluate(conf));
			// System.out.println(new Parser3240(new
			// Scanner("a*")).reg_ex("$nums"));
			// System.out.println(new Parser3240(new
			// Scanner("abcdef")).reg_ex("$nums"));
			// System.out.println(new Parser3240(new
			// Scanner("[0-9]|[A-Za-z]")).reg_ex("$nums"));
			// System.out.println(new Parser3240(new
			// Scanner("$nums")).reg_ex("$go"));

			// String[] testArr =
			// {"$num [0-9]","$char [a-z]","$go (a|b)|$num","$stop $go|$char"};
			// build_hashmap(testArr);
			// test();
			// build_big_parsetree();
			// test();
		} catch (SyntaxException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}
}
