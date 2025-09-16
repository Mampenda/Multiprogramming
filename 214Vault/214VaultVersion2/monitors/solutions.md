# Monitors: Exercises with Solutions

## Exercise 1 - Bank account:

Several people share a saving account that each person may deposit to or withdraw from.

The current balance in the account is the sum of all deposits to date minus the sum of all withdrawals to date. The
balance must never become negative. A deposit never has to delay (except for mutual exclusion), but a withdrawal has to
wait until there are enough funds.

A junior software developer has implemented a solution to this problem using a monitor with Signal-and-Continue
discipline.

```java
monitor Account() {
    int balance = 0;
    cond sufficient_funds;

    procedure deposit ( int amount){
        balance = balance + amount;
    }
    procedure withdraw ( int amount){
        balance = balance - amount;
    }
}
```

This solution is non-optimal. Help the junior developer implement the monitor correctly.

### Answer:

```java
monitor Account() {

    int balance = 0;
    cond sufficient_funds; // Condition variable to wait when there are insufficient funds

    procedure deposit ( int amount){
        balance = balance + amount;  // Add amount to the balance
        signal(sufficient_funds);   // Wake up a waiting withdrawal (if any) since funds are available
    }

    procedure withdraw ( int amount){
        // Wait until there are sufficient funds for the withdrawal
        while (balance < amount) {
            wait(sufficient_funds);
        }
        balance = balance - amount;   // Perform the withdrawal when funds are sufficient
    }
}
```

## Exercise 2 - Readers/Writers Problem (without fairness):

The `Readers-Writers Problem` is a classic synchronization problem that involves managing access to a shared resource in
such a way that multiple readers can read the resource concurrently, but writers must have exclusive access to it. The
goal is to ensure that the data integrity of the shared resource is maintained while allowing as many readers as
possible to read at the same time, as long as there are no writers.

    Problem Definition

    Readers:  Multiple readers can read the shared resource simultaneously because reading does not alter the state of 
              the resource.
    Writers:  Writers need exclusive access to the shared resource because writing involves modifying it, which could 
              conflict with a read or write operation

The arbitration monitor grants permission to access the database. To do so, it requires that processes to inform it when
they want access and when they have finished. There are two kinds of processes and two actions per process, so the
monitor has four procedures:

- `request_read`
- `request_write`
- `release_read`
- `release_write`.

These procedures are used in the obvious ways:

A reader calls `request_read` before reading the database and calls `release_read` after reading the
database. To synchronize access to the database, we need to record how many processes are reading and how many processes
are writing.

In the implementation below, `nr` is the number of readers, and `nw` is the number of writers; both of them are
initially 0.

Each variable is incremented in the appropriate request procedure and decremented in the appropriate
release procedure. A software developer has started on the implementation of this monitor.

A junior developer has implemented this code, but it misses a lot of details related to synchronization. Help fix this
code.

```java
monitor ReadersWriters_Controller() {
    int nr = 0;
    int nw = 0;

    // Signaled when nw == 0
    cond OK_to_read;
}

procedure request_read() {
    wait(OK_to_read);
    nr = nr + 1;
}

procedure release_read() {
    nr = nr - 1;
}

procedure request_write() {
    nw = nw + 1;
}

procedure release_write() {
    nw = nw - 1;
}
```

Solve the Readers/Writers problem using monitors. The solution does not need to be fair, only mutually exclusive.
Remember that readers can read at the same time, but writers have to be alone in accessing the shared variable.

**NOTE:** Your solution does not need to arbitrate between readers and writers (i.e., no need to handle fairness).

### Answer:

```java
// Read-Write Controller
monitor RW_Controller() {

    int nr = 0; // number of active readers
    int nw = 0; // number of active writers

    // Condition variables for readers and writers
    cond OK_to_read;
    cond OK_to_write;
}

// Reader's enter protocol
procedure request_read() {
    while (nw > 0) {
        wait(OK_to_read); // non-critical section
    }
    nr = nr + 1;
}

// Reader's exit protocol
procedure release_read() {
    nr = nr - 1;
    if (nr == 0) {
        signal(OK_to_write); // tricky (signaling threads involves modifying internal monitor state)
    }
}

// Writer's enter protocol
procedure request_write() {
    while (nr > 0 || nw > 0) {
        wait(OK_to_write); // non-critical section
    }
    nw = nw + 1;
}

// Writer's exit protocol
procedure release_write() {
    nw = nw - 1;            // critical section
    signal_all(OK_to_read); // tricky (signaling threads involves modifying internal monitor state)
}
```

**Critical sections:** Updates shared resource state (e.g., `nr`, `nw`, and signaling condition variables).
**Non-critical sections:** The while loops that just `wait(…))` for conditions.

## Exercise 3 - Readers/Writers Problem (with fairness):

Without arbitrating between readers and writers, you risk starvation of writers, i.e., if there is a continuous stream
of readers, a writer may never get access to the database.

How would you modify your solution to Exercise 1 to **ensure fairness** so that the readers don't starve the writers?

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
    if (nr == 0) {
        signal(OK_to_write);
    }
}

// Writer's enter protocol
procedure request_write() {

    waiting_writers = waiting_writers + 1; // critical section (update count of waiting writers)

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



