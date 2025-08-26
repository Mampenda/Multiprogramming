package org.example;


import org.example.Threads.Tuple;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static org.example.Threads.Lab1Task2.run_both;

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