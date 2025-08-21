#### Notes:
cond cv;
empty(cv); //returns true if the cv queue is empty
wait(cv);  //delays the process?
signal(cv); //awakens the process

FIFO
### Question:
Could you write a solution to the restroom key problem?

### Answer:
```java
monitor restroomKey(){
	cond occupied;
	boolean key_on_hook = true;


	procedure wantToUseRestroom(){
		while(!key_on_hook) wait(occupied);
		key_on_hook = false;
	}

	procedure doneUsingRestroom(){
		key_on_hook = true;
		signal(occupied);
}
```

# From quiz:

### Question:
Is this true or false: "Permanent variables are shared by all the procedures within the body of the monitor"?
### Answer:
True



### Question:
Is this true or false: "Statements within a monitor may not access variables declared outside the monitor"?
### Answer:
True


### Question:
Is this true or false: "Permanent variables exist and retain their values as long as the monitor exists"?
### Answer:
True



### Question:
Is this true or false: "In a monitor, permanent variables are initialized before any procedure is called"?
### Answer:
True

### Question:
Is this true or false: "Monitor procedures, by definition, execute with mutual exclusion"?
### Answer:
True


### Question:
Is this true or false: "At most one instance of one monitor procedure may be active at a time"?

### Answer:
True


### Question:
What is a value of a condition variable?

### Answer:
The value of a condition variable is true or false and belongs to a process.


### Question:
Is this statement correct: "Function _**empty(cond_var)**_ returns TRUE if the queue of _**cond_var**_ is not empty"?

### Answer:
This statement is wrong


### Question:
How can we block a process on a condition variable _**cond_var**_, i.e. what command do we execute?

### Answer:
wait(cond_va)


### Question:
Consider function _**signal(cond_var)**_.

Assume that the delay queue of _**cond_var**_ is non-empty.

Then, what will _**signal(cond_var)**_ do?
### Answer:
Awaken the process at the front of the queue




### Question:
Which one is the "Signal and Continue" signalling discipline?

Options:
  The signaller continues and the signalled process executes at some later time 

  The signaller continues and the signalled process executes now 

  The signaller waits until some later time and the signalled process executes now 

  The signaller waits until some later time and the signalled process executes at some later time
### Answer:
The signaller continues and the signalled process executes at some later time


### Question:
Fill in the blank:

_"When a process executes _______________, it moves from executing in the monitor to the queue associated with the condition variable **cond_var**"._

### Answer:
wait(cond_var);


### Question:
When a process executes _**signal(cond_var)**_, the process at the front of the condition variable queue moves to the entry queue. 

Which signalling discipline is assumed here?

### Answer:
Signal and Continue


### Question:
When a process executes _**signal(cond_var)**_, the process executing in the monitor moves to the entry queue and the process at the front of the condition variable queue moves to executing in the monitor.

Which signalling discipline is assumed here?


### Answer:
Signal and Wait


### Question:
Is this true or false: "When a process calls the Vsem operation, it wants to awaken a delayed process, if there is one"?

### Answer:
True


### Question:
Fill in the blank.

"_One can say that condition variables are similar to the **P** and **V** operations on semaphores._

- _The **wait** operation, like **P**, delays a process._
- _The **signal** operation, like **V**, awakens a process._

_An important difference is that **wait** always delays a process until a later __________________ is executed."_
### Answer:
signal(cv);








### Signalling Disciplines:
### Question:

What are the signalling disciplines and their definition?
### Answer:
Signal and Continue
	The signaler continues and the signaled process executes at some later time.

Signal and Wait
	The signaler waits until some other later time and the signaled process executes now.



### Question:
Is this true or false: "With _**wait**_ and _**signal**_, delayed processes are awakened in the order in which they delayed"?
### Answer:
True


### Question:
Is this true or false: "With _**wait**_ and _**signal**_, the delay queue is a FIFO (First-In-First-Out) queue"?

### Answer:
True


### Question:

Is this true or false: "Execution of _**signal_all(cond_var)**_ awakens all processes delayed on _**cond_var**_, and it requires that the queue is not empty"?

### Answer: 
False


