# Practice exercises for Semaphores in AWAIT Language

## Exercise 1: Bears and Honey
Here is an exercise of sudo-code in _Await Language_ with semaphores for synchronization:

- We have `N` honey bees and a hungry bear.
- They share a pot of honey.
- The pot is initially empty.
- Its capacity is `H` portions of honey.
- The bear sleeps until the pot is full, then eats all the honey and goes back to sleep.
- Each bee repeatedly gathers one portion of honey and puts it in the pot.
- The bee who fills the pot (i.e. puts in the `H`'th portion) awakens the bear.

## Exercise 2: Hungry Chicks
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

## Exercise 3: Gløgg
Here is an exercise of sudo-code in _Await Language_ with semaphores for synchronization:

- Three persons, who like gløgg very much, have gathered to play the following game in a bar. To drink a portion, each 
   of them obviously needs three ingredients: the gløgg, a mug, and almonds. 
- One player has the gløgg, the second has mugs, and the third has the almonds. Assume each of the players has an 
  unlimited supply of their ingredients, respectively. 
- The barista, who also has an unlimited supply of the ingredients, puts two random ingredients on the table. 
- The player who has the third ingredient picks up the other two, makes the drink, and then drinks it. 
- The barista waits for the player to finish. 
- This "cycle" then repeats forever.

"Simulate" this behaviour in the AWAIT language. Represent the players and the barista as processes. Use semaphores for
synchronization. Make sure that your solution avoids deadlock.

## Exercise 4: Producers and Consumers - Split Binary Semaphores
Make producers/consumers processes that synchronizes with split binary semaphores.
The buffer has only room for one element of data.

## Exercise 5: Producers and Consumers - Bounded Buffers: Resource Counting
Make producers/consumers processes that synchronizes around a buffer.

## Exercise 6: Readers/Writers as an exclusion problem
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
The solution is supposed to be unfair. (**Hint**:You only need one counter and two semaphores.)

## Exercise 7: Readers/Writers as an 
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

In the implementation below, `readers` is the number of readers, and `writers` is the number of writers; 
both of them are initially 0. Each variable is incremented in the appropriate request procedure and decremented in the 
appropriate release procedure. A software developer has started on the implementation of this monitor. Your task is to 
fill in the missing parts. Your solution does not need to arbitrate between readers and writers.

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

## Exercise 9: Dining Philosophers
In short, the dining philosophers problem is about four philosophers that are seated around a table with five plates
and five utensils available. For simplicity, we'll use chopsticks.

Each philosopher needs two chopsticks to be able to eat, so there's at most two philosophers that can eat at the same
time. After a philosopher has finished eating, she'll put down both chopsticks, making them available for the other
philosophers.

Solve the Dining Philosophers Problem with the philosophers as processes and all the forks as semaphores.

## Exercise 11: Traffic-light
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
sem driveAllowance = 1;
sem roadACounterMutex = 1; 
sem roadBCounterMutex = 1;

int roadACounter = 0;
int roadBCounter = 0;

int MAX_CARS_AllOWANCE = 5;

process RoadCarA([i = 1 to A]){
    while(true){
        P(roadACounterMutex);
        roadACounter++;
        if(roadACounter == 1){
            P(driveAllowance); //green light
        }
        V(roadACounterMutex);
        //cars drive

        P(roadACounterMutex);
        roadACounter--;
        if(roadACounter == 0 || roadACounter >= MAX_CARS_AllOWANCE){
            V(driveAllowance); //red light
        }
        V(roadACounterMutex);
    }
}

process RoadCarB([i = 1 to B]){
    while(true){
        P(roadBCounterMutex);
        roadBCounter++++;
        if(roadBCounter == 1){
            P(driveAllowance); // green light
        }
        V(roadBCounterMutex); 
        //Cars drive
        P(roadBCounterMutex);
        roadBCounter--;
        if(roadBCounter == 0 || roadBCounter >= MAX_CARS_AllOWANCE){
            V(driveAllowance); //redLight
        }
        V(roadBCounterMutex);
    }
}
```