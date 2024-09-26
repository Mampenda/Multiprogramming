package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {
    public static void main(String[] args) throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();

        class CountingThread extends Thread {
            public void run(){
                for (int i = 0; i < 10000; i++){

                    /** This method of the class AtomicInteger is functionally equivalent to the class count++,
                     *  but it performs atomically. The use of atomic variables instead of locks gives many advantages:
                     *  1. We cannot anymore forget to acquire the lock before we perform some operation
                     *  2. We avoid deadlocks
                     *
                     *  Atomic variables are the basis of the non-blocking, lock-free algorithms, which achieve
                     *  synchronization without blocks and locking
                    */
                    counter.incrementAndGet();
                }
            }
        }
    }

//    class Counter {
//        private int count = 0;
//        /**What happens the inc and dec methods is
//        * GET field count
//        * LOAD 1
//        * PUT field count
//        */
//        public synchronized void increment() { count++; }
//        public synchronized void decrement() { count--; }
//        public synchronized int getCount() { return count; }
//    }
//    final Counter conuter = new Counter();
}
