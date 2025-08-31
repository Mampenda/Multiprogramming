package org.DiningPhilosophers;

import java.util.ArrayList;
import java.util.Random;

/*
* Dining Philosophers2
* Adding an extra chopstick to DiningPhilosophers1 will solve a potential deadlock.
* */
public class DiningPhilosophers2 {

    static class DiningPhilosopher implements Runnable {
        private Object left;
        private Object right;
        private String name;
        private Random random = new Random();

        public DiningPhilosopher(Object left, Object right, String name) {
            this.left = left;
            this.right = right;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(random.nextInt(3));
            } catch (InterruptedException e) {}
            synchronized (left) {
                System.out.println(this.name + " picked up " + left);
                synchronized (right) {
                    System.out.println(this.name + " picked up " + right);
                    System.out.println(this.name + " eats with both chopsticks and thinks for a bit.");
                }
            }
        }
    }

    public static void main(String[] args) {
        Object chopstick1 = new Object();
        Object chopstick2 = new Object();
        Object chopstick3 = new Object();
        Object chopstick4 = new Object(); // Added another chopstick and gave it to Diotima

        ArrayList<DiningPhilosophers1.DiningPhilosopher> philosophers = new ArrayList<>();
        philosophers.add(new DiningPhilosophers1.DiningPhilosopher(chopstick1, chopstick2, "Hypatia of Alexandria"));
        philosophers.add(new DiningPhilosophers1.DiningPhilosopher(chopstick2, chopstick3, "Aspasia of Miletus"));
        philosophers.add(new DiningPhilosophers1.DiningPhilosopher(chopstick3, chopstick4, "Diotima of Mantinea"));

        for (DiningPhilosophers1.DiningPhilosopher philosopher : philosophers) {
            new Thread(philosopher).start();
        }
    }
}
