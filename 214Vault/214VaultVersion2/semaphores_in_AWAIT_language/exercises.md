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

## Exercise 6: Producers and Consumers - Split Binary Semaphores
Make producers/consumers processes that synchronizes with split binary semaphores.
The buffer has only room for one element of data.

## Exercise 7: Producers and Consumers - Bounded Buffers: Resource Counting
Make producers/consumers processes that synchronizes around a buffer.

## Exercise 8: Readers/Writers as an exclusion problem
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

## Exercise 9: Readers/Writers as an 
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

## Exercise 10: Multi-country

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

## Exercise 11: Dining Philosophers
In short, the dining philosophers problem is about four philosophers that are seated around a table with five plates
and five utensils available. For simplicity, we'll use chopsticks.

Each philosopher needs two chopsticks to be able to eat, so there's at most two philosophers that can eat at the same
time. After a philosopher has finished eating, she'll put down both chopsticks, making them available for the other
philosophers.

Solve the Dining Philosophers Problem with the philosophers as processes and all the forks as semaphores.

## Exercise 12: Traffic-light
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


## Exercise 13: Artists Problem (in C)
There are three artists and one supplier of art materials. Each artist has an unlimited supply of one type of material (Paint, Brushes, or Canvas) but needs the other two materials to complete a painting. The supplier has an unlimited supply of all materials and places two materials on a shared table at random. The artist who has the missing material can then take the two materials they need to complete a painting.

The problem requires synchronization between threads to ensure that only one artist can access the table at a time, and that the supplier only produces materials when an artist is ready to take them. Semaphores are used to control access to the shared table and coordinate the actions of the supplier and the artists.

- `has_paintings_left()` checks whether a given artist still has paintings left to complete.
- `rand_artist()` chooses a random artist to supply based on who still has paintings left.
- `supplierThdFunc()` represents the supplier thread, which produces a pair of materials and signals the corresponding artist.
- `artistThdFunc()` represents an artist thread, which waits for the needed materials, completes a painting, and signals the supplier that the table is free.

The `main()` function initializes the semaphores, creates the threads for the supplier and artists, waits for all threads to finish, and then cleans up the semaphores. The program terminates when all artists have completed the number of paintings specified by the user.


