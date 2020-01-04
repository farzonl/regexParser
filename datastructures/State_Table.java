package teamalpha3240.datastructures;

import java.util.*;
import java.util.Map.Entry;
import teamalpha3240.Parser3240;
import teamalpha3240.datastructures.StateRow;
import teamalpha3240.errorhandling.SyntaxException;

/**
 * @author Baris
 * 
 */
public class State_Table {

	public static char EMPTY_STRING = 0;
	
	private static int parCount = 0;

	private Map<Integer, StateRow> mainTable;

	private static Collection<Character> allChars;

	private int nextNewStateIndex;
	private Stack<ParenthesesGroup> parGroups;
	private ParenthesesGroup lastParGroup;
	private char lastChar;

	static {
		allChars = new HashSet<Character>();
		for (int i = 0; i < 256; i++) {
			allChars.add((char) i);
		}
	}

	private static Collection<Character> getAllChars() {
		Collection<Character> newColl = new HashSet<Character>();
		for (Character c : allChars)
			newColl.add(c);
		return newColl;
	}

	/**
	 * 
	 */
	public State_Table() {
		mainTable = new HashMap<Integer, StateRow>();
		nextNewStateIndex = 1;
		parGroups = new Stack<ParenthesesGroup>();
	}

	public Map<Integer, StateRow> getMainTable() {
		return mainTable;
	}

	public void setMainTable(Hashtable<Integer, StateRow> mainTable) {
		this.mainTable = mainTable;
	}

	public void fill(ParseTree<String> tree) throws SyntaxException {
		if (tree == null)
			throw new IllegalArgumentException();

		parGroups.push(new ParenthesesGroup(0));
		lastParGroup = null;
		
		
		
		rexp(tree.getRoot());
		System.out.println(nextNewStateIndex);
		while (this.mainTable.get(nextNewStateIndex) != null && this.mainTable.get(nextNewStateIndex).isEmpty() && this.mainTable.get(nextNewStateIndex-1).isLooping(nextNewStateIndex-1))
			this.mainTable.remove(nextNewStateIndex--);
		while (nextNewStateIndex > mainTable.size())
			this.mainTable.put(this.mainTable.size(), new StateRow());
		this.mainTable.get(this.mainTable.size() - 1).setAccept(true);
	}

	public void rexp(ParseNode<String> curr) throws SyntaxException {
		rexp1(curr.getChildren().get(0));
		rexpPrime(curr.getChildren().get(1));
	}

	public void rexpPrime(ParseNode<String> curr)
			throws SyntaxException {
		if (curr.getChildren().size() == 3) {
			if (parGroups.size() == 1) {
				if (lastParGroup != null)
					lastParGroup.setAccepting();
				else {
					mainTable.get(nextNewStateIndex-1).setAccept(true);
				}
			}
			parGroups.peek().split();
			rexp1(curr.getChildren().get(1));
			rexpPrime(curr.getChildren().get(2));
		} // else 
			// { empty string }
	}

	public void rexp1(ParseNode<String> curr) throws SyntaxException {
		if (curr.getChildren().size() == 1)
			rexp2(curr.getChildren().get(0));
		else {
			parGroups.push(new ParenthesesGroup(nextNewStateIndex-1));
			rexp(curr.getChildren().get(1));
			parGroups.peek().closed = true;
			rexp1Tail(curr.getChildren().get(3));
			lastParGroup = parGroups.pop();
			parGroups.peek().refreshEndingIndex(lastParGroup.getLastIndex());
		}
	}

	public void rexp1Tail(ParseNode<String> curr)
			throws SyntaxException {
		if (curr.getChildren().size() == 2) {
			if (curr.getChildren().get(0).getData().equals("+")){
				if (parGroups.size() > 1 && parGroups.peek().closed) {
					parGroups.peek().setRepeatable(false);
				} else { 
					insertChar(nextNewStateIndex-1, nextNewStateIndex-1, lastChar);
				}
			} else {
				if (parGroups.size() > 1 && parGroups.peek().closed) {
					parGroups.peek().setRepeatable(true);
				} else {
					mainTable.remove(nextNewStateIndex-1);
					nextNewStateIndex--;
					insertChar(nextNewStateIndex-1, nextNewStateIndex-1, lastChar);
				}
			}
			// TODO: figure out if this is accepting or not...
			
			rexp1(curr.getChildren().get(1));
		}
	}

