Last year exam task:
### Question:
A semaphore is a program variable that holds an integer value. It can be manipulated only by the operations P and V. 
Describe the semantics of these operations.

### Answer:
P(s) == <await (s>0) s = s - 1;>
V(s) == <s = s + 1;>

# Last year exam task:
## Question:
Three persons, who like gløgg very much, have gathered to play the following game in a bar.
To drink a portion of gløgg, each of them obviously needs three "ingredients": the gløgg(liquid in the decanter 
— see image), a mug, and almonds. One Player has the gløgg (liquid in the decanter), the second Player has mugs, and the
third has almonds. Assume that each of the players has an unlimited supply of these ingredients (i.e.,gløgg in the 
decanter, mugs, almonds), respectively.

The barista, who also has an unlimited supply of the ingredients, puts two random ingredients on the table. The Player 
who has the third ingredient picks up the other two, makes the drink (i.e., pours the gløgg liquid from the decanter 
into a mug and adds almonds), and then drinks it.
The barista Waits for the Player to finish. This "cycle" is then repeated.

"Simulate" this behaviour in the AWAIT language.
Represent the players and the barista as processes.
Use semaphores for synchronization.
Make sure that your solution avoids deadlock.

### Answer:

```java
// player1 has gløgg, player2 has mugs, player3 has almonds
// barista puts two random ingridients on the table

sem gløgg = 0;
sem mugs = 0;
sem almonds = 0;
sem barista = 1;

process Barista{
	while(true){
		P(barista);
		// puts two random ingridients on the table
		randomMissingIngridient = randomInt(0,2) // the random missing ingridient
		if (randomMissingIngridient == 0){
			V(gløgg)
		}
		if (randomMissingIngridient == 1){
			V(mugs)
		}
		if (randomMissingIngridient == 2){
			V(almonds)
		}
	}
}


process Player1{
	while(true){
		P(gløgg);
		makeGløgg(); // makes gløgg and drinks it 
		drinkGløgg();
		V(barista);
	}
}


process Player2{
	while(true){
		P(mugs);
		makeGløgg(); // makes gløgg and drinks it 
		drinkGløgg();
		V(barista);
	}
}

process Player3{
	while(true){
		P(almonds);
		makeGløgg(); // makes gløgg and drinks it 
		drinkGløgg();
		V(barista);
	}
}

```

## Producers/Consumers split binary semaphores
### Question:

Could you make producers/consumers processes that synchronizes with split binary semaphores? The buffer has only room 
for one element of data.

### Answer:

```java
typeT buf;
sem empty = 1; sem full = 0;

process Producer[i=1 to M] {
	while(true) {
		P(empty);
		buf = data;
		V(full);
	}
}

process Consumer[j=1 to N] {
	while(true) {
		P(full);
		data = buf;
		V(empty);
	}
}
```

## Bounded Buffers: Resource Counting
### Question:

Could you make producers/consumers processes that synchronizes around a buffer?

#### Hint:
```java
typeT buf[n];
int front = 0; int rear = 0;
sem empty = n; sem full = 0;
sem mutexD = 1; sem mutexf = 1;
```

### Answer:
```java
typeT buf[n];
int front = 0; int rear = 0;
sem empty = n; sem full = 0;
sem mutexD = 1; sem mutexf = 1;

process Producer[i=1 to M] {
	while(true) {
		P(empty);
		P(mutexD);
		buf[rear] = data; rear = (rear + 1) mod n;
		V(mutexD);
		V(full);
	}
}
process Consumer[j=1 to N] {
	while(true) {
		//fetch message result and consume it;
		P(full);
		P(mutexF);
		result = buf[front]; front = (front+1) mod n;
		V(mutexF);
		V(empty);
	}
}
```

## Dining Philosophers Problem
### Question:
Could you solve the Dining Philosophers Problem with the philosophers as processes, initialize all the forks as 
semaphores.

#### Hint:
sem fork[5] = {1, 1, 1, 1, 1}
process philosopher[4]


### Answer:

```java
sem fork[5] = {1, 1, 1, 1, 1}

process philosopher[i=0 to 3] {
	while(true){
		P(fork[i]); P(fork[i+1]);
		eat;
		V(fork[i]); V(fork[i+1])
		think;
	}
}


process philosopher[4] {
	while(true){
	P(fork[0]); P(fork[4]);
	eat;
	V(fork[0]); V(fork[4]);
	think;
	}
	
}
```


