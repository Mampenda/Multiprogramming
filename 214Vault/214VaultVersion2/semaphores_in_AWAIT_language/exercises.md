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
    - come back to eat.
- When the dish becomes empty, the baby bird who empties the dish awakens the parent bird.
    - The parent refills the dish with `F` portions, then
    - wait for the dish to become empty again.
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

## Exercise 4: Sandwich

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

## Exercise 5: Producers and Consumers - Split Binary Semaphores

Make producers/consumers processes that synchronizes with split binary semaphores.
The buffer has only room for one element of data.

## Exercise 6: Producers and Consumers - Bounded Buffers: Resource Counting

Make producers/consumers processes that synchronizes around a buffer.

## Exercise 7: Readers/Writers Problem (without fairness)

The `Readers-Writers Problem` is a classic synchronization problem that involves managing access to a shared resource in
such a way that multiple readers can read the resource concurrently, but writers must have exclusive access to it. The
goal is to ensure that the data integrity of the shared resource is maintained while allowing as many readers as
possible to read at the same time, as long as there are no writers.

    Problem Definition

    Readers:  Multiple readers can read the shared resource simultaneously because reading does not alter the state of 
              the resource.
    Writers:  Writers need exclusive access to the shared resource because writing involves modifying it, which could 
              conflict with a read or write operation

Could you solve the Readers/Writers exclusion problem? The solution does not need to be fair, only mutually exclusive.
Remember that readers can read at the same time, but writers have to be alone in accessing the shared variable.
The solution is supposed to be unfair. (**Hint**:You only need one counter and two semaphores.)

## Exercise 8: Readers/Writers Problem (with fairness)

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
fill in the missing parts. Your solution should support fairness between readers and writers.
