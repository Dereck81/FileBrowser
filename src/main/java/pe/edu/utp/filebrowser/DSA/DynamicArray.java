package pe.edu.utp.filebrowser.DSA;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/***
 * @author cerofour "Diego Alexis Llacsahuanga Buques"
 * @param <T>
 */

public class DynamicArray<T> implements Iterable<T>, Serializable {
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

	/*
	There may be elements that have not been deleted during the copy,
	but this is not taken into account since adding another element
	will overwrite it.
	 */
	public void delete(int index) {
		if(index < 0 || index >= size) return;
		if(index != size-1)
			System.arraycopy(arr, index+1, arr, index, size - index - 1);
		size--;
	}

	/**
	 * Finds a given element in the array
	 * @param x
	 * @return the index of the target element, -1 if not found.
	 */
	public int find(T x) {
		for (int i = 0; i < size; i++) {
			if (x.equals(arr[i]))
				return i;
		}
		return -1;
	}
}
