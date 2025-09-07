# Exercises - Monitors

A `Monitor` is a synchronization construct that provides a convenient and safe way to manage access to shared
resources in concurrent programming.

It combines a `mutex` (mutual exclusion) lock with `semaphores` (condition variables) to allow threads to wait for
certain conditions to be met and get `signalled` when they can proceed.

**Key Idea:** The monitor doesn't store shared data, but controls access to it. Threads call monitor procedures to
request/release access safely.

## Exercise 1 - Readers/Writers Problem (without fairness):

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

## Exercise 2 - Readers/Writers Problem (with fairness):

Without arbitrating between readers and writers, you risk starvation of writers, i.e., if there is a continuous stream
of readers, a writer may never get access to the database.

How would you modify your solution to Exercise 1 to **ensure fairness** so that the readers don't starve the writers?

## Exercise 3 - Bank account:

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