```c
#define _DEFAULT_SOURCE

#include <semaphore.h>  // sem_t, sem_wait(), sem_post(), sem_init()
#include <pthread.h>    // pthread_t, pthread_create(), pthread_join()
#include <stdbool.h>    // bool, true, false
#include <stdlib.h>     // atoi(), rand(), srand(), exit()
#include <string.h>     // strcmp()
#include <unistd.h>     // usleep()
#include <stdio.h>      // printf()
#include <time.h>       // time()

// =====================
// Global Variables
// =====================

sem_t artTable;       //< shared art table where supplier produces supplies
sem_t supplier;       //< art supplier who provides art supplies
sem_t artist[3];      //< three artists
int paintingsLeft[3]; //< maximum number of paintings each artist can make

// =====================
// Utility Functions
// =====================

/// ---------------------------------------------------------------------------
/// Checks if an artist still has paintings left to complete.
///
/// @param id The artist identifier.
///
/// @returns 'true' if artist has paintings left; else 'false'.
/// ---------------------------------------------------------------------------
bool has_paintings_left(int id)
{
    return (paintingsLeft[id] == 0) ? false : true;
}

/// ---------------------------------------------------------------------------
/// Generates a random artist to supply based on who still has paintings left.
///
/// @returns ret_val returns the artist ID, or -1 if all are done.
/// ---------------------------------------------------------------------------
int rand_artist()
{
    int ret_val;

    // all artists has paintings left to paint
    if(has_paintings_left(0) && has_paintings_left(1) && has_paintings_left(2)) {
        ret_val = rand() % 3;
    }

    // artist[0] is done; others have paintings left to paint
    else if(!has_paintings_left(0) && has_paintings_left(1) && has_paintings_left(2)) {
        ret_val = (rand() % 2) + 1;
    }

    // artist[1] is done; others have paintings left to paint
    else if(!has_paintings_left(1) && has_paintings_left(0) && has_paintings_left(2)) {
        ret_val = (((rand() % 2) + 1) == 1) ? 0 : 2;
    }

    // artist[2] is done; others have paintings left to paint
    else if(!has_paintings_left(2) && has_paintings_left(0) && has_paintings_left(1)) {
        ret_val = rand() % 2;
    }

    // all artists are done with their paintings
    else if(!has_paintings_left(0) && !has_paintings_left(1) && !has_paintings_left(2)) {
        ret_val = -1;
    }

    // if only one artist has paintings left to paint
    else {
        for(int i = 0; i < 3; i++) {
            if(has_paintings_left(i)) {
                ret_val = i;
            }
        }
    }

    return ret_val;
}

// =====================
// Supplier Thread
// =====================

/// ---------------------------------------------------------------------------
/// Produces a pair of art supplies for the artists.
/// Resources mapping:
///   0 => Paint & Brushes (Canvas missing -> Artist 2)
///   1 => Brushes & Canvas (Paint missing -> Artist 0)
///   2 => Paint & Canvas (Brushes missing -> Artist 1)
///
/// @param *arg A pointer argument to any type.
/// ---------------------------------------------------------------------------
void *supplierThdFunc(void *arg)
{
    int randNum;  // pair-of-ingredients identifier

    while(true) {
        sem_wait(&artTable); // semaphore that locks access to table

        // choose two ingredients to produce
        randNum = rand_artist();

        // if all artists done, stop painting
        if( randNum == -1) {
            break;
        }

        // put chosen supply on the table
        switch(randNum) {
        case 0:
            printf("Supplier produced Paint and Brushes\n");
            break;
        case 1:
            printf("Supplier produced Brushes and Canvas\n");
            break;
        case 2:
            printf("Supplier produced Paint and Canvas\n");
            break;
        }

        sem_post(&artTable);        // allow table access
        sem_post(&artist[randNum]); // signal the corresponding artist
        sem_wait(&supplier);        // wait until artist clears table
    }

    pthread_exit(NULL); // terminate thread
}

// =====================
// Artist Thread
// =====================

/// ---------------------------------------------------------------------------
/// Simulates an artist completing a painting when supplies are available.
///   artist[0] - has Paint, needs Brushes & Canvas
///   artist[1] - has Brushes, needs Paint & Canvas
///   artist[2] - has Canvas, needs Paint & Brushes
///
/// @param *arg A pointer argument to any type.
/// ---------------------------------------------------------------------------
void *artistThdFunc(void *arg)
{
    int artist_id = *(int*)arg;
    printf("Artist %d starts working...\n", artist_id);

    while(has_paintings_left(artist_id))
    {
        sem_wait(&artist[artist_id]);   // wait for ingredient
        sem_wait(&artTable);            // block table access
        usleep((rand() % 1500) * 1000); //paint for a while (0–1500 ms); clear table

        switch(artist_id) {
        case 0: // red
            printf("\033[0;31mArtist %d completed a painting\033[0m\n", artist_id);
            break;
        case 1: // green
            printf("\033[0;32mArtist %d completed a painting\033[0m\n", artist_id);
            break;
        case 2: // blue
            printf("\033[0;34mArtist %d completed a painting\033[0m\n", artist_id);
            break;
        }

        paintingsLeft[artist_id]--; // decrement paintings left
        sem_post(&artTable);        // release table
        sem_post(&supplier);        // notify supplier
    }

    printf("Artist %d has finished all paintings.\n", artist_id);
    free(arg);
    pthread_exit(NULL);
}

// =====================
// Main Function
// =====================

/// -----------------------------------------------------------------------
/// Main entry point for this program.
///
/// @return Exit-code for the process - 0 for success, else an error code.
/// -----------------------------------------------------------------------
int main(int argc, char **argv)
{
  srand(time(0));         // use current time as seed for rand()
  char str[3] = "-p";     // required cmd line option
  pthread_t artistThd[3]; // threads for smoker operations
  pthread_t supplierThd;  // thread for agent operation

  // Validate command line arguments
  if (argc != 3) {
      printf("Error, unexpected number of arguments\n");
      exit(1);
  }

  if(strcmp(argv[1], str) != 0) {
      printf("Error, invalid argument/s\n");
      exit(1);
  }

  if ((atoi(argv[2]) < 1) || (atoi(argv[2]) > 10)) {
      printf("Error: invalid number of paintings\n");
      exit(1);
  }

  // Initialize global variables
  sem_init(&artTable, 0, 1);
  sem_init(&supplier, 0, 0);
  for(int i=0; i<3; i++) {
      sem_init(&artist[i], 0, 0);
      paintingsLeft[i] = atoi(argv[2]);
  }

  // Spawn threads
  pthread_create(&supplierThd, NULL, &supplierThdFunc, NULL);
  for(int i=0; i<3; i++) {
      int *artist_id_ptr = malloc(sizeof(int));
      *artist_id_ptr = i;
      pthread_create(&artistThd[i], NULL, &artistThdFunc, artist_id_ptr);
  }

  // Join threads
  pthread_join(supplierThd, NULL);
  for(int i=0; i<3; i++) {
      pthread_join(artistThd[i], NULL);
  }

  // Destroy all semaphores
  sem_destroy(&artTable);
  sem_destroy(&supplier);
  for(int i=0; i<3; i++) {
      sem_destroy(&artist[i]);
  }

  exit(0);    // Exit program
}

/* EOF */
```

The C code provided below implements the Artists Problem using semaphores and threads. The semaphores used are:
`artTable`: controls access to the shared table where the supplier places materials.
`supplier`: represents the supplier, who can produce a pair of materials when the table is free.
`artist[3]`: one semaphore for each artist, signaling when the corresponding artist can access the materials on the table.

The array `paintingsLeft[3]` keeps track of how many paintings each artist still has to complete. This number is specified by the user via a command-line argument. The supplier thread produces pairs of materials based on which artists still have paintings left. Each artist thread waits for the appropriate materials, completes a painting, updates the `paintingsLeft` count, and signals the supplier to produce the next set of materials. The program terminates when all artists have completed their paintings.