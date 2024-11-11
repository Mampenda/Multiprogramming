Here is an example of sudo-code in _Await Language_ with semaphores for synchronization to represent the following problem:

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

```
// Initialize semaphores for the states of the dish
Semaphore eat = 1;
Semaphore fill = 0;
int portions = 100;

// Process for Parent Bird
Process Parent {
    while (true) {
        P(0)
        fill_dish();
        print("Parent refilling dish...");

        V(1)
    }
}

// Process for Baby Birds
Process Baby(i=1..N) {
    while (true) {
        P(1);
        eat_food();
        portions -= 1;
        
        if (portions == 0){
          V(0);
        } else {
          V(1);
        }    
    }
}
```