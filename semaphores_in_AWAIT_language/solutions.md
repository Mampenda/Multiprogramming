# Exercises with Solutions
## Bear and Honey
Here is an example of sudo-code with semaphores for synchronization to represent the following problem:

- We have `N` honey bees and a hungry bear.
- They share a pot of honey.
- The pot is initially empty.
- Its capacity is `H` portions of honey.
- The bear sleeps until the pot is full, then eats all the honey and goes back to sleep.
- Each bee repeatedly gathers one portion of honey and puts it in the pot.
- The bee who fills the pot (i.e. puts in the H'th portion) awakens the bear.

### Answer:
```java
sem nonempty = 1;
sem empty = 0;

int protions = 0;

process Bees([i=1 to N]){
  while(true){
    gather_honey();             //non-critical section 
    P(empty);                   // entry-protocol
    portions = portions + 1;    // critical section

    // exit-protocol
    if(portions == H){
      V(nonempty);
    } else {
      V(empty);
    }
  }
}

process Bear(){
  while(true){
    sleep();                    //non-critical section
    P(nonempty);                // entry-protocol
    portions = portions - 1;    // critical section

    // exit-protocol
    if (portions == 0){
      V(empty);
    } else {
      V(nonempty);
    }
  }
}
```

## Hungry Chicks
Here is an exercise of sudo-code in _Await Language_ with semaphores for synchronization:

- We have `N` baby birds and one parent bird.
- The baby birds eat out of a common dish that initially contains `F` portions of food.
- Each baby repeatedly
    - eats one portion of food at a time,
    - sleeps for a while, and then
    - comes back to eat.
- When the dish becomes empty, the baby bird who empties the dish awakens the parent bird.
    - The parent refills the dish with `F` portions, then
    - waits for the dish to become empty again.
- This pattern repeats forever.

### Answer:
```java
sem empty = 0;
sem full = F;
sem dishMutex;

int portions = F;

process BabyBirds([i=1 to N]){
    while(true){
      sleep(); // non-critical section
      
      // entry-protocol
      P(full);
      P(dishMutex);
      
      portions = portions - 1; // critical section
      
      // exit-protocol
      if (portions == 0){ V(empty); }
      V(dishMutex);
    }
}

process MamaBird(){
    while(true){
        wait();         // non-critical section
      // entry-protocol 
      P(empty);
      P(dishMutex); 
      
      portions = F;   // critical section
    
      // exit-protocol 
      V(nonempty);
      V(dishMutex);
    }
}
```

## Gløgg
Here is an exercise of sudo-code in _Await Language_ with semaphores for synchronization:

- Three persons, who like gløgg very much, have gathered to play the following game in a bar. To drink a portion, each
  of them obviously needs three ingredients: the gløgg, a mug, and almonds.
- One player has the gløgg, the second has mugs, and the third has the almonds. Assume each of the players has an
  unlimited supply of their ingredients, respectively.
- The barista, who also has an unlimited supply of the ingredients, puts two random ingredients on the table.
- The player who has the third ingredient picks up the other two, makes the drink, and then drinks it.
- The barista waits for the player to finish.
- This "cycle" then repeats.

"Simulate" this behaviour in the AWAIT language. Represent the players and the barista as processes. Use semaphores for
synchronization. Make sure that your solution avoids deadlock.

### Answer:
```java
sem barista;
sem gløgg;
sem mug;
sem almonds;

process Barista(){
  while(true){
    wait();     // non-critical section
    P(barista); // entry-protocol
    int missing_ingredient = randomInt(0..2); // critical section (0=gløgg, 1=mug, 2=almonds)

    // exit-protocol 
    if (missing_ingredient == 0){ V(gløgg); }
    if (missing_ingredient == 1){ V(mug); }
    if (missing_ingredient == 2){ V(almonds); }
  }
}

process P1(){
  while(true){
    wait();   // non-critical section
    P(gløgg); // entry-protocol

    // critical section
    make_gløgg();
    drink_gløgg();

    V(barista); // exit-protocol
  }
}
process P2(){
  while(true){
    wait(); // non-critical section
    P(mug); // entry-protocol

    // critical section
    make_gløgg();
    drink_gløgg();

    V(barista); // exit-protocol
  }
}
process P3(){
  while(true){
    wait();     // non-critical section
    P(almonds); // entry-protocol

    // critical section
    make_gløgg();
    drink_gløgg();

    V(barista); // exit-protocol
  }
}
```

## Producers and Consumers - Split Binary Semaphores
Make producers/consumers processes that synchronizes with split binary semaphores.
The buffer has only room for one element of data.
### Answer:
```java
type buffer;
sem empty = 0;
sem full = 1; 

process Producer([i=1 to M]){
    while(true){
        P(empty);       // entry-protocol 
        buffer = data;  // critical section
        V(full);        // exit-protocol
    }
}

process Consumer([j=1 to N]){
  while(true){
    P(full);       // entry-protocol 
    data = buffer; // critical section
    V(empty);      // exit-protocol
  }
}
```

## Producers and Consumers - Bounded Buffers: Resource Counting
Make producers/consumers processes that synchronizes around a buffer.
### Answer:
```java
type buffer[n]; // buffer of size n is initially empty 

// front and rear has index 0 since buffer is initially empty
int front = 0; 
int rear = 0;

// semaphore for producer and consumer for mutual exclusion and signalling
sem mutexP; 
sem mutexC; 
sem full = 0;  // zero full slots
sem empty = n; // n empty slots

process Producer([i=1 to M]){
  while(true){
    // entry-protocol
    P(empty);
    P(mutexP);

    // critical section
    buffer[rear] = data;
    rear = (rear + 1) mod n; // wrap-around
    
    // exit-protocol
    V(mutexC);
    V(full);
  }
}

process Consumer([j=1 to N]){
  while(true){
    // entry-protocol
    P(full);
    P(mutexC);

    // critical section 
    data = buffer[front];
    front = (front + 1) mod n; // wrap-around

    // exit-protocol
    V(mutexP);
    V(empty);
  }
}
```

## Readers/Writers as an exclusion problem

The `Readers-Writers Problem` is a classic synchronization problem that involves managing access to a shared resource in
such a way that multiple readers can read the resource concurrently, but writers must have exclusive access to it. The
goal is to ensure that the data integrity of the shared resource is maintained while allowing as many readers as
possible to read at the same time, as long as there are no writers.

    Problem Definition

    Readers:  Multiple readers can read the shared resource simultaneously because reading does not alter the state of 
              the resource.
    Writers:  Writers need exclusive access to the shared resource because writing involves modifying it, which could 
              conflict with a read or write operation

Could you solve the Readers/Writers exclusion problem? The solution does not need to be fair, only mutual exclusive.
Remember that readers can read at the same time, but writers have to be alone in accessing the shared variable.
The solution is supposed to be unfair. (**Hint**: You only need one counter and two semaphores.)

### Answer:
```java
sem readers;
sem writers;
int active_readers;

process Writers([i=1 to M]){
  while(true){
    P(writers); // entry-protocol
    write();    // critical section
    V(readers); // exit protocol
  }
}

process Readers([j=1 to N]){
  while(true){
    P(readers); // entry-protocol (for updating active_readers)
    active_readers = active_readers + 1; // critical section 

    // if this is the first reader, block writers and allow other readers
    if (active_readers == 1) { P(writers); }
    V(readers);
    read(); // critical section 

    P(readers); // entry-protocol (for updating active_readers)
    active_readers = active_readers - 1; // critical section

    // exit protocol    
    if (active_readers == 0){ V(writers); }
    V(readers);
  }
}
```

## Readers/Writers another variant
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
// Read-Write Controller
monitor RW_Controller() {
  sem OK_to_read;
  sem OK_to_write;
  int readers = 0;
  int writers = 0;
}

// Reader's enter protocol
procedure request_read(){
  while(writers > 0){ wait(OK_to_read); }
  readers = readers + 1;
}

// Reader's exit protocol
procedure release_read() {
  readers = readers - 1;
  if (readers == 0 ) { signal(OK_to_write); }
}

// Writer's enter protocol
procedure request_write() {
  while( readers > 0 || writers > 0) { wait(OK_to_write); }
  writers = writers + 1;
}

// Writer's exit protocol
procedure release_write() {
  writers = writers - 1;
  signal_all(OK_to_read);
}
```

## Exercise 9: Multi-country

Consider Multi-country, which is a country, and Multi-city, which is its capital city.

The airport of Multi-city has introduced strict entry requirements for arriving passengers because of a global pandemic.
Some of the passengers are vaccinated, while others are not vaccinated. All passengers arriving to Multi-city must go
through the document/passport control, where their vaccination certificates are checked by border guard officers.

Upon arriving to Multi-city Airport, passengers, both vaccinated and un-vaccinated, are mingling in the Mingling Zone,
and they are walking towards to the Documents Checking Zone. There, the border guard checks their vaccination
certificates, imposes quarantine on the un-vaccinated ones, and in any case lets all the passengers into the city.

Assume that the passengers enter the Documents Checking Zone in a random order. The only requirement is that there must
be never un-vaccinated and vaccinated passengers in the Documents Checking Zone at the same time. However, people with
the same vaccination status are allowed in the Documents Checking Zone at the same time (that is, at any given moment of
time, either all passengers in the Documents Checking Zone are vaccinated, or all passengers in the Documents Checking
Zone are un-vaccinated).

Assume that there are `N` vaccinated passengers, and `M` un-vaccinated passengers who have just landed at Multi-city
Airport.

Your task is to simulate the described situation in the AWAIT language.
👉 Represent passengers as processes.
👉 Use semaphores for synchronization.
👉 Make sure that your solution avoids deadlock.
💡 Your solution need NOT be fair.

### Answer:
```java
sem MutexV; // Mutex for vaccinated counter
sem MutexU; // Mutex for un-vaccinated counter
sem checkZone; // Mutex for check zone

int vaxCount = 0;
int unvaxCount = 0;

process Vaccinated([i=1 to N]){
  while(true){
    walk_to_check_zone(); // non-critical section

    // entry-protocol (for counter)
    P(MutexV);
    vaxCount = vaxCount + 1;

    // entry-protocol (for check zone)
    if (vaxCount = 1) { P(checkZone); }
    V(MutexV);
    check(); // critical section

    // exit-protocol
    P(MutexV);
    vaxCount = vaxCount - 1;
    if (vaxCount == 0) { V(checkZone); }
    V(MutexV);
  }
}

process UnVaccinated([j=1 to M]){
  while(true){
    walk_to_check_zone(); // non-critical section

    // entry-protocol (for counter)
    P(MutexU);
    unvaxCount = unvaxCount + 1;

    // entry-protocol (for check zone)
    if (unvaxCount = 1) { P(checkZone); }
    V(MutexU);
    check(); // critical section

    // exit-protocol
    P(MutexU);
    unvaxCount = unvaxCount - 1;
    if (unvaxCount == 0) { V(checkZone); }
    V(MutexU);
  }
}
```

## Dining Philosophers
In short, the dining philosophers problem is about four philosophers that are seated around a table with five plates
and five utensils available. For simplicity, we'll use chopsticks.

Each philosopher needs two chopsticks to be able to eat, so there's at most two philosophers that can eat at the same
time. After a philosopher has finished eating, she'll put down both chopsticks, making them available for the other
philosophers.

Solve the Dining Philosophers Problem with the philosophers as processes and all the forks as semaphores.

### Answer:
```java
sem fork[5] = {1,1,1,1,1}

process Philosopher([i=0 to 4]){
  while(true){
    think(); // non-critical section

    // entry-protocol
    P(fork[i]);
    P(fork[i+1]);

    eat(); // critical section

    // exit-protocol
    V(fork[i]);
    V(fork[i+1]);
  }
}
```