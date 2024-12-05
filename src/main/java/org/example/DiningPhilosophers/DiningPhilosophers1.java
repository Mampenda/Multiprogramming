package org.example.DiningPhilosophers;


import java.util.ArrayList;
import java.util.Random;

/*
* Dining Philosophers
* In this example, we have three philosophers and three chopsticks.
* Each philosopher first tries to grab the left chopstick, then the right one.
*
* This program is at risk of a deadlock.
* This program could terminate.
* This program use intrinsic locks on the chopsticks.
* */
public class DiningPhilosophers1 {

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

        ArrayList<DiningPhilosopher> philosophers = new ArrayList<>();
        philosophers.add(new DiningPhilosopher(chopstick1, chopstick2, "Hypatia of Alexandria"));
        philosophers.add(new DiningPhilosopher(chopstick2, chopstick3, "Aspasia of Miletus"));
        philosophers.add(new DiningPhilosopher(chopstick3, chopstick1, "Diotima of Mantinea"));

        for (DiningPhilosopher philosopher : philosophers) {
            new Thread(philosopher).start();
        }
    }
}
