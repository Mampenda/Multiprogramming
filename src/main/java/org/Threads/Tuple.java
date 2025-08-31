package org.Threads;

// Generic tuple class
public class Tuple<T1, T2> {

    // Fields
    private final T1 first;
    private final T2 second;

    // Constructor
    public Tuple(T1 first, T2 second) {
        this.first = first; this.second = second;
    }

    // Getters
    public T1 getFirst() { return first; }
    public T2 getSecond() { return second; }
}