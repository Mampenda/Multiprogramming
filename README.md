# Beyond Intrinsic Locks

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

## Overcoming restrictions of Intrinsic lock
We will now have a look on how the class Reentrant lock helps us overcome the restrictions that intrinsic locks have. 

Because the execution of a `Thread` that is blocked on an intrinsic lock cannot be interrupted, we have no way to 
recover from a deadlock. We can see an example of this in the class `Uninterruptable` that produces a deadlock situation 
and then tries to interrupt the threads. When ran, the class is going to deadlock forever so the only way to exit it will 
be to kill the program. 

The solution to this problem is to implement the code with reentrant locks instead of with intrinsic locks. 
In the class `Interruptible`, both treads are interruptible, and when running the code, both threads indeed gets 
interrupted. 

### The Dining Philosophers 

#### The Problem

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

#### Solutions 

Reentrant Locks allows to overcome another limitation 