	public void rexp2(ParseNode<String> curr) throws SyntaxException {
		if (curr.getChildren().get(0).getType() == Parser3240.ProductionStates.charClass) {
			charClass(curr.getChildren().get(0));
			rexp1(curr.getChildren().get(1));
		} else if (curr.getChildren().get(0).getType() == Parser3240.ProductionStates.literal) {
			parGroups.peek().append(curr.getChildren().get(0).getChar(), false);
			lastParGroup = null;
			rexp1Tail(curr.getChildren().get(1));
			rexp1(curr.getChildren().get(2));
		}
		
	}

	public void charClass(ParseNode<String> curr)
			throws SyntaxException {
		lastParGroup = null;
		if (curr.getChildren().size() == 1) {
			if (curr.getChildren().get(0).getType() == Parser3240.ProductionStates.rexp)
				rexp(curr.getChildren().get(0));
			else if (curr.getChildren().get(0).getData().equals(".")) {
				// CHECK: iterate over the table to enter a "."
				parGroups.peek().appendRange(' ', (char)255, false);
			} else {
				rexp(curr.getChildren().get(0).getChildren().get(0));
			}
		} else {
			charClass1(curr.getChildren().get(1));
		}
	}

	public void charClass1(ParseNode<String> curr)
			throws SyntaxException {
		if (curr.getChildren().get(0).getType() == Parser3240.ProductionStates.charSet)
			charSet(curr.getChildren().get(0));
		else
			excludeSet(curr.getChildren().get(0));
	}

	public void charSet(ParseNode<String> curr) throws SyntaxException {
		if (curr.getChildren().get(0).getType() == Parser3240.ProductionStates.range) {
			range(curr.getChildren().get(0), false);
			charSet1(curr.getChildren().get(1));
		} else {
			parGroups.peek().append(curr.getChildren().get(0).getChar(), false);
			charSet1(curr.getChildren().get(1));
		}
	}

	public void charSet1(ParseNode<String> curr)
			throws SyntaxException {
		if (curr.getChildren().size() == 2) {
			if (curr.getChildren().get(0).getType() == Parser3240.ProductionStates.range) {
				range(curr.getChildren().get(0), true);
				charSet1(curr.getChildren().get(1));
			} else {
				parGroups.peek().append(curr.getChildren().get(0).getChar(), true);
				charSet1(curr.getChildren().get(1));
			}
		}
	}

	public void range(ParseNode<String> curr, boolean inCharSet1) throws SyntaxException {
		char a = curr.getChildren().get(0).getChar();
		char b = curr.getChildren().get(2).getChar();
		if (a < 0 || b < 0 || a > 256 || b > 256)
			throw new SyntaxException("Invalid Character in range.");
		if (b < a)
			throw new SyntaxException("Invalid Range: [" + a + "-" + b + "]");
		parGroups.peek().appendRange(a, b, inCharSet1);
	}

	public void excludeSet(ParseNode<String> curr)
			throws SyntaxException {
		Collection<Character> includedChars;
		if (curr.getChildren().size() == 2)
			includedChars = getAllChars();
		else
			includedChars = excludeSetTail(curr.getChildren().get(3));
		excludeSet1(curr.getChildren().get(1), includedChars);
		parGroups.peek().appendCollection(includedChars);
	}

	public void excludeSet1(ParseNode<String> curr,
			Collection<Character> coll) throws SyntaxException {
		if (curr.getChildren().size() == 2) {
			if (curr.getChildren().get(0).getType() == Parser3240.ProductionStates.range) {
				ParseNode<String> range = curr.getChildren().get(0);
				char a = range.getChildren().get(0).getChar();
				char b = range.getChildren().get(2).getChar();
				if (a < 0 || b < 0 || a > 256 || b > 256)
					throw new SyntaxException("Invalid Character in range.");
				if (b < a)
					throw new SyntaxException("Invalid Range: [" + a + "-" + b
							+ "]");
				for (int i = a; i <= b; i++)
					coll.remove((char) i);
			} else {
				coll.remove(curr.getChildren().get(0).getChar());
			}
			excludeSet1(curr.getChildren().get(1), coll);
		}
	}

