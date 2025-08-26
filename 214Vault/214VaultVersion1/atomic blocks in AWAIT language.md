# INF214 EXAM H22
## Question:
Consider the following program:

``` 
int x = 2;
int y = 3;
co
	< x = x + y >
	||
	< y = x * y>
oc
```

> What are the possible final values for x and y?
> Explain how you got those values.

### Answer:

1. `<x = x+y >` runs first, and then `<y = x*y>` runs. In this case, the result would be `x = 3+2 = 5` and `y = 5*3 = 15`


2. `<y = x*y>` runs first, and then `<x = x+y >` runs. In this case, the result would be `y = 2*3 = 6` and `x = 2+6 = 8`




## Question:
Consider the following program:

```python
a = 4
b = 6
co
    < a = a + b >
    ||
    < b = a - b >
oc

```

What are the possible final values for `a` and `b`? Explain how you got those values.

### Answer:

We can either have that `< a = a + b >` runs and then `< b = a - b >` runs or the other way around since they are both atomic.

Output 1: `a = 4 + 6` => `a = 10` and then `b = 10 - 6` => `b = 4`. Result: `a = 10, b = 4`

Output 2: `b = 4 - 6` => `b = -2` and then `a = 4 + (-2)`. Result: `a = 2, b = -2`





sem full = F;   // how many portions are available
sem empty = 0;  // is the dish empty?
sem mutex = 1;  // mutually exclusive access to the dish

int dish = F;   // food dish with F portions

process parentBird {
// sleep until the dish is empty
P(empty);

    // mutual exclusion on a shared resource (the dish)
    P(mutex);
    // fill the dish
    dish = F;
    // release dish
    V(mutex);
    
    // signal the baby birds that the dish is ready
    // i.e., we now have F portions available
    for(int i = 0; i <= F; i++) V(full);
}

process babyBird[i = 1 to N] {
// wait until the dish has at least 1 portion of food
P(full);

    // mutual exclusion on a shared resource (the dish)
    P(mutex);
    // eat one portion
    dish--;
    if(dish == 0) // dish is empty
        V(empty); // signal the parent bird
    // release dish
    V(mutex);
    
    // go back to sleep
    sleep(rnd);
}
