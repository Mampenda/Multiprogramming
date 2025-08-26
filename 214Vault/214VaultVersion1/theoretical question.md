### At most once property:
### Question:
Give the definition of at most once property

### Answer:
• Critical reference: reference to a variable written to by another process 

An assignment statement 
x=e; satisfies A.M.O. property if: 
• either: 
	• e contains at most one critical reference and x is not read by another process 
• or: 
	• e contains no critical references, in which case x may be read by other processes


### Safety and liveness property:
### Question:
List the 5 safety and liveness properties

### Answer:

Safety property:

* Mutual Exclusion:
	* Prevent that two or more processes are in their critical at the same time.

* Abscence of [[deadlock]]:
	* Prevent that all processses infinitely wait on entering their critical section.

* Absence of livelock:
	* Prevent that processes continuously try to acquire resources but never succeeding since it is blocked by other processes that tries the same but also does not succeeds. This happens when trying to prevent a deadlock.

* Absence of unnecessary delay:
	* When a process wants to enter critical section and no other process is in the critical section the process should enter without delay.


Liveness property:

* Eventual entry
	* That a process will eventually enter it's critical section and not wait forever.


### Other questions:
### Question:
What is mutual exclusion?

### Answer:
a type of synchronization that ensures that statements in different processes cannot execute at the same time

For example that no processes enters its critical section at the same time.
### Question:
What is condition synchronization?

### Answer:
a type of synchronization that involves delaying a process until some Boolean condition is true



### Question:
What is the difference between coarse grained and fine grained atomic blocks?

### Answer:
# TODO