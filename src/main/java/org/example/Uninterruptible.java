package org.example;

public class Uninterruptible {
    public static void main(String[] args) throws InterruptedException {

        // Two objects
        final Object o1 = new Object();
        final Object o2 = new Object();

        // First thread
        Thread t1 = new Thread(() -> {
            try {
                // Acquire the intrinsic lock of first object
                synchronized (o1) {

                    // Put thread to sleep for 1 sec
                    Thread.sleep(1000);

                    // Acquire the intrinsic lock of second object
                    synchronized(o2) {
                        // Do nothing
                    }
                }
            }
            catch(InterruptedException e) {
                System.out.println("Thread 1 interrupted.");
            }
        });

        // Second thread
        Thread t2 = new Thread(() -> {
            try {
                // Acquire the intrinsic lock of second object
                synchronized(o2) {

                    // Put thread to sleep for 1 sec
                    Thread.sleep(1000);

                    // Acquire the intrinsic lock of first object
                    synchronized(o1) {
                        // Do nothing
                    }
                }
            }
            catch(InterruptedException e) {
                System.out.println("Thread 2 interrupted.");
            }
        });

        t1.start();
        t2.start();
        Thread.sleep(2000);
        t1.interrupt();
        t2.interrupt();
        t1.join();
        t2.join();
    }
}
