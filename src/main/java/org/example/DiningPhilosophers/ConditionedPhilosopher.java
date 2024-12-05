package org.example.DiningPhilosophers;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionedPhilosopher extends Thread{

    private boolean eating;
    private ConditionedPhilosopher philosopherToTheLeft;
    private ConditionedPhilosopher philosopherToTheRight;
    private ReentrantLock table;
    private Condition condition;
    private Random random;

    public ConditionedPhilosopher(ReentrantLock table) {
        this.table = table;

        eating = false;
        condition = table.newCondition();
        random = new Random();
    }

    public void setLeft(ConditionedPhilosopher left) { this.philosopherToTheLeft = left; }
    public void setRight(ConditionedPhilosopher right) { this.philosopherToTheRight = right; }

    private void eat() throws InterruptedException {
        // A philosopher first locks the table so the other philosophers cannot change the state
        table.lock();

        // Check if the neighbours to the left and right are eating
        try {
            // Wait while they're eating
            while(philosopherToTheLeft.eating || philosopherToTheRight.eating ){
                condition.await();
            }
            // Eat when they're not eating
            eating = true;
        }
        finally { table.unlock(); }

        // Takes 1 sec to eat
        Thread.sleep(1000);
    }

    private void think() throws InterruptedException{
        // If a philosopher wants to think, she should first acquire the lock
        table.lock();

        // Change eating to false, because at this table, the philosophers are only thinking when they're not eating
        try {
            eating = false;

            // Inform neighbours that they can start eating
            philosopherToTheLeft.condition.signal();
            philosopherToTheRight.condition.signal();
        }
        finally { table.unlock(); }

        // Takes 1 sec to think
        Thread.sleep(1000);
    }

    public void run() {
        try {
            while(true){
                think();
                eat();
            }
        }
        catch (InterruptedException e) { e.printStackTrace(); }
    }
}
