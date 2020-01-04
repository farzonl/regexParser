package teamalpha3240;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
/**
 * 
 */

import teamalpha3240.Parser3240.ProductionStates;
import teamalpha3240.datastructures.ParseNode;
import teamalpha3240.datastructures.ParseTree;
import teamalpha3240.errorhandling.SyntaxException;

/**
 * @author flotfi
 *
 */
public class DefineClassBuilder {

	/**
	 * 
	 */
	static boolean Debug = true;
	String[] file_arr;
	ParseTree<String> parse_Tree;
	HashMap<String, ParseTree<String>> Parse_Table;
	
	public DefineClassBuilder(String[] arr)
	{
		// TODO Auto-generated constructor stub
		this.file_arr = arr;
	}
	
	private void build_hashmap() throws SyntaxException
	{
		Parse_Table = new HashMap<String, ParseTree<String>>();
		
		for(int i = 0; i < file_arr.length; i++ )
		{
			Scanner    s  = new Scanner(file_arr[i]);
			String     key = s.getNextToken();
			Parser3240 p = new Parser3240(s);
			ParseTree<String> plist = p.reg_ex(key);
			if(i == 0) parse_Tree = plist;
			if(p.isDefinedClass(""+key.charAt(0)))
				Parse_Table.put(key,plist);
			//System.out.println(plist.toString());
			
		}
	}
	
	private  void build_big_parsetree()
	{
		
		//Iterator<Entry<String, ParseTree<String>>> itr = Parse_Table.entrySet().iterator();
		//while(itr.hasNext())
		for(Entry<String, ParseTree<String>> entry : Parse_Table.entrySet())
		{
			//Entry<String, ParseTree<String>> entry = itr.next();
			ParseTree<String> first = entry.getValue();
			String key = entry.getKey();
			List<ParseNode<String>> parse_list = first.leafTraversalParseNodes();
			//Iterator<ParseNode<String>> nodes = parse_list.iterator();
			for(ParseNode<String> nodes : parse_list)
			{
				//ParseNode<String> parse_node = nodes.next();
				if(Debug) System.out.println("Key: "+key+"\t"+nodes.getData()+"\t"+nodes.getType());
				if (nodes.getType() == ProductionStates.regexClass)
				{
					if(Debug) System.out.println(nodes.getData());
					ParseNode<String> parse_node2 = Parse_Table.get(nodes.getData()).deepCopy().getRoot();
					//if(nodes.hasNext()) parse_node2.addChild(nodes.next());
					//parse_node.addChild(parse_node2);
					nodes.setInternalData(parse_node2);
				}
			}
		}
	}
	
	public void test()
	{
		for(Entry<String, ParseTree<String>> ME : Parse_Table.entrySet())
		{
			System.out.println(ME.getKey()+" -\t"+ME.getValue().toString());
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Start Test");
        /*if (args.length <= 0) {
                System.out.println("\nPlease specify a filename:");
                System.out.println("java FileHandler <filename>\n");
                System.exit(0);
        }
 
        String file = args[0]; 
        File f = new File(file);
        if (!(f.exists())) {
                System.out.println("No such file or directory: " + file);
                System.exit(0);
        }
        FileHandler  fh = new FileHandler(f);*/
        String[] test = {"$num [0-9]","$char [a-z]","$go $num"};
        DefineClassBuilder  s  = new DefineClassBuilder(test);
        try {
			s.build_hashmap();
		} catch (SyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        s.test();
        s.build_big_parsetree();
        //System.out.println(s.parse_Tree.toString());
        s.test();
	}

}
