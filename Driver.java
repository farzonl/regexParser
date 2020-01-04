package teamalpha3240;

import teamalpha3240.datastructures.*;
import teamalpha3240.errorhandling.ParsingException;
import teamalpha3240.errorhandling.SyntaxException;

public class Driver {
	
	private ParseTree<String> tree;
	private State_Table table;
	private String[] regDefinition;
	private String startStateName;
	
	public ParseTree<String> getTree() {
		return tree;
	}

	public State_Table getTable() {
		return table;
	}

	public String[] getRegDefinition() {
		return regDefinition;
	}

	public String getStartStateName() {
		return startStateName;
	}

	public Driver(String regDefinitionFilename, String startStartName) {
		FileHandler fhRegDef = new FileHandler(regDefinitionFilename);
		regDefinition = fhRegDef.read();
		this.startStateName = startStartName;
		try {
			tree = Parser3240.build_big_parsetree(Parser3240.build_hashmap(regDefinition), startStartName);
			table = new State_Table();
			Parser3240.test();
			table.fill(tree);
		} catch (SyntaxException e) {
			e.printStackTrace();
		}
	}

	public Driver(String[] regDefinitionArray, String startStartName) {
		regDefinition = regDefinitionArray;
		this.startStateName = startStartName;
		try {
			tree = Parser3240.build_big_parsetree(Parser3240.build_hashmap(regDefinition), startStartName);
			System.out.println(tree);
			table = new State_Table();
			table.fill(tree);
		} catch (SyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public boolean evaluate(String toEvaluate) throws IllegalArgumentException, ParsingException {
		return new Generic_Scanner(table).evaluate(toEvaluate);
	}
	
	public static void main(String[] args) {
//		String[] arr = { "$main	([ab]c)*|def" };
//		String[] arr = { "$main	[^4-8] IN $b", "$a ([ab]c)*", "$b [0-9]" };
		String[] arr = new FileHandler("/home/david/Dropbox/gt_CS3240/GroupProject/sample_input.txt").read();
		Driver d = new Driver(arr, "$CONSTANT");
		System.out.println(d.getTree());
		System.out.println(d.getTable());
		try {
			System.out.println(d.evaluate("Acaca"));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}
}
