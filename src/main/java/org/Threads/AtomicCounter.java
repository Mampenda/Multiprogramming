package org.Threads;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {
    public static void main(String[] args) throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();

        class CountingThread extends Thread {
            public void run(){
                for (int i = 0; i < 10000; i++){
                    counter.incrementAndGet();
                }
            }
        }

        CountingThread thread1 = new CountingThread();
        CountingThread thread2 = new CountingThread();

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
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
