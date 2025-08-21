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
The program does not meet the requirements of the At-Most-Once-Property because the two commands are both 
reading/updating the same critical reference `x`. Since only one of them are atomic, indicated by the angle brackets 
`<>`, there's a risk of interference, where the non-atomic command `x = x+y` will read the value of `x`, then the 
atomic command `< x = x*y >` will execute, before the non-atomic command continues its execution. 

The possible final values are: 
1. `< x = x * y; >` executes first, then `x = x + y;` executes: x = 1*3 = 3, then x = 3+3 = 6  => x = 6, y = 3. 
2. `x = x + y;` executes first, then `< x = x * y; >` executes: x = 1+3 = 4, then x = 4*3 = 12 => x = 12, y = 3
3. `x = x + y` reads `x`, then `< x = x * y; >` executes, then `x = x + y` finishes executing: 
   1. x = x+y = 1+3
   2. x = x*y = 1*3 = 3
   3. x = x+y = 1+3 = 4
   Resulting in x = 4, y = 3


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
The statements that hold for this program are:
(1) If the scheduling policy is strongly fair, this program will eventually terminate.
(3) If the scheduling policy is weakly fair, this program might not terminate.


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
- person P1 has slices of bread.
- person P2 has slices of tomato.
- person P3 has slices of egg.
- 
We assume that persons P1, P2, and P3 each has an unlimited supply of these ingredients (i.e., slices of bread, slices 
of tomato, slices of egg), respectively. Their friend F, who invited them, also has an unlimited supply of all the 
ingredients.

Here is what happens: 
- the host F puts two random ingredients on the table. 
- Then the invited person who has the third ingredient picks up the two ingredients, makes the sandwich, then eats it. 
- The host of the party F waits for that person to finish. 
- This "cycle" of is then infinitely repeated.

Write code in the AWAIT language that simulates this situation.
Represent the persons P1, P2, P3, F as processes.

You must use SPLIT BINARY SEMAPHORE for synchronization. Make sure that your solution avoids deadlock.
EXPLAIN very briefly the advantages of using the split binary semaphore.

### Answer: 
```java
sem tableMutex;
sem F;
sem bread;
sem tomato;
sem egg; 

process HostF(){
    wait(); // non-critical section
   
    // entry-protocol    
    P(F);
    P(tableMutex);
    
    // critical section (0 = bread, 1 = tomato, 2 = egg)
    int missing_ingredient = randomInt(0..2); 
    
    // exit-protocol
    if (missing_ingredient == 0){ V(bread); }
    if (missing_ingredient == 1){ V(tomato); }
    if (missing_ingredient == 2){ V(egg); }
    V(tableMutex);
}

process personA(){
   wait(); // non-critical section
   
   // entry-protocol
   P(bread);
   P(tableMutex);
   
   // critical section
   make_sandwich();
   eat_sandwich();
   
   // exit-protocol 
   V(tableMutex);
   V(F);
}

process personB(){
   wait(); // non-critical section

   // entry-protocol
   P(tomato);
   P(tableMutex);

   // critical section
   make_sandwich();
   eat_sandwich();

   // exit-protocol 
   V(tableMutex);
   V(F);
}

process personC(){
   wait(); // non-critical section

   // entry-protocol
   P(egg);
   P(tableMutex);

   // critical section
   make_sandwich();
   eat_sandwich();

   // exit-protocol 
   V(tableMutex);
   V(F);
}
```

