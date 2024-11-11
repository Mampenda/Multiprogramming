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
- This "cycle" then repeats.

"Simulate" this behaviour in the AWAIT language. Represent the players and the barista as processes. Use semaphores for
synchronization. Make sure that your solution avoids deadlock.

## Exercise 4: Dining Philosophers
In short, the dining philosophers problem is about five philosophers that are seated around a table with five plates 
and five utensils available. For simplicity, we'll use chopsticks. 

Each philosopher needs two chopsticks to be able to eat, so there's at most two philosophers that can eat at the same 
time. After a philosopher has finished eating, she'll put down both chopsticks, making them available for the other 
philosophers.

Solve the Dining Philosophers Problem with the philosophers as processes and all the forks as semaphores.

## Exercise 5: Producers and Consumers - Split Binary Semaphores
Make producers/consumers processes that synchronizes with split binary semaphores.
The buffer has only room for one element of data.

## Exercise 6: Producers and Consumers - Bounded Buffers: Resource Counting
Make producers/consumers processes that synchronizes around a buffer.

## Exercise 7: Readers/Writers as an exclusion problem 

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


