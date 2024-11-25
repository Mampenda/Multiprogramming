# INF214 EXAM H23
## Question 1:
Consider the following program:
```
int x = 1;
int y = 4;
co < x = x * y; > || x = y - x; oc
```
Does the program meet the requirements of the At-Most-Once-Property? Explain
your answer. What are the possible final values of `x` and `y`? Explain your answer.

### Answer:
This program does not meet the requirements of the At-Most-Once-Property because:
These statements are both reading and updating `x` (critical reference), and only one of them is atomic, indicated by 
the angle brackets `<>`. This means that the non-atomic statement `x = y - x;` could be interrupted while 
reading/ updating `x` leading to interference. 

The possible final values for `x` and `y` are: 
1. `<x = x * y;>` executes first, then `x = y - x;` executes: x = 1*4 = 4 => x = 4-4 = 0,  y = 4.
2. `x = y - x;` executes first, then `<x = x * y;>` executes: x = 4-1 = 3 => x = 3*4 = 12, y = 4.
3. `x = y - x;` reads `x`, `<x = x * y;>` executes, then `x = y - x;` finishes its execution:
   1. x = y-x = 4-1
   2. x = x*y = 1*4 = 4
   3. x = 4-1 = 3 ======> Resulting in x = 3, y = 4


## Question 2:
Consider the following program written in the AWAIT language:
```
bool can_proceed = false;
bool should_continue = true;

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
The statements `1` and `3` holds for this program.
(1) If the scheduling policy is strongly fair, this program will eventually terminate.
(3) If the scheduling policy is weakly fair, this program might not terminate.


## Question 3:
Summarize very briefly the purpose of barriers. 

### Answer:
Barrier synchronisation is used to make sure that some processes won´t race to far ahead of other processes. We can 
specify that all processes should wait at a barrier until some criteria is fulfilled. 
Example, some process might do work/calculations that is required for the others that are ahead.


## Question 4:
Person A invited friends B, C, and D to cook some pasta together. To make a portion of pasta, three ingredients are 
needed: noodles, cheese, sauce.

Each of these persons B, C, D has only one type of each of the ingredients: person B has noodles, person C has cheese, 
and person D has sauce. We assume that persons B, C, and D each has an unlimited supply of these ingredients (i.e., of
noodles, of cheese, of sauce), respectively. Person A, who invited them, also has an unlimited supply of all the 
ingredients.

Here is what happens: 
- Person A puts two random ingredients on the table. 
- The invited person who has the third ingredient picks up these other two ingredients, and makes the pasta (i.e., 
  takes a handful of noodles, adds some sauce, and puts on top some cheese), and then eats the pasta. 
- Person A waits for that person to finish.
- This "cycle" of is then infinitely repeated.

Write code in the AWAIT language that simulates this situation. Represent the persons A, B, C, D as processes.
You must use a SPLIT BINARY SEMAPHORE for synchronization. Make sure that your solution avoids deadlock.
EXPLAIN very briefly the advantages of using the split binary semaphore.

### Answer:
```java
sem tableMutex;
sem A;
sem noodles;
sem cheese; 
sem sauce; 

process HostA() {
    while(true){
        // non-critical section
        wait();
        
        // entry protocol
        P(A);
        P(tableMutex); 
        
        // critical section put an ingredient on the table (0=noodles, 1=cheese, 2=sauce)
        int missing_ingredient = randomInt(0..2); 
        
        // exit protocol
        if (missing_ingredient == 0) { V(noodles); }
        if (missing_ingredient == 1) { V(cheese); }
        if (missing_ingredient == 2) { V(sauce); }
        V(tableMutex);
    }
}

process PersonB(){
    while(true){
        wait(); //non-critical section
        
        // entry-protocol
        P(noodles);
        P(tableMutex);
        
        // critical section
        make_pasta();
        eat_pasta();
        
        // exit-protocol
        V(A);
        V(tableMutex);
    }
}

process PersonC(){
    while(true){
        wait(); //non-critical section

        // entry-protocol
        P(cheese);
        P(tableMutex);

        // critical section
        make_pasta();
        eat_pasta();

        // exit-protocol
        V(A);
        V(tableMutex);
    }
}


