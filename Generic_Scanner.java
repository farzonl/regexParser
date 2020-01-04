package teamalpha3240;

import teamalpha3240.datastructures.State_Table;
import teamalpha3240.datastructures.StateRow;
import teamalpha3240.errorhandling.*;

/**
 * @author flotfi
 *
 */
public class Generic_Scanner {
	
	private State_Table table;

	public Generic_Scanner(State_Table table) {
		if (table == null)
			throw new IllegalArgumentException();
		this.table = table;
	}
	
	public boolean evaluate(String toEvaluate) throws ParsingException, IllegalArgumentException {
		if (toEvaluate == null)
			throw new IllegalArgumentException();
		CharIterator charIt = new CharIterator(toEvaluate.toCharArray());
		int currState = 0;
		Integer nextState = null;
		boolean needNewState = true;
		Character currentChar = null;
		StateRow row = null;
		do {
			row = table.getMainTable().get(currState);
			if (row == null)
				throw new ParsingException("Illegal State Transition: State does not exist.");
			while (charIt.hasNext() && (needNewState || (currentChar != null && currentChar == '\n'))){
				needNewState = false;
				currentChar = charIt.next();
			}
			nextState = row.rowTable().get(currentChar);
			needNewState = true;
			if (nextState == null && currentChar != null && currentChar != '\n') {
				nextState = row.rowTable().get(State_Table.EMPTY_STRING);
				if (nextState == null)
					return false;
				if (nextState == currState)
					return row.isAccept();
				if (!charIt.hasNext())
					return table.getMainTable().get(nextState).isAccept() && toEvaluate.length() == 0;
				needNewState = false;
			}
			if (nextState == null)
				return false;
			currState = nextState;
		} while(charIt.hasNext());
		row = table.getMainTable().get(currState);
		return row == null ? false : row.isAccept();
	}

}
