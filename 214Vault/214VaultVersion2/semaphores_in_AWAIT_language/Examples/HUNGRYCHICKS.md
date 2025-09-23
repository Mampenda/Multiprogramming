## Exercise: Hungry Chicks

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

### Answer:

```
sem empty = 0;
sem non-empty = 1; 

// number of portions in the dish
int portions = F;

process Babybirds[i=1 to N]{
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

process Mamabird{
    while(true){
        wait();     // non-critical section
        P(empty);   // entry-protocol
        
        portions = F; // critical section
        
        V(non-empty) // exit-protocol
    }
}
```