### Question:
Consider the following loop:

**while** (!_empty_(cond_var)) {   
  
   _signal_(cond_var);  
  
}

Rewrite this loop using the following operations: _**wait(cond_var)**_, _**signal(cond_var)**_, _**minrank(cond_vr)**_, _**signal_all(cond_var)**_.

You answer should be "the shortest" possible, i.e. if you don't have to use any of the above operations to express whatever is expressed by the loop, don't use it.

### Answer:

signal_all(cv);


### Question:
Is this true or false: "The _**signal**_ statements in _**deposit**_ and _**fetch**_ are executed unconditionally because in both cases the signalled condition is true at the point of the _**signal**_"?

### Answer:
True


### Question:
Why is it necessary to recheck delay conditions?

### Answer:
Since signal is merely a hint that the process now can execute, delay conditions can change in the meantime between signal and wait.


### Question:
Is this true or false: "Writers need to delay at the start of _**request_write**_ until both _**nr**_ and _**nw**_ are zero"?

### Answer:
True


### Question:
Is this true or false: "Readers need to delay at the start of _**request_read**_ until both _**nr**_ and _**nw**_ are zero"?

### Answer:
False


### Question
Formulate in your own words The Sleeping Barber problem.


### Answer:
There is a barber and he has a barbershop. Customers walk inside the barbershop through one door and exits through another, there are chairs for waiting and one chair for getting a hair cut. Customers request getting a haircut, the barber sleeps if there are no one who waits on getting a haircut. When one is done getting a haircut he alerts the next customer and leaves the barbershop.


### Question:
What are the two important stages that the customers have in The Sleeping Barber problem?

### Answer:
Getting haircut and leaving.

### Question:
What are the three important stages that the barber has in The Sleeping Barber problem?

### Answer:

Sleeping, getting the next customer and haircut.
### Question:
Explain in your own words how procedure _**get_next_customer()**_ works in The Sleeping Barber problem.
```java

procedure get_next_customer(){
		barber = barber + 1;
		signal(barber_available);
		while(chair == 0) wait(chair_occupied);
		chair = chair - 1;
	}
```
### Answer:
First he sets barber to barber + 1 and signal(barber_available) this is to show that the barber is available.

He then waits for the chair to become 1 and then sets chair to 0.

### Monitor as a semaphore
### Question:

Could you write a monitor as a semaphore with signal and wait?
### Answer:

Monitor as a semaphore with signal and wait:
```java
Monitor Semaphore{
	int s = 0; // s>=0
	cond pos; //signaled when s > 0

	procedure Psem() {
		if (s == 0) wait(pos)
		else s = s - 1;
	}

	procedure Vsem(){
		if (empty(pos)) s = s + 1;
		else signal(pos)
	}
}
```

# Task from exam:
### Question:
Recall the Readers/Writers problem: readers processes query a database and writer processes examine and modify it. Readers may access the database concurrently, but writers require exclusive access. Although the database is shared, we cannot encapsulate it by a monitor, because readers could not then access it concurrently since all code within a monitor executes With mutual exclusion. Instead, we use a monitor merely to arbitrate access to the database. The database itself is global to the readers and writers. In the Readers/Writers problem, the arbitration monitor grants permission to access the database. To do so, it requires that processes inform it when they want access and when they have finished. There are two kinds of processes and two actions per process, so the monitor has four procedures: request_read, release_read, request_write, release_write. These procedures are used in the obvious ways. For example, a reader calls request_read before reading the database and calls release_read after reading the database.

To synchronize access to the database, we need to record how many processes are reading and how many processes are writing. In the implementation below, nr is the number of readers, and nw is the number of writers; both of them are initially 0. Each variable is incremented in the appropriate request procedure and decremented in
the appropriate release procedure.



```java
monitor ReadersWriters_Controller {
	int nr = 0;
	int nw = 0;
	cond OK_to_read; // signalled when nw == O


	procedure request_read() {
		wait(OK_to_read);
		nr = nr + 1;
	}

	procedure release_read() {
		nr = nr- 1;
	}

	procedure request_write() {
		nw = nw + 1;
	}

	procedure release_write() {
		nw = nw - 1;
	}
}
```


