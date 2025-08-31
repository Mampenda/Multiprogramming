package org.DiningPhilosophers;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*
* This is a simple implementation of the Dining Philosophers problem.
* */
public class ReentrantPhilosopher extends Thread{

    private ReentrantLock leftChopstick, rightChopstick;
    private Random random;

    public ReentrantPhilosopher(ReentrantLock left, ReentrantLock right) {

        this.leftChopstick = left;
        this.rightChopstick = right;

        random = new Random();
    }
    // Use reentrant lock instead of (synchronized) intrinsic locks
    public void run() {
        try {
            while (true) {
                System.out.println("Philosopher " + this + " has thought.");

                //Thinking
                Thread.sleep(random.nextInt(1000));

                //Lock left chopstick (philosopher picks up the left chopstick)
                leftChopstick.lock();
                try {

                    //If the philosopher picks up the right chopstick
                    if(rightChopstick.tryLock(1000, TimeUnit.MILLISECONDS)) {
                        try {
                            //Eating
                            Thread.sleep(random.nextInt(1000));
                            System.out.println("Philosopher " + this + " is eating.");
                        }
                        finally {
                            // The chopstick should be unlocked regardless
                            rightChopstick.unlock();
                        }
                    }
                    else {
                        // Did not pick up the right chopstick, so the philosopher goes back to thinking
                    }
                }
                finally {
                    leftChopstick.unlock();
                }
            }
        }
        catch (InterruptedException e) {}
    }
}
