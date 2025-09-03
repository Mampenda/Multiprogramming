## Question 1.A - Readers/Writers (no fairness)
Reader processes query a database and writer processes examine and modify it. Readers may access the database
concurrently, but writers require exclusive access. Although the database is shared, we cannot encapsulate it by a
monitor, because readers could not then access it concurrently since all code within a monitor executes with mutual
exclusion.

Instead, we use a monitor merely to arbitrate access to the database. The database itself is global to the readers and
writers.

The arbitration monitor grants permission to access the database. To do so, it requires that processes to inform it when
they want access and when they have finished. There are two kinds of processes and two actions per process, so the
monitor has four procedures:

- `request_read`
- `request_write`
- `release_read`
- `release_write`

These procedures are used in the obvious ways:

A reader calls `request_read` before reading the database and calls `release_read` after reading the
database. To synchronize access to the database, we need to record how many processes are reading and how many processes
are writing.

In the implementation below, `nr` is the number of readers, and `nw` is the number of writers; both of them are
initially 0.

Each variable is incremented in the appropriate request procedure and decremented in the appropriate
release procedure. A software developer has started on the implementation of this monitor.

```java
monitor ReadersWriters_Controller(){
    int nr = 0;
    int nw = 0; 
    
    // Signalled when nw == 0
    cond OK_to_read; 
}

procedure request_read() {
    wait(OK_to_read);
    nr = nr + 1;
}

procedure release_read() { nr = nr - 1; }
procedure request_write() { nw = nw + 1; }
procedure release_write() { nw = nw - 1; }
```

A beginner has implemented this code, but it misses a lot of details related to synchronization. Help fix this code.

**NOTE:** Your solution does not need to arbitrate between readers and writers, i.e., no need to handle fairness.

### Answer:
```java
// Read-Write Controller
monitor RW_Controller() {
  sem OK_to_read;
  sem OK_to_write;
  
  int readers = 0;
  int writers = 0;
}

// Reader's enter protocol
procedure request_read(){
  while(writers > 0){ wait(OK_to_read); } // non-critical section
  readers = readers + 1;                  // critical section
}

// Reader's exit protocol
procedure release_read() {
  readers = readers - 1;                      // critical section
  if (readers == 0 ) { signal(OK_to_write); } // tricky (signaling threads involves modifying internal monitor state)
}

// Writer's enter protocol
procedure request_write() {
  while( readers > 0 || writers > 0) { wait(OK_to_write); } // non-critical section
  writers = writers + 1;                                    // critical section
}

// Writer's exit protocol
procedure release_write() {
  writers = writers - 1;  // critical section
  signal_all(OK_to_read); // tricky (signaling threads involves modifying internal monitor state)
}
```

**Critical sections:** Updates to readers or writers variables and any associated signaling.
**Non-critical sections:** The while loops that just wait for conditions (`wait(…))` — the thread is blocked and not 
touching shared data.)

```java
  // Ordinary vehicle request and release access
  public void requestAccess() {
    numWorkersInside.incrementAndGet();
    if (specialAccess.get()) {
      numWorkersInside.decrementAndGet();

      // Wait until the special vehicle leaves
      lock.lock();
      lock.unlock();

      numWorkersInside.incrementAndGet();
    }
  }
```







## Question 1.B - Readers/Writers Problem (with fairness):
### Answer:
```java
monitor ReadersWriters_Controller() {
    
    // Number of active readers and writers
    int nr = 0;
    int nw = 0;
    
    // Number of writers waiting to write
    int waiting_writers = 0;
    
    // Signaled when nw == 0 or nr == 0 (i.e., when there are no active writers or readers)
    cond OK_to_read; 
    cond OK_to_write;
}

// Reader's enter protocol
procedure request_read() {

    // Enter Protocol: Readers should wait if there's an active writer or if there are writers waiting
    if (nw > 0 || waiting_writers > 0) {
        wait(OK_to_read);
    }
    
    nr = nr + 1; // critical section
    
    // Exit Protocol: Signal to other readers that it's OK to read
    signal(OK_to_read); 
}

// Reader's exit protocol
procedure release_read() {
    
    nr = nr - 1; // critical section
    
    // Exit Protocol: If there's no more readers, signal to other writers that it's OK to write
    if (nr == 0){
        signal(OK_to_write);
    }
}

// Writer's enter protocol
procedure request_write() { 
    
    waiting_writers = waiting_writers + 1; // critical section (increment count of waiting writers)
    
    // Enter Protocol: Writers should wait if there's active readers or another writer's active
    if (nr > 0 || nw > 0) {
        wait(OK_to_write);
    }
    
    // critical sections (update counts)
    waiting_writers = waiting_writers - 1;
    nw = nw + 1;
}

// Writer's exit protocol
procedure release_write() { 
    
    nw = nw - 1; // critical section
    
    // Exit Protocol: If there's writers waiting, signal next writer, otherwise signal readers that it's OK to read
    if (waiting_writers > 0) { 
        signal(OK_to_write); 
    } else {
        signal(OK_to_read);
    }
}
```


## Question 2 - Bank account:
A savings account is shared by several people (processes). Each person may deposit or withdraw funds from the account.
The current balance in the account is the sum of all deposits to date minus the sum of all withdrawals to date. The
balance must never become negative. A deposit never has to delay (except for mutual exclusion), but a withdrawal has to
wait until there are sufficient funds. A junior software developer was asked to implement a monitor to solve this
problem, using Signal-and-Continue discipline. Here is the code the junior developer has written so far:
```java
monitor Account(){
    int balance = 0;
    cond sufficient_funds;
    
    procedure deposit(int amount) { balance = balance + amount; }
    procedure withdraw(int amount) { balance = balance - amount; }
}
```
This solution is incorrect. Help the junior developer implement the monitor correctly.

### Answer:
```java
monitor Account(){
    
int balance = 0;
cond sufficient_funds; // Condition variable to wait when there are insufficient funds

    procedure deposit(int amount) {
        balance = balance + amount;  // Add amount to the balance
        signal(sufficient_funds);   // Wake up a waiting withdrawal (if any) since funds are available
    }

    procedure withdraw(int amount) {
        // Wait until there are sufficient funds for the withdrawal
        while (balance < amount) { wait(sufficient_funds); }
        balance = balance - amount;   // Perform the withdrawal when funds are sufficient
    }
}
```



