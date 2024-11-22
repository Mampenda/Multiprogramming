# INF214 EXAM H21
## Question 1:
Describe the Critical Section Problem. What requirements do we have for the entry and exit protocols?
### Answer:
The Critical Section Problem revolves around when a program has multiple processes accessing/modifying/ the same shared 
resource. The problem occurs when one process is accessing/modifying the resource, another might try to access/modify it
too.

The requirement for the entry/exit protocols is that they satisfy the following 4 properties: 
1. Mutual exclusion                 - at most one process at a time is executing its critical section
2. Absence of deadlock / livelock   - if two or more processes are trying to enter their CS, at least one will succeed
3. Absence of unnecessary delay     - if a process is trying to enter their CS others are executing their non-CS or have
                                      terminated, the first process is not prevented from entering its critical section
4. Eventual entry                   - a process that is attempting to enter its critical section will eventually succeed

## Question 2: 
What is the purpose of `synchronized` in `Java`? Why would one use the method `tryLock()` of class `ReentrantLock` in 
Java? What are the advantages (or disadvantages) of using atomic variables (e.g.,`AtomicInteger`)?
### Answer: 
1. The `synchronized` in `Java` is used to ensure mutual exclusion, preventing multiple threads from executing a 
   critical section of code simultaneously, ensuring thread-safe access to shared resources.
2. The purpose of `tryLock()` in the `ReentrantLock()`-class in `Java` is used to attempt to acquire a lock without 
   blocking. It returns true if the lock is successfully acquired; otherwise, it returns false, allowing the thread to 
   continue executing alternative code instead of waiting indefinitely for the lock. 
   This is helpful in avoiding deadlock or allowing non-blocking access.
3. The advantages of Atomic Variables (e.g., `AtomicInteger`):
   - They provide lock-free, thread-safe operations for single variables, improving performance by avoiding the overhead
     of locks. 
   - They allow fine-grained control for simple atomic operations, which is faster and more efficient than using 
     synchronized blocks.
4. The disadvantages of Atomic Variables (e.g. `AtomicInteger`):
   - They're limited to basic atomic operations, so they are unsuitable for more complex operations or multi-variable 
     atomicity. 
   - They can lead to more complex code when coordinating atomic updates across multiple variables, as they don’t 
     provide comprehensive atomic control like locks do.

## Question 3: 
Describe the difference between synchronous and asynchronous message passing.
### Answer: 
1. Synchronous message passing requires the sender and receiver to both be active and wait for each other during 
communication. The sender waits (blocks) until the receiver acknowledges the message, ensuring that both parties are 
synchronized.

2. Asynchronous message passing, on the other hand, allows the sender to send a message without waiting for the receiver 
to be ready or acknowledge it. The message is often stored in a queue, allowing the sender to continue its process 
independently of the receiver’s state or readiness.

## Question 4: 
Explain the main ideas, and write about similarities/differences, between actors and channels.
### Answer: 
Actors and channels are both models for managing concurrency and communication in distributed or parallel systems, but 
they approach it differently.

_Main Ideas_

**Actors**:
- An actor is a self-contained, independent entity that processes messages. 
- Actors have their own state and communicate asynchronously by sending messages to other actors’ mailboxes. 
- Each actor can create more actors, send messages, and decide how to handle received messages.

**Channels**:
- A channel is a communication pathway that connects multiple processes or threads. 
- Channels often don’t maintain state but serve as conduits for data.
- Channels facilitate message-passing directly between senders and receivers, which can be synchronous (blocking) or 
  asynchronous (non-blocking). 

_Similarities_:
    
    Both support message-passing concurrency, and both and decouple the sender and receiver, allowing them to operate 
    independently.

_Differences_:

    Responsibility: Actors can make decisions based on messages; channels merely transmit data between endpoints.
    State: Actors encapsulate their own state; channels are stateless.
    Communication: Actors use asynchronous messaging, while channels can support both synchronous and asynchronous 
                   modes.

## Question 5: 
Explain briefly the main ideas of RPC (Remote Procedure Calls).
### Answer: 
Remote Procedure Calls (RPC) allow a program to invoke a procedure (or function) located on a different machine as if it
were local. This abstraction simplifies distributed computing by hiding the complexities of network communication, 
enabling the following main ideas:

- Transparency: RPC makes remote calls appear like local function calls, abstracting away details such as network 
  communication, data encoding, and server interaction. 

- Client-Server Model: In RPC, the client sends a request to the server to execute a specific function, and the server 
  returns the result. The client doesn’t need to know about the server's underlying code or location. 

- Stubs and Serialization: RPC uses stubs (client and server proxies) to package the request, serialize data, send it 
  over the network, and handle responses. This allows complex data structures to be transmitted across different 
  environments. 

- Synchronous and Asynchronous Calls: Typically, RPC is synchronous (the client waits for the server’s response), but 
  some implementations support asynchronous calls, allowing the client to continue without waiting.

