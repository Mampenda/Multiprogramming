package org.example.Threads;

import java.util.concurrent.*;
        import java.util.concurrent.locks.ReentrantLock;

public class Interruptable2 {

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
            try { // Try to acquire locks interruptibly (it can be interruted by another thread)
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

// Generic tuple class
class Tuple<T1, T2> {

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




/**
 * In my Interruptible version, each task tries to lock two resources but can be interrupted (via lockInterruptibly).
 * If that happens, the task reports "interrupted" instead of blocking forever.
 * This way we show both:
 * Safe concurrency (lockInterruptibly + finally { unlock() }),
 * Structured parallel execution (run_both + Future).
 */