	public Collection<Character> excludeSetTail(ParseNode<String> curr)
			throws SyntaxException {
		Collection<Character> coll = new HashSet<Character>();
		if (curr.getChildren().size() == 2) {
			excludeSet2(curr.getChildren().get(1), coll);
		} else {
			extractCharSetFromDefinedClass(curr.getChildren().get(0), coll);
			// TODO: handle defined class... fuck this...
			/*
			 * $nums a*bbc[0-9] $test [^4-6] IN $nums $test [^4-6] IN [1-8]
			 * 
			 * how are we supposed to extract this? This isn't even possible...
			 */
		}
		return coll;
	}

	public void excludeSet2(ParseNode<String> curr,
			Collection<Character> coll) throws SyntaxException {
		if (curr.getChildren().size() == 2) {
			if (curr.getChildren().get(0).getType() == Parser3240.ProductionStates.range) {
				ParseNode<String> range = curr.getChildren().get(0);
				char a = range.getChildren().get(0).getChar();
				char b = range.getChildren().get(2).getChar();
				if (a < 0 || b < 0 || a > 256 || b > 256)
					throw new SyntaxException("Invalid Character in range.");
				if (b < a)
					throw new SyntaxException("Invalid Range: [" + a + "-" + b
							+ "]");
				for (int i = a; i <= b; i++)
					coll.add((char) i);
			} else {
				coll.add(curr.getChildren().get(0).getChar());
			}
			excludeSet1(curr.getChildren().get(1), coll);
		}
	}

	private void extractCharSetFromDefinedClass(ParseNode<String> curr,
			Collection<Character> coll) throws SyntaxException {
		if (curr.getChildren().size() < 1
				|| curr.getChildren().get(0).getChildren().size() < 1)
			return; // TODO: throw exception
		curr = curr.getChildren().get(0).getChildren().get(0);

		if (curr.getChildren().size() > 0
				&& curr.getChildren().get(0).getType() == Parser3240.ProductionStates.rexp2) {
			curr = curr.getChildren().get(0);
			if (curr.getChildren().size() > 0
					&& curr.getChildren().get(0).getType() == Parser3240.ProductionStates.charClass) {
				excludeSet2(curr.getChildren().get(0).getChildren().get(1)
						.getChildren().get(0), coll);
				return;
			}
		}
		throw new SyntaxException(
				"Expected referenced class to be defined as a character set.");
	}

	public void insertNewState(int currState, char c) {
		insertChar(currState, nextNewStateIndex++, c);
	}
	
	public void insertChar(int currState, int nextState, char c) {
		lastChar = c;
		if (this.mainTable.get(currState) == null)
			this.mainTable.put(currState, new StateRow());
		if (this.mainTable.get(currState+1) == null)
			this.mainTable.put(currState+1, new StateRow());
		this.mainTable.get(currState).rowTable().put(c, nextState);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (Entry<Integer, StateRow> outer : this.mainTable.entrySet()) {
			StateRow row = outer.getValue();
			sb.append(row.isAccept() ? "*" : " ").append(
					buf(outer.getKey().toString(), ' ', 4)).append("| { ");
			for (Entry<Character, Integer> inner : row.rowTable().entrySet()) {
				sb.append(
						inner.getKey() == 0 ? (char) 216 /* 248 */: inner
								.getKey()).append(":").append(inner.getValue())
						.append(" ");
			}
			sb.append("}\n");
		}

		return sb.toString();
	}

	private String buf(String s, char c, int i) {
		StringBuilder sb = new StringBuilder(s);
		while (sb.length() < i)
			sb.append(c);
		return sb.toString();
	}

	private class ParenthesesGroup {
		private int id = parCount++;
		private int startIndex;
		private List<ParenthesesPartition> groups;
		private boolean startNewPartition;
		private boolean closed;
		private boolean starred;
		
		public void refreshEndingIndex(int endingIndex) {
			groups.get(groups.size()-1).endingIndex = endingIndex;
		}
		
		public int getLastIndex() {
			return groups.get(0).endingIndex;
		}
		
		public ParenthesesGroup(int startIndex) {
			this.startIndex = startIndex;
			groups = new ArrayList<ParenthesesPartition>();
			startNewPartition = true;
			closed = false;
			starred = false;
		}
		
