### Tasks from video and quiz 18:
Question:
What is meant by "eligible atomic action"?

### Answer:
Eligible atomic action means that it is the next atomic action in the process that could be executed.



Eligible 
### Question:
Which kinds of scheduling policies were mentioned in the video?

### Answer:
Round robin was mentioned.




### Question:
How does round-robin work?

### Answer:

Each process gets a fixed time slice to execute, and then the scheduler moves on to the next process in the queue. This cycle continues until all processes are complete. Round-robin is known for its fairness, ensuring that each process gets a fair share of CPU time.


### Question:
What are the three different type of fairness a scheduling policy can have and what is their definition?

### Answer:

Unconditional fairness: a scheduling policy is u.f. if every unconditional
atomic action that is eligible will be eventually executed.

Weak fairness: unconditionally fair + every conditional atomic action that
is eligible, will be eventually executed, assuming that its condition becomes
true and remains true until it is seen by the process that executes the
conditional atomic action.

Strong fairness: it is unconditionally fair + every conditional atomic action that is eligible will be eventually executed assuming that its condition is "always" (infinitely often) true
