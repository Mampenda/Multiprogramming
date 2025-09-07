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
sem pot;

int protions = 0;

process Bees([i=1 to N]){
  while(true){
    gather_honey();             //non-critical section 
    
    // entry-protocol
    P(empty);                   
    P(pot);
    portions = portions + 1;    // critical section

    // exit-protocol
    if(portions == H) {
        V(nonempty);
        P(pot);
    }
  }
}

process Bear(){
  while(true){
    sleep();                    //non-critical section
    
    // entry-protocol
    P(nonempty);                
    P(pot);
    portions = portions - 1;    // critical section

    // exit-protocol
    if (portions == 0){ V(empty); }
    P(pot);
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

"Simulate" this behavior in the AWAIT language. Represent the barista and the players as processes. Use semaphores for
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

## Sandwich:
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
   
    // entry-protocol (P = acquire)
    P(F);
    P(tableMutex);
    
    // critical section (0 = bread, 1 = tomato, 2 = egg)
    int missing_ingredient = randomInt(0..2); 
    
    // exit-protocol (V = release)
    if (missing_ingredient == 0){ V(bread); }
    if (missing_ingredient == 1){ V(tomato); }
    if (missing_ingredient == 2){ V(egg); }
    V(tableMutex);
}

process personA(){
   wait(); // non-critical section
   
   // entry-protocol (P = acquire)
   P(bread);
   P(tableMutex);
   
   // critical section
   make_sandwich();
   eat_sandwich();
   
   // exit-protocol (V = release)
   V(tableMutex);
   V(F);
}

process personB(){
   wait(); // non-critical section

   // entry-protocol (P = acquire)
   P(tomato);
   P(tableMutex);

   // critical section
   make_sandwich();
   eat_sandwich();

   // exit-protocol (V = release)
   V(tableMutex);
   V(F);
}

process personC(){
   wait(); // non-critical section

   // entry-protocol (P = acquire)
   P(egg);
   P(tableMutex);

   // critical section
   make_sandwich();
   eat_sandwich();

   // exit-protocol (V = release)
   V(tableMutex);
   V(F);
}
```

This solution uses binary split semaphores where each process waits for their own semaphore in addition to the common
mutex for the table. The advantages of this is that each process waits for its own semaphore before accessing the
critical section, minimizing the chance of two processes/threads accessing the critical section at the same time.

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
sem fork[5] = {1,1,1,1,1}   // list of five semaphores
sem table = 1;              // global mutex for table
int timeout = 1000;         // timeout for 1000 ms

process Philosopher([i=0 to 4]){
  while(true) {
    think();  // Non-critical section

    P(table);               // Acquire global mutex
    try_acquire_forks(i);   // Try to acquire forks with timeout
    V(table);               // Release global mutex

    if (success) {
        eat(); 
      
        // Release forks (right first, then left)
        V(fork[(i+1)%5]);
        V(fork[i]);
    } else {
        wait();  // Wait before trying again
    }
  }
}

function try_acquire_forks(i) {
  P(fork[i]);  // Pick up left fork
  timeout = now() + timeout_ms;
  
  while(true) {
    P(fork[(i+1)%5]);  // Pick up right fork
    
    // Release left fork if timeout finished and try again later
    if(now() > timeout) { 
        V(fork[i]); 
        return false;
    }
    
    // Eat if required right fork
    eat();

    // Release forks (right first, then left)
    V(fork[(i+1)%5]);
    V(fork[i]);
    return true;
  }
}
```


## Exercise 12: Traffic-Light 
There exit an intersection where two main roads Road A and Road B, intersect. To manage traffic at this intersection,
the city has installed a traffic light system. However, due to the complexity and the high volume of traffic, special
rules have been established to ensure a smooth and fair flow of vehicles.

-Vehicles arriving from Road A or Road B can queue up at the intersection and wait for the green light.

-The traffic light system alternates between allowing vehicles from Road A and Road B to pass.

-A group of vehicles from one road (say, Road A) can only proceed when the light for Road A is green, and during that
time, no vehicles from Road B are allowed to enter the intersection, and vice versa.

-To prevent any one road from monopolizing the intersection, after a set number of vehicles from Road A (5 vehicles),
the light will automatically switch to allow vehicles from Road B, even if there are more vehicles waiting on Road A.
Similarly, after a set number of vehicles from Road B pass, the light will switch to allow vehicles from Road A.

```java
// Semaphore controlling which road currently has the green light
sem driveAllowance = 1;

// Mutexes to safely update the number of cars waiting on each road
sem roadACounterMutex = 1;
sem roadBCounterMutex = 1;

// Counters for cars waiting or passing on each road
int roadACounter = 0;
int roadBCounter = 0;

// Maximum number of cars allowed to pass before switching
int MAX_CARS_ALLOWANCE = 5;

// Variable tracking which road currently has the green light
char currentGreenRoad = 'A';  // Initially Road A has green light

// Process for cars on Road A
process RoadCarA([i = 1 to A]){
    while(true){
        // Car arrives: increment Road A counter safely
        P(roadACounterMutex);
        roadACounter++;
        
        // First car checks if it can acquire the intersection (only acquire if Road A has green light)
        if(roadACounter == 1 && currentGreenRoad == 'A'){
            P(driveAllowance); // acquire intersection (green light)
        }
        V(roadACounterMutex); // release mutex
      
        
        // After passing: acquire counter and decrement safely
        P(roadACounterMutex);
        roadACounter--;
        
        // Check if we need to switch the light
        if(roadACounter == 0 || roadACounter >= MAX_CARS_ALLOWANCE){
            // Switch green light to Road B
            currentGreenRoad = 'B';
            V(driveAllowance); // release intersection for Road B
        }
        V(roadACounterMutex); // release counter mutex
    }
}

// Process for cars on Road B
process RoadCarB([i = 1 to B]){
    while(true){
        // Car arrives: acquire counter mutex and increment Road B safely
        P(roadBCounterMutex);
        roadBCounter++;
        
        // First car checks if it can acquire the intersection (only acquire if Road B has green light)
        if(roadBCounter == 1 && currentGreenRoad == 'B'){
            P(driveAllowance); // acquire intersection (green light)
        }
        V(roadBCounterMutex); // release counter mutex


        // After passing: acquire counter and decrement safely
        P(roadBCounterMutex);
        roadBCounter--;
        
        // Check if we need to switch the light
        if(roadBCounter == 0 || roadBCounter >= MAX_CARS_ALLOWANCE){
            // Switch green light to Road A
            currentGreenRoad = 'A';
            V(driveAllowance); // release intersection for Road A
        }
        V(roadBCounterMutex); // release counter mutex
    }
}
```