## Exercise: Bears and Honey
Here is an exercise of sudo-code in _Await Language_ with semaphores for synchronization:

- We have `N` honey bees and a hungry bear.
- They share a pot of honey.
- The pot is initially empty.
- Its capacity is `H` portions of honey.
- The bear sleeps until the pot is full, then eats all the honey and goes back to sleep.
- Each bee repeatedly gathers one portion of honey and puts it in the pot.
- The bee who fills the pot (i.e. puts in the `H`'th portion) awakens the bear.

### Answer: 
```
sem empty = 1;
sem full = 0;

// Number of portions in the pot currently available to the bear
int portions = 0;

process Bees[i=1 to N]{
	while(true) {
		collect_honey();
		P(empty);
		portions = portions + 1;

		if (portions == H){
			V(full);
		}
		else {
			V(empty);
		}
	}
}

process Bear {
	while(true){
		P(full);
		eat_honey();
		portions = 0;
		V(empty);
	}
}
```

