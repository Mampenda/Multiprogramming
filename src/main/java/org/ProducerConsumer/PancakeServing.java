/*
package org.example.ProducerConsumer;


import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


*/
/*
* Which of the properties of concurrent programs (Safety, Liveness, Fairness) are ensured here?
*
* Safety: ensures that the program avoids undesirable outcomes like race conditions or invalid states.
*   In this code:
*   The lock ensures mutual exclusion, meaning only one thread can modify the shared plate at a time.
*   The Condition variables prevent underflow and overflow.
*   The proper use of await and signal ensures no two threads concurrently access plate.
* Thus, the program guarantees correct state maintenance at all times, ensuring Safety.
*
* Liveness - ensures that threads can always make progress and are not indefinitely blocked.
* In this code:
* The producer and consumer both use await to wait for specific conditions (stack not full or not empty) but are woken up via signal, preventing starvation or indefinite blocking.
* However, liveness can be compromised if...:
*   The producer or consumer is permanently delayed (e.g., system thread scheduling issues).
*   If the signals are lost due to exceptions or external interruptions.
* In ideal conditions, Liveness is maintained, but it could be threatened under exceptional circumstances.
* Thus, Liveness is partially ensured
*
* Fairness - Fairness ensures that all threads get a chance to execute without one being starved.
*   In this code:
*       There is no explicit mechanism for ensuring fair scheduling.
*       Threads are awakened based on the signal call, but it's possible for one thread (e.g., producers) to monopolize the lock depending on the scheduling policy of the JVM.
*       The ReentrantLock does not guarantee fairness unless it is constructed with true (fair mode), which is not done here.
* Thus, Fairness is not guaranteed by default in this implementation.
*
* Conclusion:
*   1. Safety: Ensured
*   2. Liveness: Partially Ensured
*   3. Fairness: Not Ensured
* *//*


public class PancakeServing {
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmtpy = lock.newCondition();

    private final int limit = 100;
    private Stack<String> plate = new Stack<String>();

    public void produce() throws InterruptedException {
        lock.lock();

        try{
            while(plate.size() == limit){
                notFull.await();
            }
            Thread.sleep(10); // Make pancake
            plate.add("Pancake: " + plate.size());
            System.out.println("Made a pancake and added it to the stack");
            notEmtpy.signal();
        } finally {
            lock.unlock();
        }
    }

    public void consume() throws InterruptedException {
        lock.lock();

        try{
            while(plate.size() == 0){
                notEmtpy.await();
            }
            plate.pop();
            Thread.sleep(10); // Eat pancake
            System.out.println("Ate a pancake from the stack");
            notFull.signal();
        } finally {
            lock.unlock();
        }
    }
}*/