RPC is widely used in distributed systems for its simplicity and transparency, allowing services to interact across 
networks as if they were local procedures.

## Question 6: 
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
cond cv;  // Condition variable to wait when there are insufficient funds

    procedure deposit(int amount) {
        balance = balance + amount;  // Add amount to the balance
        cv.signal();  // Wake up a waiting withdrawal (if any) since funds are available
    }

    procedure withdraw(int amount) {
        // Wait until there are sufficient funds for the withdrawal
        while (balance < amount) {
            cv.wait();  // Wait for funds to become available
        }
        balance = balance - amount;  // Perform the withdrawal when funds are sufficient
    }
}
```

## Question 7: 
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
// Semaphores for synchronization
sem vaxMutex = 1;
sem unvaxMutex = 1;
sem doc_check_zone = 1;

// Counters to track the number of passengers currently in the Documents Checking Zone
int vaxCounter = 0;
int unvaxCounter = 0;

process vaccinated([i = 1 to N]){
    while(true) {
      walk_to_DCZ(); // non-critical section (Mingling Zone: passengers walk towards the Document Checking Zone)
      
      P(vaxMutex); // entry-protocol
        
      // CRITICAL SECTION
      vaxCounter++;  // Increment nr. of vaccinated passengers in the zone
      
      // If this is the first passenger in the zone, lock the zone for un-vaccinated passengers
      if (vaxCounter == 1) { P(doc_check_zone); }
      
      V(vaxMutex); // Release access to "vaxCounter"

      check_docs();

      // exit-protocol
      P(vaxMutex);   // Lock access to "vaxCounter" for synchronization
      vaxCounter--;  // Decrement nr. of vaccinated passengers in the zone

      // If this is the last passenger in the zone, release "doc_checking_zone"
      if (vaxCounter == 0) { V(doc_check_zone); }
      V(vaxMutex); // Release "vaxCounter"
    }
}

process unvaccinated([i = 1 to M]){
    while(true){
      walk_to_DCZ(); // non-critical section  (Mingling Zone: passengers walk towards the Document Checking Zone)
      
      P(unvaxMutex); //entry-protocol

      // CRITICAL SECTION
      unvaxCounter++; // Increment nr. of un-vaccinated passengers in the zone
      
      // If this is the first passenger in the zone, lock it for vaccinated passengers
      if (unvaxCounter == 1){ P(doc_check_zone); }
      V(unvaxMutex); // Release access to "unvaxMutex"
      
      check_docs(); 
      
      // exit-protocol
      P(unvaxMutex); // Acquire lock on "unvaxMutex" 
      unvaxCounter--; // Decrement nr. of un-vaccinated passengers in the zone
      
      // If this is the last passanger in the zone, release mutex for zone
      if (unvaxCounter == 0){ V(doc_check_zone); }
      V(unvaxMutex); // Release "unvaxMutex" 
    }
}
```

## Question 8: 
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
sem barista = 1; 
sem almonds = 0;
sem gløgg = 0;
sem mug = 0;

process Barista(){
    while(true){
        wait();     // non-critical section
        P(barista); // Entry protocol (put two ingredients on the table)
        
        // critical section (the random missing ingredient: 0=almonds, 1=gløgg, 2=mug)
        missingIngridient = randomInt(0,2);
        
        // exit-protocol
        if (missingIngredient == 0){ V(almonds); }
        if (missingIngredient == 1){ V(gløgg); }
        if (missingIngredient == 2){ V(mug); }
    }
}

process Player1(){
    while(true){
        wait();     // non-critical section
        P(almonds); // entry protocol 
        
        // critical section
        make_gløgg();
        drink_gløgg();
        
        // exit protocol
        V(barista);
    }
}

process Player2(){
    while(true){
        wait();     // non-critical section
        P(gløgg);   // entry protocol 

        // critical section
        make_gløgg();
        drink_gløgg();

        // exit protocol
        V(barista);
    }
}

process Player3(){
    while(true){
        wait();     // non-critical section
        P(mug);     // entry protocol 

        // critical section
        make_gløgg();
        drink_gløgg();

        // exit protocol
        V(barista);
    }
}
```
## Question 10: 
Using JavaScript, give an example of how `yield` can receive data when a co-routine is resumed.
(Hint 1:Start by declaring a generator. Hint 2: Think how many calls of `next` you need.)

### Answer: 
```javascript
// passing messages with yield / next
function *foo(x) {
  var y = x * (yield);
  return y;
}

var it = foo(6);
// the first invocation of `next` always just starts the generator and executes it until the first `yield` is encountered
it.next();

// the second invocation of `next` executes the suspended `yield` expression
var res = it.next(7);
console.log(res.value);

// the third `next` invocation would execute the second `yield` expression

// In general, the amount of `next` invocations is 1 more that the amount of `yield`s
```