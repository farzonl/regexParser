package teamalpha3240.datastructures;
import java.util.*;
/**
 * StateRow class, represents one row in state table
 * 
 * @author Baris
 */
public class StateRow {
	private boolean accept;
	private Map<Character, Integer> rowTable;
	
	public boolean isLooping(int index) {
		boolean rtn = true;
		for(Integer i : rowTable.values())
			rtn = rtn && index >= i;
		return rtn && rowTable.size() > 0;
	}
	
	public boolean isEmpty() {
		return rowTable.size() == 0;
	}
	
	public Map<Character, Integer> rowTable() {
		return rowTable;
	}
	public void setrowTable(Hashtable<Character, Integer> hT) {
		rowTable = hT;
	}
	public StateRow(){
		rowTable = new HashMap<Character,Integer>();
		accept = false;
	}
	public StateRow(boolean accpt){
		rowTable = new HashMap<Character,Integer>();
		accept = accpt;
	}
	public void setAccept(boolean accpt){
		accept = accpt;
	}
	public boolean isAccept(){
		return accept;
	}
}
