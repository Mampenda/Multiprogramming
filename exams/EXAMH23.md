# INF214 EXAM H23
## Question 1:
Consider the following program:
```
int x = 1;
int y = 3;
co < x = x * y; > || x = x + y; oc
```
Does the program meet the requirements of the At-Most-Once-Property? Explain
your answer. What are the possible final values of x and y? Explain your answer.
### Answer: 
The first statement in the `co oc`-block is an atomic action (surrounded by angle brackets `<>`), but the second is not.

This does _not_ meet the requirements At-Most-Once property because both processes has a critical reference `e` and both
of the `x`'s are read by the other process. 

For a program to meet the requirements of the At-Most-Once property a process can either have a critical reference `e`, 
but then `x` can't be read by another process, or if there is no critical reference, `x` can be read by other processes.
 
The possible final values for `x` and `y` are: 
1. `x = x*y` executes first => `x` = 6, `y` = 3
2. `x = x+y` executes first => `x` = 12, `y` = 3
3. The non-atomic process reads `x = 1`, then the atomic process finishes executing, resulting in 
   `x = 3`, but then the non-atomic process finishes last, making `x = 4`, resulting in`x = 4, y = 3`.

## Question 2: 
Consider the following program written in the AWAIT language:
```
bool should_continue = true;
bool can_proceed = false;
co
    while(should_continue){
        can_proceed = true;
        can_proceed = false;
    }
||
    <await(can_proceed) should_continue = false;>
oc
```

Which of the following statements hold for this program?
1. If the scheduling policy is strongly fair, this program will eventually terminate.
2. If the scheduling policy is strongly fair, this program will never terminate. 
3. If the scheduling policy is weakly fair, this program might not terminate.
4. If the scheduling policy is weakly fair, this program will never terminate.
### Answer: 
The correct statements are `1` and `3`.

## Question 3: 
Describe the difference between synchronous and asynchronous message passing.
### Answer: 
1. `Synchronous` message passing requires the sender and receiver to both be active and _wait_ for each other during
   communication. The sender waits (blocks) until the receiver acknowledges the message, ensuring that both parties are
   synchronized.

2. `Asynchronous` message passing, on the other hand, allows the sender to send a message without waiting for the 
   receiver to be ready or acknowledge it. The message is often _stored in a queue_, allowing the sender to continue its 
   process independently of the receiver’s state or readiness.

## Question 4:
Three persons P1, P2, and P3 were invited by their friend F to make some sandwiches (made of bread, eggs, and tomato).
To make a sandwich, three ingredients are needed: a slice of bread, a slice of tomato, and a slice of an egg.

Each of these persons P1, P2, P3 has only one type of each of the ingredients:
person P1 has slices of bread.
person P2 has slices of tomato.
person P3 has slices of egg.
We assume that persons P1, P2, and P3 each has an unlimited supply of these ingredients (i.e., slices of bread, slices 
of tomato, slices of egg), respectively. Their friend F, who invited them, also has an unlimited supply of all the 
ingredients.

Here is what happens: the host F puts two random ingredients on the table. Then the invited person who has the third 
ingredient picks up these other two ingredients, makes the sandwich, and then eats it. The host of the party F waits for
that person to finish. This "cycle" of is then infinitely repeated.

Write code in the AWAIT language that simulates this situation.
Represent the persons P1, P2, P3, F as processes.

You must use SPLIT BINARY SEMAPHORE for synchronization. Make sure that your solution avoids deadlock.
EXPLAIN very briefly the advantages of using the split binary semaphore.

### Answer: 
```java
sem bread;
sem tomato;
sem egg; 
sem host; 

process F(){
    while(true){
        wait();  // non-critical section
        P(host); // entry-protocol (the host puts two ingredients on the table)
       
        // critical section (the missing ingredient is either 0-bread, 1-tomato, or 2-egg)
        int missing_ingredient = randInt(0,2);
        
        // exit-protocol
        if(missing_ingredient == 0){ V(bread); }
        if(missing_ingredient == 1){ V(tomato); }
        if(missing_ingredient == 2){ V(egg); }
    }
}

process P1(){
    while(true){
       wait();   // non-critical section
       P(bread); // entry-protocol
       
       // critical section
       make_sandwich(); 
       eat_sandwich();
       
       V(host); // exit-protocol
    }
}

process P2(){
   while(true){
      wait();    // non-critical section
      P(tomato); // entry-protocol

      // critical section
      make_sandwich();
      eat_sandwich();

      V(host); // exit-protocol
   }
}

process P3(){
   while(true){
      wait();  // non-critical section
      P(egg);  // entry-protocol

      // critical section
      make_sandwich();
      eat_sandwich();

      V(host); // exit-protocol
   }
}
```

This code uses split binary semaphores, where multiple semaphores (in this case, bread, tomato, and egg) to control 
access to specific resources based on the available "ingredient" on the table. Each process (P1, P2, and P3) waits on a 
unique semaphore (bread, tomato, or egg) to proceed with making and eating a sandwich.

## Question 5: 
Consider the following variant of the Readers/Writers problem: 
Reader processes query a database and writer processes examine and modify it. Readers may access the database 
concurrently, but writers require exclusive access. Although the database is shared, we cannot encapsulate it by a 
monitor, because readers could not then access it concurrently since all code within a monitor executes with mutual 
exclusion. 

Instead, we use a monitor merely to arbitrate access to the database. The database itself is global to the readers and 
writers. The arbitration monitor grants permission to access the database. To do so, it requires that processes inform 
it when they want access and when they have finished. There are two kinds of processes and two actions per process, so 
the monitor has four procedures: `request_read`, `request_write`, `release_read`, `release_write`. These procedures are 
used in the obvious ways.

