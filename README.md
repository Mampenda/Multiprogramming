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

In what follows in this course we'll use a special version of an imperative programming language called Simple 
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
> Pre-condition:   x == a && y == b (what is assumed to be true before a program runs)

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

Sequential consistency is _wishful thinking_ because compilers and hardware re-order operations, so in reality we cannot
ensure that the operations are executed in the order they're written. In reality, the operations are executed with 
relaxed memory models, so the sequential consistency is preserved for data-race free programs. Data-race means that two
processes concurrently access the same memory locations and at least one access of this is a `write` and the accesses 
are not synchronized.

---

## Atomic Operations
An atomic operation is an operation that cannot be subdivided, that is, in the middle of an atomic operation we have no
observable, or intermediate, state. They are language-dependant. Only reading or only writing a variable is usually 
atomic, but reading AND writing to a variable is not. 

So ```x=y``` is not atomic because we first read the variable `y` before writing it to the variable `x`, and `++x` is
not atomic either because we first have to `read` the variable `x` before we `write` to it (by adding 1 to it).

There are special atomic operations/atomic variable types in programming languages, f.ex. in Java it was `AtomicInteger`
and in C++ we might use `atomic<int> x;` and then `x += 1` would be atomic. 

Some language constructs may allow creating large atomic blocks. A statement with at most one atomic operation plus
operations on local variables can be considered atomic. This is referred to as the _**AT MOST ONCE**_ rule.

> At most once
> 
> When a code statement has at most one atomic operation and the rest of the operations are performed on the local 
> variables, then the whole statement can be considered atomic.

#### Mutual exclusion
Atomic operations on a variable cannot happen simultaneously. In a way, one "happens" before the other, in that case we 
have mutual exclusion. 

---

Let's go back to the example:
```
co x = x+1; || x = x-1 oc
```
What are the atomic operations here? 

We first `read` the value `x`, then we increase it before we `write` the increased value to `x`
```
Process 1: 
READ x    == x.getValue();  (R1)
INCREASE
WRITE x   == x.setValue();  (W1)

==> x.setValue( x.getValue() + 1);

Process 2: 
READ x    == x.getValue();  (R2)
DECREASE
WRITE x   == x.setValue();  (W2)

All in all we have 4 atomic operations: R1, W1, R2, W2
```

The program order is as follows: R1 happens before W1 and R2 happens before W2. 
When we have interleaving, we get six possible options for the result: 

``` 
0     -1    1     -1    1     0
R1    R1    R1    R2    R2    R2
W1    R2    R2    R1    R1    W2
R2    W1    W2    W1    W2    R1
W2    W2    W1    W2    W1    W1
```

Only two of the interleavings are optional/viable, and those are the ones which gives the result 0. But the four others 
yields a result which gives the wrong answer. 

---

We can view the execution of a concurrent program as an interleaving of the atomic actions executed by individual 
processes / threads. When processes interact, not all interleavings are likely to be acceptable. The role of 
synchronization is to prevent undesirable interleavings and this is done by combining _fine-grained_ atomic actions into 
_coarse-grained_ atomic actions.

> Fine-grained atomic actions are implemented directly by the hardware on which a concurrent program executes. 

> Coarse-grained atomic actions are compositions of several fine-grained atomic actions.

Using synchronization as a way to prevent undesired interleavings, is also possible by delaying the execution of a 
process until the program state satisfies some predicate (some _boolean_ condition). 

> Mutual exclusion is when we combine fined-grained atomic actions into composite coarse-grained atomic actions.

> Conditioned synchronization is when we delay the execution of a process until the program state satisfies some 
> predicate.

_**An atomic action makes an indivisible state transformation.**_ This means that any intermediate state that might 
exist in the implementation of the action, must not be visible to other processes. In a sequential program, assignment 
statements appear to be atomic because no intermediate state is visible to the program. However, this is not generally 
the case in concurrent programs because an assignment statement might be implemented by a sequence of fine-grained 
machine instructions. 

---

### Summary of Assumptions

In what follows, we'll assume that the machines on which we execute our concurrent programs have the following realistic 
characteristics:

1. Read and write values that fit into a word are atomic. The values of the basic types, such as integers, are stored in 
memory elements. They're read and written as atomic actions.
2. Values are manipulated in registers. Values are manipulated by loading them into registers, operating on them there, 
and then storing the results back into memory.
3. Registers are local to processes. Each process has its own set of registers. If the same processes changes from one 
process to another, we have so-called _context switch_. This is realized either by having distinct sets of registers, or 
by saving and restoring register values whenever a different process is executed. 