process PersonD(){
    while(true){
        wait(); //non-critical section

        // entry-protocol
        P(sauce);
        P(tableMutex);

        // critical section
        make_pasta();
        eat_pasta();

        // exit-protocol
        V(A);
        V(tableMutex);
    }
}
```

This code uses split binary semaphores, where multiple semaphores (in this noodles, cheese, and sauce) to control
access to specific resources (the table and other ingredients) based on the available "ingredient" on the table. 
Each process (person B,C,D) waits on a unique semaphore (noodle, cheese, egg) to proceed with making and eating pasta.


## Question 5:
Consider the following variant of the Readers/Writers problem:
Reader processes query a database and writer processes examine and modify it. Readers may access the database
concurrently, but writers require exclusive access. Although the database is shared, we cannot encapsulate it by a
monitor, because readers could not then access it concurrently since all code within a monitor executes with mutual
exclusion. Instead, we use a monitor merely to arbitrate access to the database. 

The database itself is global to the readers and writers. The arbitration monitor grants permission to access the 
database. To do so, it requires that processes inform it when they want access and when they have finished. There are 
two kinds of processes and two actions per process, so the monitor has four procedures: 
`request_read`, `request_write`, `release_read`, `release_write`. 

These procedures are used in the obvious ways. For example, a reader calls `request_read` before reading the database 
and calls `release_read` after reading the database. 
To synchronize access to the database, we need to record how many processes are reading and how many processes are 
writing. In the implementation below, `nr` is the number of readers, and `nw` is the number of writers; both of them are
initially 0. Each variable is incremented in the appropriate request procedure and decremented in the appropriate
release procedure. 

A software developer has started on the implementation of this monitor. Your task is to fill in the missing parts. Your 
solution does not need to arbitrate between readers and writers.

```java
monitor RW_Controller(){
    int nr = 0;
    int nw = 0;
    cond OK_to_write; // signaled when nr == 0 and nw == 0
    cond OK_to_read; // signaled when nw == 0
    
    reading();
    
    procedure request_read(){
        /* FILL IN WHAT GOES HERE
         * while (nw > 0), while (nr > 0), while (nr > 0 || nw > 0), if (nr > 0), while (nr < 0), conditional or loop 
         * is not needed here, if (nr>0 || nw>0), if (nw>0), if (nw<0), while (nw<0), if (nr==0), while (nr<0 || nw<0),
         * while (nw==0), if (nr<0 || nw<0), while (nr==0), if (nw==0), if (nr<0)) */
        {
           /*  FILL IN WHAT GOES HERE
            * (signal(OK_to_read), // nothing is needed here, wait(OK_to_read), signal(OK_to_write), 
            * signal_all(OK_to_write), wait(OK_to_write), signal_all(OK_to_read)) */
        };
        nr = nr + 1;
        /* FILL IN WHAT GOES HERE
         * (signal(OK_to_write), wait(OK_to_read), signal(OK_to_read), wait(OK_to_write), signal_all(OK_to_read),
         * // nothing is needed here, signal_all(OK_to_write)) */
    }
    procedure release_read(){
        /* FILL IN WHAT GOES HERE
         * (signal_all(OK_to_write), signal(OK_to_read), signal(OK_to_write), // nothing is needed here, 
         * wait(OK_to_write), wait(OK_to_read), signal_all(OK_to_read)) */
        
        nr = nr - 1;
        /* FILL IN WHAT GOES HERE
         * (while (nw>0), if (nr<0), while (nw<0), if (nr>0 || nw>0), while (nr<0 || nw<0), while (nr<0), while (nw==0), 
         * if (nw>0), conditional or loop is not needed here , if(nr==0), if (nr>0), while (nr>0), if (nw<0), 
         * if (nr<0 || nw<0), while (nr==0), if (nw==0), while (nr>0 || nw>0)) */
        {
        /* FILL IN WHAT GOES HERE
        * (signal_all(OK_to_read), signal(OK_to_write), wait(OK_to_read), signal_all(OK_to_write), 
        * // nothing is needed here, signal(OK_to_read) wait(OK_to_write)) */
        }
    }
    procedure request_write(){
        /* FILL IN WHAT GOES HERE
         * while (nw > 0), while (nr > 0), while (nr > 0 || nw > 0), if (nr > 0), while (nr < 0), conditional or loop
         * is not needed here, if (nr>0 || nw>0), if (nw>0), if (nw<0), while (nw<0), if (nr==0), while (nr<0 || nw<0),
         * while (nw==0), if (nr<0 || nw<0), while (nr==0), if (nw==0), if (nr<0)) */
        {
            /*  FILL IN WHAT GOES HERE
             * (signal(OK_to_read), // nothing is needed here, wait(OK_to_read), signal(OK_to_write),
             * signal_all(OK_to_write), wait(OK_to_write), signal_all(OK_to_read)) */
        };
        nr = nr +1;
        /* FILL IN WHAT GOES HERE
         * (signal(OK_to_write), wait(OK_to_read), signal(OK_to_read), wait(OK_to_write), signal_all(OK_to_read),
         * // nothing is needed here, signal_all(OK_to_write)) */
    }
    procedure release_write(){
        /* FILL IN WHAT GOES HERE
         * while (nw > 0), while (nr > 0), while (nr > 0 || nw > 0), if (nr > 0), while (nr < 0), conditional or loop
         * is not needed here, if (nr>0 || nw>0), if (nw>0), if (nw<0), while (nw<0), if (nr==0), while (nr<0 || nw<0),
         * while (nw==0), if (nr<0 || nw<0), while (nr==0), if (nw==0), if (nr<0)) */
        {
            /*  FILL IN WHAT GOES HERE
             * (signal(OK_to_read), // nothing is needed here, wait(OK_to_read), signal(OK_to_write),
             * signal_all(OK_to_write), wait(OK_to_write), signal_all(OK_to_read)) */
        };
        nr = nr +1;
        /* FILL IN WHAT GOES HERE
         * (signal(OK_to_write), wait(OK_to_read), signal(OK_to_read), wait(OK_to_write), signal_all(OK_to_read),
         * // nothing is needed here, signal_all(OK_to_write)) */
    }
}
```

### Answer:
```java
monitor RW_Controller(){
    int nr = 0;
    int nw = 0;
    cond OK_to_read();
    cond OK_to_write();
    
    procedure request_read{
        while (nw > 0) { wait(OK_to_read); }
        nr = nr + 1;
    }
    
    procedure release_read{
        nr = nr - 1; 
        if (nr == 0) { signal(OK_to_write); }
    }
    
    procedure request_write{
        while(nr > 0 || nw > 0) { wait(OK_to_write); }
        nw = nw + 1;
    }
    
    procedure release_write{
        nw = nw - 1;
        signal(OK_to_write);
        if (nw == 0){ signal_all(OK_to_read); }
    }
}

