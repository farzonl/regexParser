package teamalpha3240;

import java.io.File;

import teamalpha3240.errorhandling.ParsingException;
import teamalpha3240.errorhandling.SyntaxException;

public class Test_Main 
{
	public static void main(String[] args) {
		System.out.println("Start Test");
        if (args.length < 3) {
                System.out.println("\nPlease specify a filename:");
                System.out.println("java Test_Main <Grammar filename> <sample filename> <start state>\n");
                System.exit(0);
        }
 
        String file = args[0];
        String file2 = args[1];
        String startState = args[2];
        File f = new File(file);
        File f2 = new File(file2);
        if (!(f.exists())) {
                System.out.println("No such file or directory: " + file);
                System.exit(0);
        }
        if (!(f2.exists())) 
        {
            System.out.println("No such file or directory: " + file2);
            System.exit(0);
        }
        FileHandler  fh = new FileHandler(f);
        String[] arr = fh.read();
        
        FileHandler  fh2 = new FileHandler(f2);
        String toEval = fh2.readString();
        
		Driver d = new Driver(arr,startState);
		
		System.out.println(d.getTree());
		System.out.println(d.getTable());
		try 
		{
				System.out.println(d.evaluate(toEval));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
}
