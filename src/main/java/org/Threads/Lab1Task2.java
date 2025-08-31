package org.Threads;

import java.util.concurrent.*;

/**
 * Utility class that provides a method to run two tasks concurrently and return both results.
 */
public class Lab1Task2 {

    private Lab1Task2() {} // constructor to prevent instantiation

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

        // Create a thread pool with two threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // Submit both tasks to the executor. Returns a Future placeholder for the result.
            Future<T1> run1 = executor.submit(f1);
            Future<T2> run2 = executor.submit(f2);

            // Wait for both tasks to complete and retrieve their results
            T1 result1 = run1.get();
            T2 result2 = run2.get();

            return new Tuple<>(result1, result2);

        } finally {
            // Cleanup (stop tasks by sending an interrupt to all actively executing threads)
            executor.shutdown();
        }
    }
}