```

## Question 6:
There is a duality between monitors and message passing. What is that duality exactly? In the table, the rows represent 
notions about monitors, and the columns represent notions about message passing. Click the circle in a cell to represent
that a notion about monitors is dual to a notion about message passing

| Notion about message passing             | Notion about monitors |
|------------------------------------------|-----------------------|
| Save pending request                     |                       |
| send_reply()                             |                       |
| arms of case statement on operation kind |                       |
| send_requiests(), recieve_request()      |                       |
| retrieve and process pending request     |                       |
| request channel and operation kinds      |                       |
| recieve_request()                        |                       |


| Notion about message passing  | Notion about monitors |
|-------------------------------|-----------------------|
|                               | procedure bodies      |
|                               | signal                |
|                               | wait                  |
|                               | procedure call        |
|                               | monitor entry         |
|                               | permanent variables   |
|                               | procedure return      |
|                               | procedure identifiers |

### Answer: 
| Notion about message passing             | Notion about monitors |
|------------------------------------------|-----------------------|
| Save pending request                     | wait                  |
| send_reply()                             | procedure return      |
| arms of case statement on operation kind | procedure bodies      |
| send_requiests(), recieve_reply()        | procedure call        |
| retrieve and process pending request     | signal                |
| request channel and operation kinds      | procedure identifiers |
| recieve_request()                        | monitor entry         |

## Question 7:
Using Communicating Sequential Processes (CSP), define a process Copy that copies characters from process Bergen to 
process Vestland.

### Answer:
```java
process Copy(){
    char c;
    co:
    Bergen?c 	->  // Recieve a character from Vestland and store it in 'c'
    Vestland!c;     // Send the character stored in 'c' through Bergen
    oc
}
```

## Question 7.5 (again):
Using Communicating Sequential Processes, define a process Copy the copies a character from Vestland to Bergen.

### Answer:
```java
process Copy(){
    char c;
    co:
    Vestland?c 	->  // Recieve a character from Vestland and store it in 'c'
    Bergen!c;       // Send the character stored in 'c' through Bergen
    oc
}
```

## Question 8:
What will be printed when the following JavaScript code is executed?
```javascript
function* foo(x) {
   var y = x * (yield true);
   return y;
}

