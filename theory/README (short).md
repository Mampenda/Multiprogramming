Multiprogramming and Concurrent Programming
Introduction to Multiprogramming

Multiprogramming refers to the ability of a computer system to execute multiple programs simultaneously or concurrently. In this context, we focus on how multiple Java threads share resources.
The Dining Philosophers Problem

Five philosophers sit around a table with five plates and five utensils. Each philosopher needs two utensils to eat. The problem is to design a solution so that no philosopher starves, alternating between thinking and eating without anyone waiting indefinitely.

```
Philosopher()
{
    While(true)
    {
        Think();
    
        Acquire Right Fork();
        Print("Acquired Right Fork");
        
        Try Acquire Left Fork()
            If(Successful)
                Print("Acquired Left Fork");
                Eat();
            Else
                Release Right Fork;
        EndTry
        
        Release Left and Right Forks();
        Wait(3ms);
    }
}
```

Intrinsic Locks

Intrinsic locks in Java are limited:

- No way to interrupt a thread blocked on acquiring a lock
- No timeout mechanism 
- Strict nesting required for lock acquisition and release

Instead of declaring a method as synchronized, we can use:

```
public void someMethod() {
    synchronized(this) {
    // Method body
    }
}
```

Reentrant Locks

Reentrant locks provide more flexibility:

    Explicit lock() and unlock() methods
    Ability to limit waiting period when acquiring a lock

Example usage:

Lock lock = new ReentrantLock();
lock.lock();
try {
// Use shared resources
} finally {
lock.unlock();
}

Condition Variables

Condition variables allow threads to wait until certain events occur:

```
public class Main {
    public static void main(String[] args) {

        Lock lock = new ReentrantLock();
        lock.lock();
    
        try {
            // use shared resources
        }
        finally {
            lock.unlock();
        }
    }
}
```