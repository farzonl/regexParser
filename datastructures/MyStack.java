package teamalpha3240.datastructures;

import java.util.*;

/**
 * The Class MyStack. Can be used as an iterator to access the 
 * data elements in order. Otherwise, this is a normal stack implementation.
 *
 * @param <T> the generic type
 * @author David Esposito
 */
public class MyStack<T> implements Iterator<T> {

	/** The spine. */
	private List<T> spine;
	
	/** The current. */
	private int current;

	/**
	 * Instantiates a new my stack.
	 */
	public MyStack() {
		spine = new ArrayList<T>();
		resetIterator();
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return spine.size();
	}
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Push.
	 *
	 * @param data the data
	 */
	public void push(T data) {
		if (data != null)
			spine.add(data);
	}
	
	/**
	 * Peek.
	 *
	 * @return the t
	 */
	public T peek() {
		return peek(spine.size()-1);
	}

	/**
	 * Peek.
	 *
	 * @param index the index
	 * @return the t
	 */
	public T peek(int index) {
		if (index >= spine.size())
			return null;
		return spine.get(index);
	}

	/**
	 * Pop.
	 *
	 * @return the t
	 */
	public T pop() {
		return spine.remove(spine.size() - 1);
	}
	
	/**
	 * Reset iterator.
	 */
	public void resetIterator() {
		current = 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return current < spine.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		if (hasNext())
			return spine.get(current++);
		return null;
	}
	
	/**
	 * Peek for iterator.
	 *
	 * @return the t
	 */
	public T peekForIterator() {
		if (hasNext())
			return spine.get(current);
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// Not Implemented
	}
}