var it = foo(10);
var res = it.next(20);

console.log(res.value); // what will be printed here?
res = it.next(30);
console.log(res.value); // what will be printed here?
```

### Answer:
The generator function `foo` takes an input x, pause after yielding true, and then use a second value passed to it to
complete a calculation of `x`.
```java
function* foo(x) {
   var y = x + (yield true);  // The 'yield true' pauses the generator and returns `true`
   return y;                  // After resuming, calculates and returns y = x + foo(x);
}

var it = foo(10);        // Creates a generator instance `it`
var res = it.next(20);   // `20` is ignored because the generator runs until their first yield statement

console.log(res.value);  // Prints `true` because 'yield true' pauses and returns 'true'
res = it.next(30);       // Resumes after generator yielded 'true' and computes y = 30 + 10 = 40
console.log(res.value);  // Prints `40` because `y` is returned from the generator
```

Final answer: "true" and "40".

## Question 9:
Consider the JavaScript code: 
```javascript
var a = promisify({});
var b = promisify({});
var c = b.onReject(x => x -1);
a.link(b);
a.reject(1);
```

### Answer:
Step-by-Step breakdown of the code:
```javascript
var a = promisify({});          // 1 - new promise (P1/a) 
var b = promisify({});          // 2 - new promise (P2/b) 
var c = b.onReject(x => x-1);   // 3 - new reject handler (f3/c/x => x-1)
a.link(b);                      // 4 - link (P1/a) to (P2/b)
a.reject(1);                    // 5 - root (V5/1) that rejects (P1/a)
```
Promise graph:
```
(V5/1) --Reject--> (P1/a) --Link--> (P2/b) --Reject--> (f3/c/x => x-1) --> (V3/0) --Reject--> (P3/c) 
```

### Question 10 (need to find code snippet for this one): 
Consider the following HTML/JavaScript

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

    /* (clickHandler) starts before the other methods because user clicked after 6 ms*/
    myButton.addEventListener("click", function clickHandler() {
        Promise.resolve().then(() => { /* some promise handling code that runs for 4 ms */ });
        /* click-handling code that runs for 10 ms (CLICK HANDLER)*/
    });
    /* code that runs for 18 ms (MAINLINE EXECUTION)*/ 
</script>
```
The user **never** clicks the button `myOtherButton`. 
The user clicks the button `myButton` 7 ms after the execution starts. 
What happens at particular time points?

What happens------------------------------------at what time
`clickHandler`starts                            after X ms
`clickHandler` finishes                         after X ms
interval fires for the first time               after X ms
interval fires for the second time              after X ms
interval fires for the third time               after X ms
`intervalHandler` starts (for the first time)   after X ms
`intervalHandler` finishes (for the first time) after X ms
mainline execution starts                       after 0 ms
mainline execution finishes                     after X ms
`otherClickHandler` starts                      after X ms
`otherClickHandler` finishes                    after X ms
promise handler starts                          after X ms
promise handler finishes                        after X ms
promise resolved                                after X ms
`timeoutHandler` starts (for the first time)    after X ms
`timeoutHandler` finishes (for the first time)  after X ms
`timeoutHandler` starts (for the second time)   after X ms
`timeoutHandler` finishes (for the second time) after X ms
timer fires (for the first time)                after X ms
timer fires (for the second time)               after X ms
user clicks the button                          after 7 ms

#### Answer: 


### Question 11 (H22): 
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

#### Answer: 


