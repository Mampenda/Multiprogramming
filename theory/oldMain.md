
```java
package org.example;

import org.example.DiningPhilosophers.ConditionedPhilosopher;

import java.util.concurrent.locks.ReentrantLock;

public class OldMain {
public static void main(String[] args) throws InterruptedException {

        // Reentrant Lock
        ReentrantLock table = new ReentrantLock();

        // List of five philosophers
        ConditionedPhilosopher[] philosophers = new ConditionedPhilosopher[5];

        // For each philosopher, we initialize them before starting the corresponding thread
        for (int i = 0; i < 5; i++) {
            philosophers[i] = new ConditionedPhilosopher(table);
        }

        // Seat them around the table
        for (int i = 0; i < philosophers.length; i++) {
            for (int j = 0; j < philosophers.length; j++) {

                // Seat a philosopher on the left side
                philosophers[i].setLeft(philosophers[j]);

                //Seat a philosopher at the right
                philosophers[i].setRight(philosophers[j-2]);
            }

        }

        // Use the run()-method to execute all the philosophers
        for (int i = 0; i < philosophers.length; i++) {
            philosophers[i].run();
            philosophers[i].join();
        }
    }
}
```