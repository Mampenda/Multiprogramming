# EXAM QUESTION

## Question 1: 
Describe the Critical Section Problem. What requirements doe we have for entry and exit protocol? 

### Answer: 
The critical section problem revolves around when two (or more) processes/threads are trying to access the same global 
variable, to read/update. This can cause deadlocks, livelocks, intermediate leavings, faulty data, etc. because the 
processes/threads cannot move on with their programs once inside a deadlock, or use wrongful information when continuing
with their program. 

The requirements for entry-/exit protocols are
1. Mutual exclusion
2. Absence of deadlock/livelock and unnecessary delay 
3. Eventual entry.

## Question 2:
What is the purpose of `synchronized` in `Java`? Why would one use the method `tryLock()` of class `ReentrantLock` in
Java? What are the advantages (or disadvantages) of using atomic variables (e.g. `AtomicInteger`)?

### Answer: 
The purpose of `synchronized` in `Java` is to assure mutual exclusion for threads/processes trying to access the same 
critical section. One would use `tryLock()` of class `ReentrantLock` in Java to try and lock the critical section 
without stopping the process/thread completely. If a process/thread is trying and failing to gain access, it can still 
continue its program. 

The advantages for `AtomicInteger` are:
1. Lock-free, thread-safe operations for single variables
2. Allow fine-grained control for simple atomic actions. Is safer and more efficient than synchronized blocks.
The disadvantages for `AtomicInteger` are: 
1. Not suitable for more complex operations or multi-variable atomicity. 
2. Lead to more complex code as they don't provide comprehensive atomic control.

## Question 3: 
Describe the difference between synchronous and asynchronous message passing. 

### Answer: 
Synchronous message passing requires the sender/receiver to stop and wait for confirmation that the message has been 
successfully sent/received, while asynchronous message passing does not. In asynchronous message passing, the sender 
can send a message and continue its program without having to wait for a conformation from the receiver. 

## Question 4: 
Explain the main ideas, and write about similarities/differences, between actors and channels. 

### Answer:
Actors and channels are both models for managing concurrency and communication in distributed or parallel systems, but 
approach they it differently.

**Actors**
- An actor is a self-contained, independent entity that process messages. 
- They have their own state and communicate asynchronously by sending messages to other actors' mailboxes. 
- Each actor can create more actors, send messages, and decide how to handle received messages. 

**Channels** 
- A channel is a communication pathway that connects multiple processes or threads. 
- Channels often don't maintain state but serve as conduits for data. 
- Channels facilitates message-passing directly between sender and receiver, which can be synchronous or asynchronous. 

## Question 6 - Savings Account:
A savings account is shared by several people (processes). Each person may deposit or withdraw funds from the account.
The current balance in the account is the sum of all deposits to date minus the sum of all withdrawals to date. The
balance must never become negative. A deposit never has to delay (except for mutual exclusion), but a withdrawal has to
wait until there are sufficient funds. A junior software developer was asked to implement a monitor to solve this
problem, using Signal-and-Continue discipline. Here is the code the junior developer has written so far:
```java
monitor Account(){
    int balance = 0;
    cond cv;
    
    procedure deposit(int amount) { balance = balance + amount; }
    procedure withdraw(int amount) { balance = balance - amount; }
}
```
This solution is incorrect. Help the junior developer implement the monitor correctly.

### Answer: 
```java
monitor Account(){
    int balance = 0;
    cond cv;
    
    procedure deposit(int amount) {
        balance = balance + amount; 
        signal(cv);
    }
    procedure withdraw(int amount) { 
        wait(cv);
        balance = balance - amount; 
    }
}
```