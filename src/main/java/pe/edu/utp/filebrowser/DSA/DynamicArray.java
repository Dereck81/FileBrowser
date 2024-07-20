package pe.edu.utp.filebrowser.DSA;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/***
 * @author cerofour "Diego Alexis Llacsahuanga Buques"
 * @param <T>
 */

public class DynamicArray<T> implements Iterable<T>, Serializable {

	@Serial
	private static final long serialVersionUID = -8102157520166196885L;
	private T[] arr;
	private int size;
	private int capacity;

	@SuppressWarnings("unchecked")
	public DynamicArray() {
		capacity = 0;
		size = 0;
		arr = (T[]) new Object[capacity];
	}

	@SuppressWarnings("unchecked")
	public DynamicArray(int cap) {
		capacity = cap;
		size = 0;
		arr = (T[]) new Object[capacity];
	}

	@SuppressWarnings("unchecked")
	public DynamicArray(T...elements) {
		arr = elements;
		capacity = elements.length;
		size = elements.length;
	}

	public Iterator<T> iterator() {
		return new DynamicArrayIterator();
	}

	private class DynamicArrayIterator implements Iterator<T> {
		private int currentIndex = 0;

		@Override
		public boolean hasNext() {
			return currentIndex < size;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return arr[currentIndex++];
		}
	}

	@SuppressWarnings("unchecked")
	private void grow() {
		assert (size >= capacity);

		int newCapacity = capacity + 4;
		T[] newArr = (T[]) new Object[newCapacity];
		System.arraycopy(arr, 0, newArr, 0, capacity);
		size = capacity;
		capacity = newCapacity;
		arr = newArr;
	}

	public T at(int i) {
		// no exception handling because we are too intelligent for that
		return arr[i];
	}

	public void pushBack(T elem) {
		if (size == capacity)
			grow();
		arr[size++] = elem;
	}

	public T pop() {
		if (size == 0)
			return null;
		return arr[--size];
	}

	public int size() {
		return size;
	}

	public void swap(int i, int j) {
		T tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

	public void setAt(int i, T elem) {
		arr[i] = elem;
	}

	public T front() {
		return arr[0];
	}

	@SuppressWarnings("unused")
	public T back() {
		return arr[size - 1];
	}

	@SuppressWarnings("unchecked")
	public void clear() {
		capacity = 0;
		size = 0;
		arr = (T[]) new Object[capacity];
	}

	/**
	 * Creates a new array with exactly the same elements as the original array (this.arr)
	 * The elements of the newly created array are references to the exact same object that the elements of the
	 * original array refer to. But the array is a different block of memory, thus a different reference.
	 * @return A newly created array, with the same elements of the original array
	 */
	public DynamicArray<T> clone() {
		DynamicArray<T> copy = new DynamicArray<>(capacity);
		if (size >= 0) System.arraycopy(arr, 0, copy.arr, 0, size);

		copy.size = size;

		return copy;
	}

	/**
	 * Deletes the element at the specified index from the array.
	 * There may be elements that have not been deleted during the copy,
	 * but this is not taken into account since adding another element
	 * will overwrite it.
	 *
	 * @param index the index of the element to be deleted.
	 *
	 * @throws IndexOutOfBoundsException if the specified index is out of range (index < 0 || index >= size).
	 */
	public void delete(int index) {
		if(index < 0 || index >= size) return;
		if(index != size-1)
			System.arraycopy(arr, index+1, arr, index, size - index - 1);
		size--;
	}

	/**
	 * Finds the index of the specified element in the array.
	 *
	 * @param x the element to be searched for in the array.
	 * @return the index of the specified element if it is found; otherwise, -1.
	 *
	 * @throws NullPointerException if the specified element is null.
	 */
	public int find(T x) {
		for (int i = 0; i < size; i++) {
			if (x.equals(arr[i]))
				return i;
		}
		return -1;
	}

	/**
	 * Converts the internal storage array to a new array of the same type containing only the elements in use.
	 *
	 * @return a new array containing all the elements in this collection.
	 *
	 * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime type of every element in this collection
	 * @throws NullPointerException if the specified array is null
	 */
	@SuppressWarnings("unchecked")
	public T[] toArray(){
		T[] newArr = (T[]) new Object[size];
		System.arraycopy(arr, 0, newArr, 0, size);
		return newArr;
	}


}