Then, any intermediate results that occur when a complex expression is evaluated are stored in registers or in memory 
which private to the execution process, i.e. intermediate results, "temporaries", are local.

With this machine model, if an expression in one process does not reference a variable which is modified by another 
process, then the expression evaluation will appear to be atomic (by the _at-most-once_ property) even if it requires 
executing several fine-grained atomic actions. 

There are two reasons for this: 
1. None of the values on which our expression depends, could possibly change while this expression is being evaluated. 
2. No other process can see any temporary values that might be created while the expression is being evaluated. 

Similarly, if an assignment statement in one process, does not reference any variable that is modified by another 
process, then the execution of the assignment will appear to be atomic. This happens, f.ex. when the value that we try 
to assign to a variable only references local variables. 

Unfortunately, most statements in concurrent programs that references shared variables do not meet the both of the 
requirements above. However, a weaker requirement (_at-most-once_ property) is often met. 

---

### At-Most-Once Property
Consider the assignment statement `x = e`, where we assign the value `e` to the variable `x`. 

Let us introduce the notion of a _critical reference_. A _critical reference_ is a reference to a variable written to by
another process.

If an expression `e` has no critical references, then `e` appears to be atomic. So, `x = e` satisfies the at-most-once 
property, if `e` contains at-most-one critical reference, and `x` is not read by another process. `x = e` will also 
satisfy the at-most-once property if `e` contains NO critical references. 

It is called the at-most-once property because there can be at most one shared variable, and it can be referenced at 
most one time. 

Examples: 
```
int x = 0;
int y = 0;

1. co x = x+1 || y = y+1 oc 
2. co x = y+1 || y = y+1 oc
3. co x = y+1 || y = x+1 oc
```
1. In the first example, there are no critical references because `x` is only being read and written by the first process, 
and `y` is only being read and written by the second process. So both processes satisfy the at-most-once property. 

2. In the second example, `y` is being read and written to `x` by the first process, while `y`is being read and written 
to`y` by the second process. So `y` is the critical reference because it's being written to by the second process and 
read by the first, but there's only that one critical reference. Thus, both processes satisfies the at-most-once 
property, since they both contain at most one critical reference. 

3. In the third example, the first process is reading the variable `y` and writing it to the variable `x`, and the 
second process is reading the variable `x` and writing it to the variable `y`. Neither processes satisfy the at-most-
once property because we refer to `y` in the first process `x=y+1` and `y` is assigned a value in the second process.
And we refer to `x` in the second process `y=x+1` and `x` is assigned a value in the first process. 

If an expression or assignment does not satisfy the at-most-once property, it often must be arranged to be executed 
atomically. So we can use synchronization to create a coarse-grained atomic action.

---

## Simple Programming Language Await (continuing)
Let's now have a look at the constructions that our `await` language has. 

```
for [i=0 to n-1] { a[i] = 0; }
co [i=0 to n] { a[i] = 0; }
process foo { ... }
process bar { [i=1 to n] { write(i); }
<await(B) S;>
<S>
<await(B)>
```
In the first line,

`for [i=0 to n-1] { a[i] = 0; }`

we have a `for` statement with one several quantifier `i=0 to n-1`, which introduces a new index 
variable, gives its initial value, and specifies the range of value in the index variable. The brackets are used around 
the quantifiers to indicate that there is a range of values as in an array declaration.

Apart, from the `co` `oc` statements, there is a second form of the `co`-statement that uses one or more quantifiers as 
a shorthand way to express that a set of statements is to be executed in parallel for every combination of values for 
the quantifier variables.

The second line, 

`co [i=0 to n] { a[i] = 0; }`

creates n+1 processes, one for each value of i. The scope of the quantifier variable is the process declaration and each 
process has a unique value of i.


The third line, 

`process foo{ ... }`

is the process declaration, which essentially is an abbreviation of the co-statement, with one arm and/or one 
quantifier.


The fourth line, 

`process bar[i=1 to n]{ write(i); }`

is declaring an array of processes by appending the quantifier `[i=1 to n]` to the name of the process `bar`. The order 
in which th process writes values, is non-deterministic. 

This means that the path of execution isn't fully determined by the specification of the computation, so the same input 
can produce different outputs. This is because `bar` is an array of n distinct processes and processes execute in an 
arbitrary order.  

