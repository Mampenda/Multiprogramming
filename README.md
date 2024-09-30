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

---
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

Here is the code for the `Main()`-method for the `ReentrantPhilosopher`-class: 

```
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        // List of five philosophers
        ReentrantPhilosopher[] philosophers = new ReentrantPhilosopher[5];

        // List of five chopsticks
        ReentrantLock[] chopsticks = new ReentrantLock[5];

        // For each chopstick, we initialize them before starting the corresponding thread
        for (int i = 0; i < 5; i++){
            chopsticks[i] = new ReentrantLock();
        }

        // For each philosopher, we initialize them before starting the corresponding thread
        for (int i = 0; i < 5; i++) {
            philosophers[i] = new ReentrantPhilosopher(chopsticks[i], chopsticks[(i+1) % 5]);
            philosophers[i].start();
        }

        // Use the join()-method to wait until each of the corresponding threads finishes execution
        for (int i = 0; i < 5; i++) {
            philosophers[i].join();
        }
    }
}
```
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

When a thread is doing operations in a try-finally block using `await()`, it looks as if this operation done 
_atomically_ for other threads, i.e. it looks like a single operation that is either performed or not. An _atomic_ 
operation cannot be suspended halfway to give the control to other threads. 

When the method `await()` returns, it only means that the condition _might_ be true, this is why `await()` is invoked in
a loop. Indeed, the thread should go back to check whether the condition is true and potentially block on `await()` 
again and wait for it to become true once more.
---
## The Dining Philosophers

### Solution 2
Let's now get back to the Dining Philosophers Problem for another potential solution. Instead of having a separate class
for chopsticks, but use the fact that a philosopher can eat only if the neighbour to the left and right are thinking.

The main class for this solution is 

```

```

---
## Atomic Variables
This method of the class AtomicInteger is functionally equivalent to the class count++, but it performs atomically. The 
use of atomic variables instead of locks gives many advantages:
 1. We cannot anymore forget to acquire the lock before we perform some operation
 2. We avoid deadlocks
Atomic variables are the basis of the non-blocking, lock-free algorithms, which achieve synchronization without locks 
and blocking.

### Volatile variables
In Java, you can mark a variable as 'volatile'. Doing so, guarantees that the reads and writes to that variable will not
be rewarded by the compiler or Java Virtual Machine. However, volatile is a very weak form of synchronization. It would
not help fix the `AtomicCounter`-class because making `count` volatile would not ensure that `count++` is atomic. 

---
## Recap 

So far, we've discussed _sequential_ programs, which has one Thread of execution, and _concurrent_ programs, which has 
multiple Threads of execution. 

We will study two different approaches to concurrent programs:
- Shared Memory concurrency
  - Threads communicate via variables in shared memory
  - Access to those variables must be synchronized
- Message Passing concurrency
  - Threads communicate by sending/receiving messages
  - All memory is local to threads/processes.
  - Distributed programs:
    - They execute on different machines
    - They communicate over a network
---
### Why Concurrent Programs? Why do we study this topic?
1. Performance - Time gets saved if work can be subdivided into concurrent tasks.
2. To model concurrent phenomena such as: 
   - Graphical User Interface (GUI) events 
   - Access to the same information/resources by many
---
## Shared Memory Concurrency
Shared memory concurrency is important because:
- It matches common hardware (many CPUs, each have many cores)
- It is supported by mainstream languages (Java, C++, ect... )
- Even on one-core machines, it is a natural model for capturing real life concurrency

Basic properties for shared memory concurrency: 
- Each thread executes its own sequence of operations
- The operating system decides 
  - which thread gets a turn to execute
  - for how long a thread gets to execute
  - on which core a thread executes 
    - The programmer may block threads, but _not_ force them to execute

## Simple Programming Language Await

In what follows in this course we'll use a special version of a imperative programming language called Simple 
Programming Language Await. It has a normal C-style syntax and Hoare-style pre-/post-conditions that may surround every
instruction in the language. 
---
Example: 
```
// First statement takes variable x and increases it by value z
x = x+z;

// Second statement takes a variable y and increases it by value z
y = y+z;
```
Now we want to analyse what the state of the program is before these two statements gets executed
> Pre-condition:   x == a && y == b (what is assumend to be true before a program runs)

> Run the two statements

> Post-condition:  x = a+z && y = b+z (what will be true after the program runs, if it terminates) 

NOTE: pre-/post-conditions are not executed, they're only for the programmer to analyse the code. 

---

In addition to pre-/post-conditions that we can add to every statement, we also have the parallel execution operators: 
`co` ... `oc`. The dots between the two operators will be a list of processes, separated by the double vertical bars 
`co S_1 || S_2 || S_3 || ... || S_N oc` where the programs `S_1, ..., S_N` are executed concurrently, and the whole 
statement terminates after all the processes has all finished terminating. So for the example code above would run 
concurrently if it was written like this: 
```
co 
x = x+z; || y = y+z;
oc
```
The pre- and post-conditions would still be valid because processes are independent, i.e. they do not write to the same
variables. Neither writes to a variable that the other reads, so there's no interference. In general, parallelizing 
statements invalidates the post-conditions, that is, in the sequential version, the post-conditions hold, but in the 
concurrent version, the post-condition it might not hold. 

In our conditions, the post-condition holds because of their independence, but in the following example, the post 
condition changes: 
```
// pre-condition: {x == 0}
co x = x+1; || x = x-1 oc
// post-condition: {x == -1 OR x == 0 OR x == 1}
```
If this code had been run sequentially, the post-condition `{x==0}`, would be true, but since it's ran concurrently
the post-condition changes. This is because with sequential programs the scheduler assumes that the operations of all 
individual processes are executed in a sequential order, where each process's operations are in program order. 
```
// P1 and P2 consists of the following operations 
P1: OP1, OP2, ..., OP4
P2: OP1, OP2, ..., OP5

//Then the program executes them f.ex. like
P1_OP1
P1_OP2
P2_OP1
P1_OP3
P2_OP2
P1_OP4
P2_OP3
P2_OP4
P2_OP5
```
So the two programs are executed in order, even though they're interchanging. So, the result of an execution is the same
as the order they're written, and the execution if sequentially consistent. If the operations were executed like this 
```
P1_OP1
P1_OP2
P2_OP1
P1_OP4
P2_OP2
P1_OP3
...
```
Then this would not be true, `P1_OP4` is executed before `P1_OP3`, so they are not executed in program order. 












