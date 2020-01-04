package teamalpha3240;

import java.util.Iterator;

/**
 * The Class CharIterator.
 */
public class CharIterator implements Iterator<Character> {

	/** The arr. */
	private char[] arr;

	/** The index. */
	private int index;

	/**
	 * Instantiates a new char iterator.
	 * 
	 * @param charArray
	 *            the char array
	 */
	public CharIterator(char[] charArray) {
		this.arr = charArray;
		this.index = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return arr == null ? false : index < arr.length;
	}

	/**
	 * Peek next.
	 * 
	 * @return the character
	 */
	public Character peekNext() {
		if (hasNext())
			return arr[index];
		return null;
	}

	/**
	 * Peek next.
	 * 
	 * @return the character
	 */
	public Character peekFuture(int offset) {
		if (arr != null && index + offset < arr.length)
			return arr[index + offset - 1];
		return null;
	}

	/**
	 * Peek potential range.
	 * 
	 * @return the string
	 */
	public String peekPotentialRange() {
		if (arr == null || index > arr.length - 2)
			return null;
		StringBuilder sb = new StringBuilder();
		int count = 0;
		int probe = index;
		while (count < 2 && probe < arr.length) {
			sb.append(arr[probe]);
			if (arr[probe++] != '\\')
				count++;
		}
		return count == 2 ? sb.toString() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Character next() {
		if (arr != null && hasNext())
			return arr[index++];
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// Method not implemented
	}
}