This solution uses binary split semaphores where each process waits for their own semaphore in addition to the common
mutex for the table. The advantages of this is that each process waits for its own semaphore before accessing the
critical section, minimizing the chance of two processes/threads accessing the critical section at the same time. 


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
monitor RW_Controller(){
    int nr = 0;
    int nw = 0;
    cond OK_to_read;
    cond OK_to_write;
    
    request_read{
        while(nw > 0){ wait(OK_to_read); }
        nr = nr + 1;
   }
   
   release_read{
       nr = nr - 1;
       if(nr == 0){ signal(OK_to_write); }
   }
        
   request_write{
       while(nr > 0 || nw > 0){ wait(OK_to_write); }
       nw = nw + 1; 
   }
   
   release_write{
      nw = nw - 1;
      signal(OK_to_write);
      signal_all(OK_to_read);
   }
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
    cond cv; 
    
    procedure deposit(int amount){
        balance = balance + amount; 
        signal_all(cv);
   }
    
    procedure withdrawl(int amount){
        if(amount > balance){ wait(cv); }
        balance = balance - amount; 
   }
}
```

## Question 7:
Using Modern CSP, specify behaviour of a traffic light that repeatedly turns green, then yellow, and
then red.

### Answer: 
```
TrafficLight --> Green --> Yellow --> Red --> TrafficLight
```

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
var res = it.next(2);         // `2` is ignored because the generators in JavaScript runs until their first yield statement

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
```javascript
var a = promisify({});           // 1 - new promise (P1/a)
var b = a.onResolve(x => x + 1); // 2 - resolve handler (f2/b/x => x+1) for (P1/a)
var c = a.onResolve(y => y - 1); // 3 - resolve handler (f3/c/y => y-1) for (P1/a)
a.resolve(100);                  // 4 - root (V4/100) that resolves (P1/a)
```
```
(V4/100) --Resolve--> (P1/a) --Resolve--> (f2/b/x => x+1) --> (V2/101) --Resolve--> (P2/b)
                        |
                        v
                        + ----Resolve--> (f3/c/y => y-1) --> (V3/99) ----Resolve--> (P3/c) 
```

## Question 10 (use the one from H22/V24 since H23 does not show)
Consider the following HTML/JavaScript attached in the PDF file to this question. 
This code runs on a computer of a super-user, who clicks the button `myButton` 6 ms after the execution starts. 

What happens at particular time points?
```html

<button id="myButton"></button>
<script>
    setTimeout(function timeoutHandler() {
        /* code that runs for 6 ms*/
    }, 10);

    setInterval(function intervalHandler() {
        /* code that runs for 8 ms */
    }, 10);

    const myButton = document.getElementById("myButton");
    
    myButton.addEventListener("click", function clickHandler() {
        Promise.resolve().then(() => { /* some promise handling code that runs for 4 ms */ });
        /* click-handling code that runs for 10 ms */
    });
    /* code that runs for 18 ms */ 
</script>
```

| What happens...                    | ...at what time? |
|------------------------------------|------------------|
| `clickHandler` finishes            | at X ms          |
| `clickHandler` starts              | at X ms          |
| interval fires for the first time  | at X ms          |
| interval fires for the second time | at X ms          |
| interval fires for the third time  | at X ms          |
| interval fires for the fourth time | at X ms          |
| `intervalHandler` starts           | at X ms          |
| `intervalHandler` finishes         | at X ms          |
| mainline execution starts          | at 0 ms          |
| mainline execution finishes        | at X ms          |
| promise handler starts             | at X ms          |
| promise handler finishes           | at X ms          |
| promise resolved a tiny bit after  | at X ms          |
| `timeoutHandler` starts            | at X ms          |
| `timeoutHandler` finishes          | at X ms          |
| timer fires                        | at X ms          |
| user clicks button                 | at 6 ms          |


### Answer:

| What happens...                    | ...at what time? |
|------------------------------------|------------------|
| `clickHandler` finishes            | at 28 ms         |
| `clickHandler` starts              | at 18 ms         |
| interval fires for the first time  | at 10 ms         |
| interval fires for the second time | at 20 ms         |
| interval fires for the third time  | at 30 ms         |
| interval fires for the fourth time | at 40 ms         |
| `intervalHandler` starts           | at 38 ms         |
| `intervalHandler` finishes         | at 46 ms         |
| mainline execution starts          | at 0 ms          |
| mainline execution finishes        | at 18 ms         |
| promise handler starts             | at 28 ms         |
| promise handler finishes           | at 32 ms         |
| promise resolved a tiny bit after  | at 18 ms         |
| `timeoutHandler` starts            | at 32 ms         |
| `timeoutHandler` finishes          | at 38 ms         |
| timer fires                        | at 10 ms         |
| user clicks button                 | at 6 ms          |