A beginner software developer has implemented this code, but has unfortunately missed a lot of details related to synchronization. Help the beginner developer fix this code.

Note: Your solution does not need to arbitrate between readers and writers.

### Answer:
```java
monitor ReadersWriters_Controller {
	int nr = 0;
	int nw = 0;
	cond OK_to_read; // signalled when nw == O
	cond OK_to_write; // signalled when nw == 0 and nr == 0

	procedure request_read() {
		while(nw > 0) wait(OK_to_read);
		nr = nr + 1;
	}

	procedure release_read() {
		nr = nr- 1;
		if (nr == 0 && nw == 0) signal(OK_to_write);
	}

	procedure request_write() {
		while(nr > 0 || nw > 0) wait(OK_to_write)
		nw = nw + 1;
	}

	procedure release_write() {
		nw = nw - 1;
		if (nw == 0){
			signal(OK_to_write)
			signal_all(OK_to_read)
		}
	}
}
```



```java
monitor ReadersWriters_Controller {
	int nr = 0;
	int nw = 0;
	cond OK_to_read; // signalled when nw == O
	cond OK_to_write;


	procedure request_read() {
		while(nw > 0) {wait(OK_to_read);}
		nr = nr + 1;
	}

	procedure release_read() {
		nr = nr- 1;
		if (nr == 0){
			signal(OK_to_write);
		}
	}

	procedure request_write() {
		while(nw > 0 or nr > 0){wait(OK_to_write);}
		nw = nw + 1;
	}

	procedure release_write() {
		nw = nw - 1;
		signal_all(OK_to_read);
		signal(OK_to_write);
	}
}

```
### Task from exam:
### Question:
There is duality between monitors and message passing. What is that duality exactly?

In the table, the rows represent notions about monitors, and the columns represent notions about message passing.

Cross the circle in a cell to represent that a notion about monitors is dual to a notion about message passing. Please match the values.

 __ | local server variables | arms of case statement on operation kind | retrieve and process pending request | send request(); receive reply()' | save pending request | receive request() | 'request' channel and operation kinds | 'send reply()'
-- | -- | -- | -- | -- | -- | -- | -- | -- 
procedure identifiers | 
'signal' | 
permanent variables | 
monitor entry |
procedure return |
procedure bodies |
procedure call |
'wait' |


### Answer:

 __ | local server variables | arms of case statement on operation kind | retrieve and process pending request | send request(); receive reply()' | save pending request | receive request() | 'request' channel and operation kinds | 'send reply()'
-- | -- | -- | -- | -- | -- | -- | -- | -- 
procedure identifiers | | | | | | | X |
'signal' | | | X |
permanent variables | X | 
monitor entry | | | | | | X
procedure return | | | | | | | | X
procedure bodies | | X
procedure call | | | | X
'wait' | | | | | X



### Question:
Could you write down the solution to the barbershop problem?


### Hint:
```java
	int barber = 0, chair = 0, open = 0;
	//barber == bavail - cinchair
	//chair == cinchair - bbusy
	//open == bdone - cleave
	cond barber_available; 
	cond chair_occupied;
	cond door_open;
	cond customer_left;
```
### Answer:

```java
monitor Barbershop{
	int barber = 0, chair = 0, open = 0;
	//barber == bavail - cinchair
	//chair == cinchair - bbusy
	//open == bdone - cleave
	cond barber_available; 
	cond chair_occupied;
	cond door_open;
	cond customer_left;

	procedure get_haircut(){
		while (barber == 0) wait(barber_available);
		barber = barber - 1;
		chair = chair + 1;
		signal(chair_occupied);
		while (open == 0) wait(door_open);
		open = open - 1;
		signal(customer_left);
	}

	procedure get_next_customer(){
		barber = barber + 1;
		signal(barber_available);
		while(chair == 0) wait(chair_occupied);
		chair = chair - 1;
	}

	procedure finished_cut(){
		open = open + 1;
		signal(door_open);
		while(open > 0) wait(customer_left);
	}
}
```
