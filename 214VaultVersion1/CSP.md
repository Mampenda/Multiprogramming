### Notes:

Output:  
process A { ...B!e;... } //betyr å outpute e til B

Input:
process B { ...A?x;... } //betyr å få inn input fra A og lagre det som x

General form:
Destination!port(e_1, ..., e_n)
Source?port(x_1, ..., x_n)  // if we write Source[ * ] it means "read from any process in an array of processes"

Example copy process:
```
process Copy{
	char c;
	do true ->
		West?c;
		East!c;
	od
}
```

do od is a guarded command ^


B;  C -> S; // B is a boolean, C is a communication statement, S is a statement list

B and C is the guard. The guard succeeds if B is true and C is executed with no delay, no delay means that another process is waiting with a matching statement.

The guard fails if the B is false, and it blocks if B is true but C cant be executed yet.


Example:
if B1; C1 -> S1;
[ ] B2; C2 -> S2;
fi

If both B1 and B2 fails the if terminates with doing nothing, but if either one succeds  you can choose it nondetermistically. 


Example of copy with the above syntax:

```java
process Copy{
	char c;
	do West?c -> East!c; od
}
```

```java
process Copy{
	char c1, c2;
	 West?c1; 
	 do West?c2 -> East!c1; c1 = c2;
	 [] East!c1 -> West?c1;
	 od
}
```

Bounded buffer example:

```java
process Copy{
	char buffer[10];
	int front = 0; rear = 0; count = 0;

	do count < 10; West?buffer[rear] -> count++; rear = (rear + 1) mod 10; 
	[] count > 0; East!buffer[front] -> count--; front = (front + 1) mod 10;
	od
	
}

```


Resource allocator with CSP:

```java
process Allocator{
	int avail = MAXUNITS;
	set units = //initial values;
	int index, unitID;

	do avail > 0; Client[*]?acquire(index) -> 
		avail--; remove(units, unitID); Client[index]!reply(unitID);
	[] Client[*]?release(index, unitId) -> 
		avail++; insert(units, unitID);
	od
}
```



# Tasks:
### Question:

Could you write the Synchronous symmetric exchange with CSP?

Here you should create two processes with each two variables that will be exchanged in a if block.

### Answer:

```java
process P1 {
	int value1 = 1;
	int value2;

	if P2!value1 -> P2?value2;
	[] P2?value2 -> P2!value1
}

process P2 {
	int value1;
	int value2 = 1;

	if P1!value2 -> P1?value1;
	[] P1?value1 -> P1!value2;
}
```
### The sieve of Eratosthenes (HARD TASK)
### Question:
Can you write the sieve of Eratosthenes in CSP? This CSP program will find all the prime numbers.

### Answer:

```java
process Sieve[1]{
	int p = 2;
	for [i = 3 to n step 2] Sieve[2]!i;
}

process Sieve[i = 2 to L] {
	int p; nextM
	Sieve[i-1]?p;

	do
		Sieve[i-1]?next -> 
				if 
					(next mod p) != 0 -> Sieve[i+1]!next;
				fi
	od
}
```


### GCD
### Question:
Could you write a CSP for Greatest Common Divisor?

### Answer:

```java
process GCD{
	int id, x, y;
	do true ->
		Client[*]?args(id, x, y);
		do x > y -> x = x - y;
		[] x < y -> y = y - x;
		od
		Client[id]!result(x)
	od
}
```


### From quiz:

### Question:
What does notation _**Source[*]**_ mean?

### Answer:
It means that the source will be read from any process in an array of processes


### Question:
Using CSP notation, write down "input character _**c**_ from _**north**_".

Please only use small letters in your answer, and please do not use any space symbols.

### Answer:
north?c

### Question:
Consider the following program:

```java

process Copy {  
  
   char c1, c2;  
  
   West?c1;  
  
   do West?c2 -> East!c1; c1 = c2;  
  
   [] East!c1 -> West?c1;  
  
   od 
  
}
```

What happens when the both arms of the **do** statement succeed?

### Answer:
Either of the arms can be chosen; this happens nondeterministically





### Last exam task:
### Question: 
Using Communicating Sequential Processes, define a process Copy that copies a character from process Vestland to process Bergen.

### Answer:

```java
process Copy{
	char c;
	do Vestland?c -> Bergen!c; od
}
```


