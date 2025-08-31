# INF214 H25 

## Lab 1—Concurrency in Java—for week 36

### Task 1

Assume we have variables `x`, `y` and `z`, which all are initialized to 0.
Suppose we have two threads which are as follows.
What are the possible values of variable `x` after running the threads concurrently?
Explain your answer.

```java
Thread t1 = new Thread() {
    public void run() {
        x = y + z;
    }
};
Thread t2 = new Thread() {
    public void run() {
        y = 1;
        z = 2;
    }
};
```

### Possible answer

Possible values for `x`:** `0`, `1`, `2`, or `3`.
There’s no synchronization or `volatile`, so there’s **no happens-before** relationship between the threads. 
That means:

* `t1` reads `y` and `z` independently when evaluating `x = y + z;`.
* `t2` writes `y = 1;` and `z = 2;`

That results in these cases for `t1` reading `y` and `z`:

1. Sees neither update: `y=0`, `z=0` → `x = 0`
2. Sees only `y`’s update: `y=1`, `z=0` → `x = 1`
3. Sees only `z`’s update: `y=0`, `z=2` → `x = 2`
4. Sees both updates: `y=1`, `z=2` → `x = 3`

### Task 2
Implement method `run_both`, which takes two functions as parameters, and calls each of them in a new thread.
The method must return a tuple with the result of the both functions.
You may use any JVM-language (for example, Java, Kotlin, Scala, ...) for this task.

* Please note that Task 1 and Task 2 are **not related** to each other.

### Outline for Task 2 (in Java)

```java
import java.util.concurrent.*;

// class `Tuple` represents a tuple

class Tuple<T1, T2> {
    private final T1 first;
    private final T2 second;

    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }
}


public class Main {
    
    public static <T1, T2> Tuple<T1, T2> run_both(Callable<T1> f1, Callable<T2> f2) throws InterruptedException, ExecutionException {

        // TODO: read about `Callable`

        // Hint: look into `ExecutorService`, `Future`, and thread pools
        
        /* TODO: ... code here ... */

        try {
            // Run the two functions `f1` and `f2`
            Future<T1> run1 = /* TODO: ... code here ... */;
            Future<T2> run2 = /* TODO: ... code here ... */;

            // Wait for the both runs to complete
            T1 result1 = run1.get();
            T2 result2 = run2.get();

            return new Tuple<>(result1, result2);
            
        } finally {
            /* TODO: ...*/
        }
    }


    public static void main(String[] args) {

        // example of using the function `run_both`:

        // f1:
        Callable<Integer> f1 = () -> {
            Thread.sleep(1000);
            return 42;
        };

        // f2:
        Callable<String> f2 = () -> "Greetings INF214!";

        try {
            Tuple<Integer, String> res = run_both(f1, f2);
            System.out.println("Result of function1: " + res.getFirst());
            System.out.println("Result of function2: " + res.getSecond());
        }
        catch (InterruptedException | ExecutionException e) { }
    }
}
```

### Solution for Task 2 (in Java)

```java
package org.example.Threads;

import org.Threads.Tuple;

import java.util.concurrent.*;

/**
 * Utility class that provides a method to run two tasks concurrently and return both results.
 */
public class Lab1Task2 {

    private Lab1Task2() {
    } // constructor to prevent instantiation

    /**
     * Runs two functions concurrently in separate threads and waits for both to complete.
     *
     * @param f1 The first task to execute, provided as a {@link Callable}.
     * @param f2 The second task to execute, provided as a {@link Callable}.
     * @param <T1> The result type returned by the first task.
     * @param <T2> The result type returned by the second task.
     * @return A {@link Tuple} containing the results of both tasks,
     *         with the first element being the result of {@code f1} and the second of {@code f2}.
     * @throws InterruptedException If the current thread was interrupted while waiting.
     * @throws ExecutionException If one of the tasks threw an exception.
     */
    public static <T1, T2> Tuple<T1, T2> run_both(Callable<T1> f1, Callable<T2> f2)
            throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<T1> run1 = executor.submit(f1);
            Future<T2> run2 = executor.submit(f2);

            T1 result1 = run1.get();
            T2 result2 = run2.get();

            return new Tuple<>(result1, result2);

        } finally {
            executor.shutdown();
        }
    }
}
```

```java
package org.example;


import org.Threads.Tuple;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static org.Threads.Lab1Task2.run_both;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // Run Lab 1 Task 2

        // Define the two tasks
        Callable<Integer> f1 = () -> {
            Thread.sleep(1000);
            return 42;
        };

        Callable<String> f2 = () -> "Greetings INF214!";

        // Call run_both on the tasks
        Tuple<Integer, String> results = run_both(f1, f2);

        System.out.println("Result of run_both(): <" + results.getFirst() + ", " + results.getSecond() + ">");
    }
}
```


### Alternative (in Java)

```java
package org.example.Threads;

import org.Threads.Tuple;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In this version, each task tries to lock two resources but can be interrupted (via lockInterruptibly).
 * If that happens, the task reports "interrupted" instead of blocking forever.
 * This way we show both:
 * Safe concurrency (lockInterruptibly + finally { unlock() }),
 * Structured parallel execution (run_both + Future).
 */

public class Interruptible2 {

    // run_both provides a clean pattern for running two threads in parallel and waiting for their results
    public static <T1, T2> Tuple<T1, T2> run_both(Callable<T1> f1, Callable<T2> f2)
            throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(2); // two threads

        // Use try-finally to ensure executor is shut down after tasks complete
        try {

            // Instead of manually t1 and t2, use ExecutorService.submit(Callable) which gives us a Future (placeholder)
            Future<T1> run1 = executor.submit(f1);
            Future<T2> run2 = executor.submit(f2);

            // Future.get() blocks until the task is done, or throws an exception if interrupted.
            T1 result1 = run1.get();  // wait for f1 to be done
            T2 result2 = run2.get();  // wait for f2 to be done

            return new Tuple<>(result1, result2);

        } finally {
            executor.shutdownNow(); // cleanup (stop accepting new submit or execute tasks by sending an interrupt to all actively executing threads).
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        final ReentrantLock l1 = new ReentrantLock();
        final ReentrantLock l2 = new ReentrantLock();

        // Task 1
        Callable<String> task1 = () -> {
            try { // Try to acquire locks interruptibly (it can be interrupted by another thread)
                l1.lockInterruptibly();
                Thread.sleep(1000);
                l2.lockInterruptibly();
                return "Task1 finished normally";

            } catch (InterruptedException e) { // Handle interruption
                return "Task1 interrupted";

            } finally { // Ensure locks are released if held
                if (l1.isHeldByCurrentThread()) l1.unlock();
                if (l2.isHeldByCurrentThread()) l2.unlock();
            }
        };

        // Task 2
        Callable<String> task2 = () -> {
            try {
                l2.lockInterruptibly();
                Thread.sleep(1000);
                l1.lockInterruptibly();
                return "Task2 finished normally";
            } catch (InterruptedException e) {
                return "Task2 interrupted";
            } finally {
                if (l1.isHeldByCurrentThread()) l1.unlock();
                if (l2.isHeldByCurrentThread()) l2.unlock();
            }
        };

        // Run both tasks concurrently
        Tuple<String, String> results = run_both(task1, task2); // wait for both to finish

        // Print results
        System.out.println("Result of task1: " + results.getFirst());
        System.out.println("Result of task2: " + results.getSecond());
    }
}
```