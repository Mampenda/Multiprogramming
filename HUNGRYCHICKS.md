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
Semaphore empty = 1;
Semaphore nonempty = 0;
Boolean dishEmpty = new Boolean(true);

// Number of portions currently available to the baby birds
int F = 0;

// Process for Parent Bird
Process Parent {
    while (true) {
        fill_dish();
        print("Parent refilling dish...");
        dish.post(empty);           // or P(empty)
        F += 1;

        if (F != 0) {
            dishEmpty.post(nonempty);   // or V(nonempty);
            dishEmpty = false;
        }
        else {
           dish.post(empty)    // or V(empty)
        }
    }
}

// Process for Baby Birds
Process Baby(i=1..N) {
    while (true) {
        <await (!dishEmpty()) eat(); > //Wait for non-empty dish, then eat one portion
        print("Baby " + i + " eating...");
        dishEmpty.post(empty)    // or V(empty)
        dishEmpty = true;
        sleep(randomTime()); // Sleep for a random duration
    }
}
```