		public void append(char c, boolean inCharSet1) {
			if (startNewPartition) {
				Collection<Character> chars = new ArrayList<Character>();
				chars.add(c);
				groups.add(new ParenthesesPartition(nextNewStateIndex, chars));
				insertNewState(startIndex, c);
				groups.get(groups.size()-1).setEndingIndex(nextNewStateIndex-1);
				startNewPartition = false;
			} else {
				if (inCharSet1)
					insertChar(groups.get(groups.size()-1).endingIndex-1, nextNewStateIndex-1, c);
				else {
					insertNewState(groups.get(groups.size()-1).endingIndex, c);
					groups.get(groups.size()-1).setEndingIndex(nextNewStateIndex-1);
				}
			}
		}
		
		public void appendRange(char startChar, char endChar, boolean inCharSet1) {
			if (startNewPartition) {
				Collection<Character> chars = new ArrayList<Character>();
				for (int i=(int)startChar;i<=(int)endChar;i++)
					chars.add((char)i);
				groups.add(new ParenthesesPartition(nextNewStateIndex, chars));
				insertNewState(startIndex, startChar);
				for (int i=(int)startChar+1;i<=(int)endChar;i++)
					insertChar(startIndex, nextNewStateIndex-1, (char)i);
				startNewPartition = false;
			} else {
				if (inCharSet1)
					insertChar(groups.get(groups.size()-1).endingIndex-1, nextNewStateIndex-1, startChar);
				else
					insertNewState(groups.get(groups.size()-1).endingIndex, startChar);
				for (int i=(int)startChar+1;i<=(int)endChar;i++)
					insertChar(groups.get(groups.size()-1).endingIndex-1, nextNewStateIndex-1, (char)i);
				groups.get(groups.size()-1).setEndingIndex(nextNewStateIndex-1);
			}
		}
		
		public void appendCollection(Collection<Character> chars) {
			if (chars != null && !chars.isEmpty()) {
				int startingIndex;
				if (startNewPartition) {
					groups.add(new ParenthesesPartition(nextNewStateIndex, chars));
					startingIndex = startIndex;
					startNewPartition = false;
				} else {
					startingIndex = groups.get(groups.size()-1).endingIndex;
					groups.get(groups.size()-1).setEndingIndex(nextNewStateIndex);
				}
				Iterator<Character> charIt = chars.iterator();
				insertNewState(startingIndex, charIt.next());
				while(charIt.hasNext())
					insertChar(startingIndex, nextNewStateIndex-1, charIt.next());
			}
		}

		public void split() {
			if (startNewPartition) {
				// TODO handle empty string
				// "a(|b|c)d"
			}
			startNewPartition = true;
		}

		public void setRepeatable(boolean starRepeat) throws SyntaxException {
			if (groups.size() == 0) {
				throw new SyntaxException("Groups must be include atleast one character. ex. (a|) NOT (|).");
			}
			starred = starRepeat;
			for(ParenthesesPartition p1 : groups) {
				for (Character c : p1.incomming)
					insertChar(p1.endingIndex, p1.startingIndex, c);
				for (ParenthesesPartition p2 : groups) {
					if (p1 != p2) {
						for (Character c : mainTable.get(p1.startingIndex).rowTable().keySet())
							insertChar(p2.endingIndex, p1.startingIndex, c);
					}
				}
			}
		}
		
		public void setAccepting() throws SyntaxException {
			if (groups.size() == 0) {
				throw new SyntaxException("Groups must be include atleast one character. ex. (a|) NOT (|).");
			}
			if (starred)
				mainTable.get(startIndex).setAccept(true);
			for(ParenthesesPartition p : groups) {
				mainTable.get(p.endingIndex).setAccept(true);
			}
		}
		
		@Override
		public String toString() {
			return id + "_start:" + startIndex;
		}
	}
	
	private class ParenthesesPartition {
		private int startingIndex;
		private int endingIndex;
		private Collection<Character> incomming;

		public void setEndingIndex(int endingIndex) {
			this.endingIndex = endingIndex;
		}

		public ParenthesesPartition(int startingIndex, Collection<Character> incoming) {
			this.startingIndex = startingIndex;
			this.endingIndex = startingIndex;
			this.incomming = incoming;
			System.out.println(incoming);
			
		}
	}

}
