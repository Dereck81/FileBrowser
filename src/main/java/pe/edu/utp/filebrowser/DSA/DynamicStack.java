package pe.edu.utp.filebrowser.DSA;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class DynamicStack<T> implements Serializable {
    private final DynamicArray<T> arr;
    private int top;

    /**
     * Constructs a stack with the specified capacity.
     *
     */
    public DynamicStack(){
        //arr = (T[]) new Object[capacity];
        arr = new DynamicArray<>();
        top = -1;
    }

    /**
     * Pushes an element onto the stack.
     *
     * @param element the element to push onto the stack
     */
    public void push(T element){
        arr.pushBack(element);
        top++;
    }

    /**
     * Peeks at the top element of the stack without removing it.
     *
     * @return the top element of the stack, or null if the stack is empty
     */
    public T peek(){
        // return arr.at(top);
        try{ return arr.at(top);}
        catch (Exception _) {return null;}

    }

    /**
     * Pops the top element off the stack.
     *
     * @return the top element of the stack, or null if the stack is empty
     */
    public T pop(){
        T element =  arr.pop();
        top--;
        return element;
    }

    /**
     * Checks if the stack is empty.
     *
     * @return true if the stack is empty, false otherwise
     */
    public boolean isEmpty(){
        return top == -1;
    }

    /**
     * Gets the number of elements in the stack.
     *
     * @return the size of the stack
     */
    public int size(){
        return arr.size();
    }

    /**
     * Clears the stack, removing all elements.
     */
    public void clear(){
        arr.clear();
        top = -1;
    }

    /**
     * Converts the stack to an array.
     *
     * @return an array containing all elements in the stack
     */
    @SuppressWarnings("unchecked")
    public T[] toArray(){
        Object[] arr1 = new Object[arr.size()];
        int index = 0;
        for(T element: arr){
            arr1[index++] = element;
        }
        return (T[]) arr1;
    }

    /**
     * Converts the stack to a list.
     *
     * @return a list containing all elements in the stack
     */
    public List<T> toList(){
        return Arrays.asList(toArray());
    }

}