For example, a reader calls `request_read` before reading the database and calls `release_read` after reading the 
database. To synchronize access to the database, we need to record how many processes are reading and how many processes 
are writing. 

In the implementation below, `nr` is the number of readers, and `nw` is the number of writers; both of them are 
initially 0. Each variable is incremented in the appropriate request procedure and decremented in the appropriate 
release procedure. A software developer has started on the implementation of this monitor. Your task is to fill
in the missing parts. Your solution does not need to arbitrate between readers and writers.

### Answer: 
```java
monitor RW_Controller() {
    int nr = 0; // number of readers (do not need exclusive access)
    int nw = 0; // number of writers (does need exclusive access) 
    cond OK_to_write;   // signalled when nr == 0 and nw == 0
    cond OK_to_read;    // signalled when nw == 0
}

// Reader's enter protocol
procedure request_read() {
   while (nw > 0) { wait(OK_to_read); } // entry-protocol (readers should wait if there's an active writer)
   nr = nr + 1;                         // critical section (increment number of readers)
}

// Reader's exit protocol
procedure release_read() {

   // critical section (decrement number of readers)
   nr = nr - 1;

   // exit-protocol (if there's no more readers, signal to other writers that it's OK to write)
   if (nr == 0){
      signal(OK_to_write);
   }
}

// Writer's enter protocol
procedure request_write() {
    // entry-protocol (writers must wait until there are no other readers/writers accessing the database)
    while (nr > 0 || nw > 0) { wait(OK_to_write); }
    
    // critical section (increment number of writers)
    nw = nw + 1;
}

// Writer's exit protocol
procedure release_write() {

   // critical section (decrement number of writers)
   nw = nw - 1;

   // exit-protocol (signal readers that it's OK to read)
   signal_all(OK_to_read);
}
```

## Question 6: 
A savings account is shared by several people (processes). Each person may deposit or withdraw funds from the account. 
The current balance in the account is the sum of all deposits to date minus the sum of all withdrawals to date. The 
balance must never become negative. A deposit never has to delay (except for mutual exclusion), but a withdrawal has to 
wait until there are sufficient funds.

A software developer was asked to implement a monitor to solve this problem, using Signal-and-Continue discipline. 
Below is the code the developer has written so far. Help the developer to finish the implementation.
```java
monitor Account(){
    int balance = 0;
    cond cv; // condition variable
    
    procedure Deposit(int amount){
        balance = balance + amount;// critical section (increase balance)
        signal_all(cv);            // exit-protocol: signal to all other processes that the condition is fulfilled
   }
    
    procedure Withdraw(int amount){
        while(amount > balance){ wait(cv); }// entry-protocol: while withdraw-amount is larger than balance, wait for cv
        balance = balance - amount;         // critical section (decrease balance)
    }
}
```

## Question 7:
Using Modern CSP, specify behaviour of a traffic light that repeatedly turns green, then yellow, and
then red.

### Answer:
`TrafficLight = Green -> Yellow -> Red -> TrafficLight`
In this specification, the traffic light repeatedly goes through the sequence Green -> Yellow -> Red in a loop, 
representing a continuous cycle.

## Question 8: 
What will be printed when the following JavaScript code is executed?
```java
function* foo(x) {
   var y = x * (yield false);
   return y;
}

var it = foo(100);
var res = it.next(2);

console.log(res.value); // what will be printed here?
res = it.next(3);
console.log(res.value); // what will be printed here?
```

### Answer: 
The generator function `foo` takes an input x, pause after yielding false, and then use a second value passed to it to 
complete a calculation.
```java
function* foo(x) {
   var y = x * (yield false);  // The 'yield false' pauses the generator and returns `false`
   return y;                   // After resuming, calculates and returns y
}

var it = foo(100);            // Creates a generator instance `it`
var res = it.next(2);         // Starts the generator; `2` is ignored because the generators in JavaScript runs until their first yield statement

console.log(res.value);       // Prints `false` because 'yield false' pauses and returns 'false'
res = it.next(3);             // Resumes after generator yielding 'false' and computes y = 100 * 3 = 300
console.log(res.value);       // Prints `300` because `y` is returned from the generator
```

## Question 9: 
```javascript
var a = promisify({});
var b = a.onResolve(x => x + 1);
var c = a.onResolve(y => y - 1);
a.resolve(100);
```
Consider the JavaScript code. Note the syntax here is a blend of JavaScript and lambda, which uses:

      promisify to create a promise,
      onResolve to register a resolve reaction

Draw a promise graph for this code. Remember to use the names of nodes in that graph that represent the "type" of node:

      v for value
      f for function
      p for promise

with a subscript that represents the line number where that particular value/function/promise has been declared/where it
appears first. For example, the value 100 on line 4 will be denoted by v in the promise graph.

### Answer: 
Step-by-Step breakdown of the code:
```javascript
var a = promisify({});              // new promise "a" which we call P_1
var b = a.onResolve(x => x+1);      // new resolve handler "b" for P_1 which we call f_2
var c = a.onReject(x => x + 1);     // new rejection handler "c" for P_1 which we call f_3
a.resolve(100);                     // resolves P_1 with the value V_5 = 100
```
Promise graph: 
```scss
(V_1/100) --Resolve--> (P_1/a) --Link--> (f_2/b) --Resolve--> (V_2/101) ----> (f_3/c) --Resolve--> (V_3/99)
```