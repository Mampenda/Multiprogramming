# Communicating Sequential Processes (CSP)

## Basic Communication

In CSP, an exclamation mark, `!`, is for sending data to a process, and a question mark `?`, is for receiving data from
a process.

The following code **outputs (sends)** data stored in `o` to process `B`:

```
process A { 
    B!o; 
}
```

The following code **inputs (receives)** from process `D` and stores it in `i`:

```
process C { 
    D?i; 
}
```

The **general form** for (one or more processes?) receiving from multiple processes is given by

```
Destination!port(e1, ..., 1n)
Departure?port(x1, ..., xn)
```

## Guarded Commands

### Syntax

In the following code, the guard `B` is a boolean condition that which when succeeding (returns `true`) will allow `C`
to execute immediately, and blocks communication `C` in case of failure:

```js 
B;
C => S;
````

### Conditional

In the following program, one of the guards succeeding will result in one of the communications to be chosen
nondeterministically, if both guards fail, then nothing happens.

```
if
B1;
C1 => S1;
[]
B2;
C2 => S2;
fi
```

### Loop

The following program repeats as long as at least one guard succeeds

```
do 
    B;
    C => S;
od
```

### Simple Examples

```
// Copy process
process Copy {
  char c;
  do West?c -> East!c; od
}
```

```
// Copy process with two inputs 
process Copy {
  char c1, c2;
  West?c1;
  do West?c2 -> East!c1; c1 = c2;
  [] East!c1 -> West?c1;
  od
}
```

```
// Bounded buffer
process Buffer {
    char buffer[10];
    int front = 0, rear = 0, count = 0;

    do 
       count < 10; West?buffer[rear] -> count++; rear = (rear + 1) mod 10;
    [] count > 0; East!buffer[front] -> count--; front = (front + 1) mod 10;
    od
}
```

```
// Resource allocator
process Allocator {
    int avail = MAXUNITS;
    set units = {initial values};
    int index, unitID;

    do 
        avail > 0; Client[*]?acquire(index) -> 
        avail--; remove(units, unitID);
        
        Client[index]!reply(unitID);
        
     [] Client[*]?release(index, unitID) -> 
        avail++; insert(units, unitID);
    od
}

```

### Practice Tasks

#### Symmetric Exchange

The following code has two processes exchanging values nondeterministically.

```
process P1 {
int value1 = 1, value2;

if P2!value1 -> P2?value2;
[] P2?value2 -> P2!value1;
}

process P2 {
int value1, value2 = 1;

if P1!value2 -> P1?value1;
[] P1?value1 -> P1!value2;
}

Using JavaScript, give an example of how `yield` can receive data
when a coroutine is resumed.
Hint 1:
Start by declaring a generator.
Hint 2:
Think how many calls of `next` you need.
```

## Exercises

### Exercise 1

Using Modern CSP, specify the behaviour of a traffic light that repeatedly turns green, then yellow, and
then red.

#### Answer

`Light = green -> yellow -> red -> Light`

### Exercise 2

Using Communicating Sequential Processes, define a process Copy that copies a character from
process Bergen to process Vestland.

#### Answer

```
Process Copy {
    Char c;
    True ->
    do Bergen?c -> Vestland!c od
}
```

### Exercise

Why does the following `JavaScript` code print? Explain your answer.

```
function * g(x) {
    let r = x * (yield -1) + (yield "blabla") + (yield false); 
    console.log(r)
}
let p = g(3);
p.next(32);
p.next(4);
p.next(5);
p.next(6);
p.next(7);
```

#### Answer


