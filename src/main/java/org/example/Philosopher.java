package org.example;

import java.util.Random;

/*
* This is a simple implementation of the Dining Philosophers problem.
* */
public class Philosopher extends Thread{
    // The first example using left/right will end in a deadlock when ran long enough!
    private Chopstick first, second;
    private Random random;

    public Philosopher(Chopstick left, Chopstick right) {
        if (left.getId() < right.getId()) {
            first = left;
            second = right;
        }
        else {
            first = right;
            second = left;
        }
        random = new Random();
    }

    public void run() {
        try {
            while (true) {
                System.out.println("Philosopher " + this + " has thought.");

                Thread.sleep(random.nextInt(1000)); // Philosophers thinking
                synchronized (first) {   // Take fst chopstick
                    synchronized (second) {  //Take snd chopstick
                        Thread.sleep(random.nextInt(1000));  // Eat
                    }
                }
            }
        }
        catch (InterruptedException e) {}
    }
}
