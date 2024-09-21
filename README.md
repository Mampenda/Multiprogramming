# Beyond Intrinsic Locks

So far, we have used intrinsic locks that are build into every Java object. For a long time, this is all the support 
that Java provided for concurrent programming. However, now we can use the library in the package Java.util.concurrent 
which provides enhanced locking mechanisms.

Intrinsic locks are convenient, but limited. Problems with it are 
- There's no way to interrupt a thread that's blocked as a result of trying to acquire an intrinsic lock. 
- There's no way to time-out while trying to acquire an intrinsic lock. 
- There's exactly one way to acquire an intrinsic lock, by using the `synchronized` key-word. Which means that the lock 
acquisition- and release method have to take place in the same method and have to be very strictly nested. 

Also, that declaring a method as `synchronized` is just a syntactic sugar for surrounding the methods body with a 
`synchronized (this){}` block. 

So instead of declaring a method as synchronized like this `public synchronized void someMethod() {}`, we could write 
it like this
```
public void someMethod(){
    synchronized(this){
    //method body
    }
}
```
So a `synchronized` method is the same as putting the method body into a `synchronized (this){}` block. 

## Reentrant Locks
Reentrant locks allows us to go beyond the restrictions of Intrinsic locks by providing explicit `lock()` and `unlock()` 
methods. 

```
public class Main {
    public static void main(String[] args) {

        Lock lock = new ReentrantLock();
        lock.lock();
    
        try {
            // use shared resources
        }
        finally {
            lock.unlock();
        }
    }
}
```
The try-finally block is a good practice to ensure that the lock is always released no matter what happens in the code 
that the lock is protecting.

#### Overcoming restrictions of Intrinsic lock
Let's now have a look on how the class Reentrant lock helps us overcome the restrictions that intrinsic locks have. 
Because the execution of a `Thread` that is blocked on an intrinsic lock cannot be interrupted, we have no way to recover 
from a deadlock. We can see this in the following example that produces a deadlock situation and then tries to interrupt
the threads. 

```
public class Uninterruptable {
    public static void main(String[] args) throws InterruptedException {

        // Two objects
        final Object o1 = new Object();
        final Object o2 = new Object();

        // First thread
        Thread t1 = new Thread() {
            public void run() {
                try {
                    // Aqquire the intrinsic lock of first object
                    synchronized (o1) {

                        // Put thread to sleep for 1 sec
                        Thread.sleep(1000)

                        // Aqquire the intrinsic lock of second object
                        synchronized(o2) {
                            // Do nothing
                        }
                    }
                }
                catch(InterruptedException e) {
                    System.out.println("Thread 1 interrupted.")
                }
            }
        }

        // Second thread
        Thread t2 = new Thread() {
            public void run() {
                try {
                    // Aqquire the intrinsic lock of second object
                    synchronized(o2) {

                        // Put thread to sleep for 1 sec
                        Thread.sleep(1000)

                        // Aqquire the intrinsic lock of first object
                        synchronized(o1) {
                            // Do nothing
                        }
                    }
                }
                catch(InterruptedException e) {
                    System.out.println("Thread 2 interrupted.")
                }
            }
        }

        t1.start();
        t2.start();
        Thread.sleep(2000);
        t1.interrupt();
        t2.interrupt();
        t1.join();
        t2.join();
    }
}
```

When ran, this program is going to deadlock forever. The only way to exit it will be to kill the Java virtual machine 
that ran it. The solution to this problem is to implement this with reentrant locks instead of with intrinsic locks. 
In the code below, both treads are interruptable, and when running the code, both threads indeed gets interrupted. 
```
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
```
Reentrant Locks allows to overcome yet another limitation 