### Question:
Given are N honeybees and a hungry bear. They share a pot of honey. The pot is initially empty; its capacity is H 
portions of honey. The bear sleeps until the pot is full, then eats all the honey and goes back to sleep. Each bee 
repeatedly gathers one portion of honey and puts it in the pot; the bee who fills the pot awakens the bear. Represent 
the bear and honeybees as processes and develop code in the Await Language that simulates their actions. Use semaphores
for synchronization. 

### Answer:
```java
sem e = 1;
sem eat = 0;

int portions = 0;

process Bees[i=1 to N]{
	while(true){
		collectHoney();
		P(e);
		portions = portions + 1;
		if (portions == H){
			V(eat);
		}
		else{
			V(e);
		}
	}
}

process Bear{
	while(true){
		P(eat);
		eat_honey();
		portions = 0;
		V(e);
	}
}
```

## Readers/Writers as an exclusion problem

### Question:

Could you solve the Readers/Writers exclusion problem? The solution does not need to be fair only mutual exclusive. 
Remember that readers can read at the same time, but writers have to be alone in accessing the shared variable. 
The solution is supposed to be unfair.

### Hint:
You only need one counter and two semaphores.


### Answer:

```java
sem rw = 1;
sem mutexR = 1; // lock for reader acces to nr
int nr = 0;


process Reader[i=1 to M]{
	while(true){
		P(mutexR);
		nr = nr + 1
		if (nr==1) P(rw); //if first reader, get lock
		V(mutexR);
		read();//read the database
		P(mutexR);
		nr = nr - 1;
		if (nr==0) V(rw); //if the last reader, then release lock
		V(mutexR)
	}
}

process Writer[j=1 to N]{
	while(true){
		P(rw);
		write();//write to database
		V(rw);
		
	}
}
```

### Readers/Writers passing the baton (Hard task)
### Question:

Can you write readers and writers as processes and solve the synchronization with "passing the baton" method? This is a much more fair solution than the previous task.

### Hint:
int nr = 0; int nw = 0;
sem e = 1; sem r = 0; sem w = 0;
int dr = 0; int dw = 0;

### Answer:

```java
int nr = 0; int nw = 0;
sem e = 1; sem r = 0; sem w = 0;
int dr = 0; int dw = 0;

process Reader[i = 1 to M] {
	while(true){
		P(e);
		if(nw > 0) {dr=dr+1; V(e); P(r); }
		nr = nr + 1;
		SIGNAL;
		//read the database
		P(e);
		nr = nr - 1;
		SIGNAL;
	}
}

process Writer[j = 1 to N]{
	while(true){
		P(e);
		if (nr > 0 or nw > 0) {dw=dw+1; V(e); P(w);}
		nw = nw + 1;
		SIGNAL;
		//write to database
		P(e);
		nw = nw - 1;
		SIGNAL;
	}
}

SIGNAL:
	if (nw == 0 and dr > 0) {
		dr = dr -1;
		V(r);   //awaken a reader, or
	}
	else if (nr == 0 and nw == 0 and dw > 0) {
		dw = dw - 1;
		V(w);   //release a writer, or
	}
	else{
		V(e);   //release the
	}
```

## QUIZ:

### Question:
Is this true or false: "The value of a semaphore is a positive integer (i.e., always greater than zero)"?

### Answer:
False

### Question:
Is this true or false: "The V operation is used to signal the occurrence of an event"?

### Answer:
True

### Question:
Is this true or false: "The P operation is used to delay a process until an event is occurred"?

### Answer:
True

### Question:
Is this true or false: "The V operation increments the value of a semaphore"?

### Answer:
True

### Question:
Is this true or false: "The P operation waits until the value of a semaphore is positive and then decrements the value"?

### Answer:
True

### Question:
Consider the following statement that declares a semaphore: **sem** s;
What will be the "default" value of _**s**_?

### Answer:
0

### Question:
**Complete the following statement:**

"Semaphores make it relatively easy to implement barrier synchronization. The basic idea is to use one semaphore for 
each synchronization flag. A process set a flag by executing a BLANK operation."

### Answer:
V

### Question:
**Complete the following statement:** _(continuing the previous statement)_

"A process waits for a flag to be set and then clears it by executing a BLANK operation."

### Answer:
P

### Question:
Why is it necessary that the execution of **_deposit_** and **_fetch_** alternates?

### Answer: 
To achieve mutual exclusion on the critical sections.




