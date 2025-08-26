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

## Question 5: 
Explain briefly the main ideas of Remote Procedure Calls. 

### Answer: 
Remote Procedure Call (RPC) allows a program to execute code on another computer as if it were a local procedure call. 
It enables distributed computing by allowing processes to request services from programs located in other computers over
a network without the need for the calling program to understand the network details.

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
        singal_all(cv);
    }
    procedure withdraw(int amount) { 
        while(amount > balance) { wait(cv); }
        balance = balance - amount; 
    }
}
```

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

## Question 7 - Multicountry
Consider Multi-country, which is a country, and Multi-city, which is its capital city.

The airport of Multi-city has introduced strict entry requirements for arriving passengers because of a global pandemic.
Some of the passengers are vaccinated, while others are not vaccinated. All passengers arriving to Multi-city must go
through the document/passport control, where their vaccination certificates are checked by border guard officers.

Upon arriving to Multi-city Airport, passengers, both vaccinated and un-vaccinated, are mingling in the Mingling Zone,
and they are walking towards to the Documents Checking Zone. There, the border guard checks their vaccination
certificates, imposes quarantine on the un-vaccinated ones, and in any case lets all the passengers into the city.

Assume that the passengers enter the Documents Checking Zone in a random order. The only requirement is that there must
be never un-vaccinated and vaccinated passengers in the Documents Checking Zone at the same time. However, people with
the same vaccination status are allowed in the Documents Checking Zone at the same time (that is, at any given moment of
time, either all passengers in the Documents Checking Zone are vaccinated, or all passengers in the Documents Checking
Zone are un-vaccinated).

Assume that there are `N` vaccinated passengers, and `M` un-vaccinated passengers who have just landed at Multi-city
Airport.

Your task is to simulate the described situation in the AWAIT language.
👉 Represent passengers as processes.
👉 Use semaphores for synchronization.
👉 Make sure that your solution avoids deadlock.
💡 Your solution need NOT be fair.

### Answer: 
```java
sem dock_zone = 0;
sem unVaxMutex = 0;
sem vaxMutex = 0;
int unVaxCounter = 0;
int vaxCounter = 0; 

process V(){
    while(true) {
        walk(); // non-critical section (walk to dock zone) 

        // entry-protocol
        P(vaxMutex);
        vaxCounter = vaxCounter + 1;
        if (vaxCounter == 1) {
            P(dock_zone);
        }
        V(vaxMutex);

        // critical section
        check_doccuments();

        // exit-protocol 
        P(vaxMutex);
        vaxCounter = vaxCounter - 1;
        if (vaxCounter == 0) {
            V(dock_zone);
        }
        V(vaxMutex);
    }
}

process U(){
    while(true) {
        walk(); // non-critical section (walk to dock zone) 

        // entry-protocol
        P(unVaxMutex);
        unVaxCounter = unVaxCounter + 1;
        if (unVaxCounter == 1) {
            P(dock_zone);
        }
        V(unVaxMutex);

        // critical section
        check_doccuments();

        // exit-protocol 
        P(unVaxMutex);
        unVaxCounter = unVaxCounter - 1;
        if (unVaxCounter == 0) {
            V(dock_zone);
        }
        V(unVaxMutex);
    }
}
```

## Question 8 - Gløgg
Here is an exercise of sudo-code in _Await Language_ with semaphores for synchronization:

- Three persons, who like gløgg very much, have gathered to play the following game in a bar. To drink a portion, each
  of them obviously needs three ingredients: the gløgg, a mug, and almonds.
- One player has the gløgg, the second has mugs, and the third has the almonds. Assume each of the players has an
  unlimited supply of their ingredients, respectively.
- The barista, who also has an unlimited supply of the ingredients, puts two random ingredients on the table.
- The player who has the third ingredient picks up the other two, makes the drink, and then drinks it.
- The barista waits for the player to finish.
- This "cycle" then repeats.

"Simulate" this behaviour in the AWAIT language. Represent the players and the barista as processes. Use semaphores for
synchronization. Make sure that your solution avoids deadlock.

### Answer:
```java
sem barista;
sem gløgg;
sem mug;
sem almonds;

process Barista(){
  while(true){
    wait();     // non-critical section
    P(barista); // entry-protocol
    int missing_ingredient = randomInt(0..2); // critical section (0=gløgg, 1=mug, 2=almonds)

    // exit-protocol 
    if (missing_ingredient == 0){ V(gløgg); }
    if (missing_ingredient == 1){ V(mug); }
    if (missing_ingredient == 2){ V(almonds); }
  }
}

process P1(){
  while(true){
    wait();   // non-critical section
    P(gløgg); // entry-protocol

    // critical section
    make_gløgg();
    drink_gløgg();

    V(barista); // exit-protocol
  }
}
process P2(){
  while(true){
    wait(); // non-critical section
    P(mug); // entry-protocol

    // critical section
    make_gløgg();
    drink_gløgg();

    V(barista); // exit-protocol
  }
}
process P3(){
  while(true){
    wait();     // non-critical section
    P(almonds); // entry-protocol

    // critical section
    make_gløgg();
    drink_gløgg();

    V(barista); // exit-protocol
  }
}
```