package org.example;

import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        // Reentrant locks for each thread
        final ReentrantLock l1 = new ReentrantLock();
        final ReentrantLock l2 = new ReentrantLock();

        // Two objects
        final Object o1 = new Object();
        final Object o2 = new Object();

        // First thread
        Thread t1 = new Thread() {
            public void run() {
                try {
                    // Interruptibly lock first lock
                    l1.lockInterruptibly();

                    // Put thread to sleep for 1 sec
                    Thread.sleep(1000);

                    // Interruptibly lock second lock
                    l2.lockInterruptibly();
                }
                catch(InterruptedException e) {
                    System.out.println("Thread 1 interrupted.");
                }
            }
        };

        // Second thread
        Thread t2 = new Thread() {
            public void run() {
                try {
                    // Interruptibly lock second lock
                    l2.lockInterruptibly();

                    // Put thread to sleep for 1 sec
                    Thread.sleep(1000);

                    // Interruptibly lock first lock
                    l1.lockInterruptibly();
                }
                catch(InterruptedException e) {
                    System.out.println("Thread 2 interrupted.");
                }
            }
        };

        t1.start();
        t2.start();
        Thread.sleep(2000);
        t1.interrupt();
        t2.interrupt();
        t1.join();
        t2.join();
    }
}