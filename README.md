# Multiprogramming
In short, multiprogramming is when multiple programs execute simultaneously (or concurrently). In this repo, we will 
touch on how multiple java `Threads` shares resources. 

---

## The Dining Philosophers

### The Problem

> In computer science, the dining philosophers problem is an example problem often used in concurrent algorithm design
> to illustrate synchronization issues and techniques for resolving them.
>
>   -- [Wikipedia](https://en.wikipedia.org/wiki/Dining_philosophers_problem)

In short, five philosophers are seated around a table with five plates and five utensils available. For simplicity,
we'll use chopsticks. Each philosopher needs two chopsticks to be able to eat, so there's at most two philosophers that
can eat at the same time. After a philosopher has finished eating, she'll put down both chopsticks, making them
available for the other philosophers.

The problem is to design a solution (a concurrent algorithm) so that no philosopher will starve, i.e., each will
alternate between thinking and eating without anyone having to wait to eat forever.

---

## Intrinsic Locks
For a long time, intrinsic locks is all the support that Java provided for concurrent programming. However, now we can 
use the library in the package `Java.util.concurrent` which provides enhanced locking mechanisms. Intrinsic locks are 
convenient, but limited. Problems with them are 
- There's no way to interrupt a thread that's blocked as a result of trying to acquire an intrinsic lock. 
- There's no way to time-out while trying to acquire an intrinsic lock. 
- There's exactly one way to acquire an intrinsic lock, by using the `synchronized` key-word. 

Which means that the lock acquisition- and release method have to take place in the same method and have to be very 
strictly nested. Also, that declaring a method as `synchronized` is just a syntactic sugar for surrounding the methods 
body with a `synchronized (this){}` block. 

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

### Overcoming restrictions of Intrinsic lock
We will now have a look on how the class Reentrant lock helps us overcome the restrictions that intrinsic locks have. 

Because the execution of a `Thread` that is blocked on an intrinsic lock cannot be interrupted, we have no way to 
recover from a deadlock. We can see an example of this in the class `Uninterruptable` that produces a deadlock situation 
and then tries to interrupt the threads. When ran, the class is going to deadlock forever so the only way to exit it 
will be to kill the program. 

The solution to this problem is to implement the code with reentrant locks instead of with intrinsic locks. 
In the class `Interruptible`, both treads are interruptible, and when running the code, both threads indeed gets 
interrupted.

---

## The Dining Philosophers 

### Solution 1
**Reentrant Locks**

Reentrant Locks allows to overcome another limitation of intrinsic locks. The class allows us the limit the waiting 
period when we try to acquire a lock. We can see how this provides a possible solution for the dining philosophers 
problem in the `Philosopher`-class.  

The `tryLock()`-method that is used prevents infinite deadlocks, but that doesn't automatically mean that this is a good 
solution. It doesn't avoid deadlocks, it just provides a way to recover when it happens. Secondly, there's still a 
possibility that a _livelock_ occurs. A livelock is when all threads timeout, or that all philosophers are stuck waiting
for the right chopstick, resulting in a deadlock. Although the deadlock doesn't last forever, it means that no progress 
can be made either, i.e. nothing gets executed in the program. 

This situation can be mitigated by giving each thread a different time-out value, this will f.ex. minimize the chances 
that they will all timeout simultaneously. All in all, using timeouts provides a solution, but it is far better to avoid
deadlocks all together.

---

## Condition variables
Quite often in concurrent programming, there's a need to wait until a certain event happens. For example, one might need
to wait for a moment when a queue becomes non-empty before removing an element from it , or we need to wait before some 
space becomes available in a buffer before we can add something to it. This is where _condition variables_ come into play.

Let's see how we can use condition variables effectively in code. We shall follow a simple example in this sudo code 
which uses the method `await()`.
```
    // Define an reentrant lock object
    ReentrantLock reentrantLock = new ReentrantLock();

    // Define condition that we initialize with the method, 'new Condition()', of the lock that we have defined
    Condition condition = reentrantLock.newCondition();

    // Lock the reentrant lock
    reentrantLock.lock();

    // try-finally block
    try {
        // While the condition is not true, we wait for the condition to be true
        while(){

            // await() automatically releases the lock and blocks the thread on the condition variable.
            condition.await();
        }
        // Use shared resources after condition has become true

    }
    // Unlock the lock after using the shared resources
    finally { reentrantLock.unlock(); }
```
To indicate that the condition variable is true, another thread would use the method `signal()` or `signalAll()`, to 
signal that the condition has become true. The method `await()` will then unblock and re-acquire the lock.

When a thread is doing operations in a try-finally block using `await()`, it looks as if this operation done _atomically_ 
for other threads, i.e. it looks like a single operation that is either performed or not. An _atomic_ operation cannot be 
suspended halfway to give the control to other threads. 

When the method `await()` returns, it only means that the condition _might_ be true, this is why `await()` is invoked in
a loop. Indeed, the thread should go back to check whether the condition is true and potentially block on `await()` 
again and wait for it to become true once more.

---

## The Dining Philosophers

### Solution 2
Let's now get back to the Dining Philosophers Problem for another potential solution. Instead of having a separate class
for chopsticks, but use the fact that a philosopher can eat only if the neighbour to the left and right are thinking.