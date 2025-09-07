# Readers/Writers Problem


## Problem 1: Readers and Writers problem (without fairness)

The `Readers-Writers Problem` is a classic synchronization problem that involves managing access to a shared resource in
such a way that multiple readers can read the resource concurrently, but writers must have exclusive access to it. The
goal is to ensure that the data integrity of the shared resource is maintained while allowing as many readers as
possible to read at the same time, as long as there are no writers.

    Problem Definition

    Readers:  Multiple readers can read the shared resource simultaneously because reading does not alter the state of 
              the resource.
    Writers:  Writers need exclusive access to the shared resource because writing involves modifying it, which could 
              conflict with a read or write operation

Solve the Readers/Writers exclusion problem. The solution does not need to be fair, only mutually exclusive.
Remember that readers can read at the same time, but writers have to be alone in accessing the shared variable.
(**Hint**: You only need one counter and two semaphores.)

### Answer:
Standard (unfair) solution to the Readers and Writers Problem using one counter and two semaphores.
```java
sem readers;
sem writers;
int active_readers;

process Writers([i=1 to M]){
  while(true){
    P(writers); // entry-protocol (P = acquire)
    write();    // critical section
    V(writers); // exit protocol (V = release)
  }
}

process Readers([j=1 to N]){
  while(true){
    P(readers); // entry-protocol (P = acquire)
    active_readers = active_readers + 1; // critical section (update active_readers)

    // if this is the first reader, block writers and allow other readers
    if (active_readers == 1) { P(writers); }
    V(readers);
    read();

    P(readers); // entry-protocol (P = acquire)
    active_readers = active_readers - 1; // critical section

    // exit protocol (V = release)
    if (active_readers == 0){ V(writers); }
    V(readers);
  }
}
```

```java
monitor ReadersWriters_Controller {
int nr = 0;  // number of active readers
int nw = 0;  // number of active writers

    cond OK_to_read;
    cond OK_to_write;

    procedure request_read() {
        // Wait until there are no writers
        while (nw > 0) {
            wait(OK_to_read);
        }
        nr = nr + 1;
    }

    procedure release_read() {
        nr = nr - 1;
        // If this was the last reader, signal a waiting writer
        if (nr == 0) {
            signal(OK_to_write);
        }
    }

    procedure request_write() {
        // Wait until no one is reading or writing
        while (nr > 0 || nw > 0) {
            wait(OK_to_write);
        }
        nw = nw + 1;
    }

    procedure release_write() {
        nw = nw - 1;
        // Writers finished → let readers or writers continue
        signal(OK_to_read);
        signal(OK_to_write);
    }
}
```