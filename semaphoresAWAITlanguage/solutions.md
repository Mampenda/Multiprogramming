# Bear and Honey
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
sem empty = 0;
sem full = 1;

// Numper of portions in the pot
int portions = 0;

process Bees([i=1 to N]){
    while(true){
        collect_honey(); // non-citical section
        P(empty);        // entry-protocol

        // critical section
        portions += 1;
        
        // exit-protocol
        if (portions == H){
            V(full);
        } else {
            V(empty);
        }
    }
}

protocol Bear() {
    sleep(); // non-citical section
    P(full); // entry-protocol
    
    // critical section
    portions -= 1;
    
    // exit-protocol
    if (portions == 0) {
        V(empty);
    } else {
        V(full);
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
sem non-empty = 1; 

// number of portions in the dish
int portions = F;

process Babybirds([i=1 to N]){
    while (true){
        sleep();        // non-critical section
        P(non-empty)    // entry-protocol
        
        // critical section
        portions = portions - 1; 
        
        // exit-protocol
        if (portions == 0){
            V(empty);
        } else { 
            V(non-empty);
        }
    }
}

process Mamabird(){
    while(true){
        wait();     // non-critical section
        P(empty);   // entry-protocol
        
        // critical section
        portions = F;
        
        // exit-protocol
        V(non-empty)
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
sem barista = 1; 
sem almonds = 0;
sem gløgg = 0;
sem mug = 0;

process Barista(){
    while(true){
        wait();     // non-critical section
        P(barista); // Entry protocol (put two ingredients on the table)
        
        // critical section (the random missing ingredient: 0=almonds, 1=gløgg, 2=mug)
        missingIngridient = randomInt(0,2);
        
        // exit-protocol
        if (missingIngredient == 0){ V(almonds); }
        if (missingIngredient == 1){ V(gløgg); }
        if (missingIngredient == 2){ V(mug); }
    }
}

process Player1(){
    while(true){
        wait();     // non-critical section
        P(almonds); // entry protocol 
        
        // critical section
        make_gløgg();
        drink_gløgg();
        
        // exit protocol
        V(barista);
    }
}

process Player2(){
    while(true){
        wait();     // non-critical section
        P(gløgg);   // entry protocol 

        // critical section
        make_gløgg();
        drink_gløgg();

        // exit protocol
        V(barista);
    }
}

process Player3(){
    while(true){
        wait();     // non-critical section
        P(mug);     // entry protocol 

        // critical section
        make_gløgg();
        drink_gløgg();

        // exit protocol
        V(barista);
    }
}
```

## Dining Philosophers
In short, the dining philosophers problem is about five philosophers that are seated around a table with five plates
and five utensils available. For simplicity, we'll use chopsticks.

Each philosopher needs two chopsticks to be able to eat, so there's at most two philosophers that can eat at the same
time. After a philosopher has finished eating, she'll put down both chopsticks, making them available for the other
philosophers.

Solve the Dining Philosophers Problem with the philosophers as processes and all the forks as semaphores.

### Answer:
```java 
sem fork[5] = {1,1,1,1,1}

process Philosopher([1=0 to 4]){
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

## Producers and Consumers - Split Binary Semaphores
Make producers/consumers processes that synchronizes with split binary semaphores.
The buffer has only room for one element of data.
### Answer
```java
type buffer;
sem empty = 1; 
sem full = 0;

process Producer([i=1 to M]){
	while(true) {
		P(empty);   // entry-protocol
		buf = data; // critical section (fill buffer with data)
		V(full);    // exit-protocol
	}
}

process Consumer([j=1 to N]){
	while(true) {
		P(full);    // entry-protocol
		data = buf; // critical section (consume data from buffer)
		V(empty);   // exit-protocol
	}
}
```

## Producers and Consumers - Bounded Buffers: Resource Counting
Make producers/consumers processes that synchronizes around a buffer.
### Answer:
```java
type buffer[n]; // A buffer of size 'n' that acts as the shared resource between producers and consumers.
int front = 0;  // 'front' is the index from which the consumer will read (consume) data.
int rear = 0;   // 'rear' is the index where the producer will write (produce) data.

// Semaphores representing the nr of empty slots (n) and nr of items (0) in the buffer
sem empty = n;  // Initial value is 'n' because initially, the buffer is empty.
sem full = 0;   // Initial value is '0' because initially the buffer has with no items to consume.

// Mutex semaphores to provide mutual exclusion
sem mutexD = 1;
sem mutexF = 1; 

process Producer([i=1 to M]){
  while(true) {
    // ENTRY PROTOCOL
    P(empty);   // Wait on 'empty' to ensure there is space in the buffer.
    P(mutexD);  // Lock the critical section for the producer.

    // CRITICAL SECTION
    buf[rear] = data;        // Place the produced data in the buffer at the 'rear' index.
    rear = (rear + 1) mod n; // Move 'rear' to the next index, 'mod n' ensures wrap-around

    // EXIT PROTOCOL
    V(mutexD);  // Release the producer's lock on the buffer.
    V(full);    // Signal the 'full' semaphore to indicate a new item is available in the buffer.
	}
}

process Consumer([j=1 to N]){
while(true) {
    // ENTRY PROTOCOL
    P(full);     // Wait on 'full' to ensure there is data to consume.
    P(mutexF);   // Lock the critical section for the consumer. 
  
    // CRITICAL SECTION
    result = buf[front];        // Read and consume the data from the buffer at the 'front' index.
    front = (front + 1) mod n;  // Move 'front' to the next index, 'mod n' ensures wrap-around

    // EXIT PROTOCOL
    V(mutexF);  // Release the consumer's lock on the buffer.
    V(empty);   // Signal (increment) the 'empty' semaphore to indicate a space is available in the buffer.
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
int readerCount = 0; // Counter to keep track of the number of active readers

sem readers = 1;  // Semaphore to control access to the 'readerCount' (ensuring mutual exclusion)
sem writers = 1;  // Semaphore to ensure exclusive access to the shared resource for writers

process Reader() {
  while (true) {
    // entry-protocol for Reader
    P(readers);        // Acquire 'readers' to safely increment 'readerCount'
    readerCount += 1;  // Increment 'readerCount'

    // If this is the first reader, acquire 'writers' to block writers from writing.
    if (readerCount == 1) { P(writers); }
    V(readers);  // Release 'readers', allowing other readers to update 'readerCount'.

    // CRITICAL SECTION for Reader (multiple readers can access the resource at the same time).
    read();

    // exit-protocol for Reader
    P(readers);        // Re-acquire 'readers' to safely decrement 'readerCount' as the reader is about to leave.
    readerCount -= 1;  // Decrement 'readerCount'

    // If this is the last reader, release 'writers' to allow writers to access the shared resource.
    if (readerCount == 0) { V(writers); }

    V(readers);   // Release 'readers', allowing other readers to update 'readerCount'.
  }
}

process Writer() {
  while (true) {
    P(writers); // entry-protocol: Acquire 'writers' to block all readers and other writers.

    // CRITICAL SECTION for Writer (Writing to the shared resource)
    write();

    V(writers); // exit-protocol: Release 'writers' to allow others to read/write
  }
}
```

## Exercise 8: Multi-country

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
// Semaphores for synchronization
sem vaxMutex = 1;
sem unvaxMutex = 1;
sem doc_check_zone = 1;

// Counters to track the number of passengers currently in the Documents Checking Zone
int vaxCounter = 0;
int unvaxCounter = 0;

process vaccinated([i = 1 to N]){
    while(true) {
      walk_to_DCZ(); // non-critical section (Mingling Zone: passengers walk towards the Document Checking Zone)
      
      P(vaxMutex); // entry-protocol
        
      // CRITICAL SECTION
      vaxCounter++;  // Increment nr. of vaccinated passengers in the zone
      
      // If this is the first passenger in the zone, lock the zone for un-vaccinated passengers
      if (vaxCounter == 1) { P(doc_check_zone); }
      
      V(vaxMutex); // Release access to "vaxCounter"

      check_docs();

      // exit-protocol
      P(vaxMutex);   // Lock access to "vaxCounter" for synchronization
      vaxCounter--;  // Decrement nr. of vaccinated passengers in the zone

      // If this is the last passenger in the zone, release "doc_checking_zone"
      if (vaxCounter == 0) { V(doc_check_zone); }
      V(vaxMutex); // Release "vaxCounter"
    }
}

process unvaccinated([i = 1 to M]){
    while(true){
      walk_to_DCZ(); // non-critical section  (Mingling Zone: passengers walk towards the Document Checking Zone)
      
      P(unvaxMutex); //entry-protocol

      // CRITICAL SECTION
      unvaxCounter++; // Increment nr. of un-vaccinated passengers in the zone
      
      // If this is the first passenger in the zone, lock it for vaccinated passengers
      if (unvaxCounter == 1){ P(doc_check_zone); }
      V(unvaxMutex); // Release access to "unvaxMutex"
      
      check_docs(); 
      
      // exit-protocol
      P(unvaxMutex); // Acquire lock on "unvaxMutex" 
      unvaxCounter--; // Decrement nr. of un-vaccinated passengers in the zone
      
      // If this is the last passanger in the zone, release mutex for zone
      if (unvaxCounter == 0){ V(doc_check_zone); }
      V(unvaxMutex); // Release "unvaxMutex" 
    }
}
```