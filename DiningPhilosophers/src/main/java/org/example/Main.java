package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Two list of 5 philosophers and 5 chopsticks
        Philosopher[] philosophers = new Philosopher[5];
        Chopstick[] chopsticks = new Chopstick[5];


        for (int i = 0; i < 5; i++) {
            chopsticks[i] = new Chopstick(i);
        }

        // For each philosopher, we run the constructor before starting the corresponding thread
        for (int i = 0; i < 5; i++) {
            philosophers[i] = new Philosopher(chopsticks[i], chopsticks[(i+1) % 5]);
            philosophers[i].start();
        }

        // Use the join()-method to wait until each of the corresponding threads finishes execution
        for (int i = 0; i < 5; i++) {
            philosophers[i].join();
        }
    }
}