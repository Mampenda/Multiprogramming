# Exercises
## Question 1:
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
- What are the possible final values for `x` and `y`? Explain how you got those values.

### Answer:
This program satisfies the at-most-once property as one atomic action, indicated by the angle brackets `<>`,
`< x = x+y >` reads and updates `x` (critical reference = `x`), while the other atomic action `< y = x*y >` reads `x`.
The same can be said the other way around about `y`, one atomic action, indicated by the angle brackets `<>`,  
`< y = x*y >` reads and updates `y` (critical reference = `y`), while the other atomic action `< x = x+y >` reads `y`.
The statements does not both read and update the same variable.

Since these actions are atomic, they have to be executed (and finished) without any other commands being executed, so
the possible values for this program are the following two:

1. `< x = x+y >` is executed first, then `< y = x*y >` will be executed after: x = 3+2 = 5 and y = 5*3 = 15.
2. `< y = x*y >`is executed first, then `< x = x+y >`  will be executed after: y = 2*3 = 6 and x = 2+6 = 8.

## Question 2:
Consider the following program:
```
a = 4
b = 6
co
    < a = a + b >
    ||
    < b = a - b >
oc
```
- What are the possible final values for `a` and `b`? Explain how you got those values.

### Answer:
This program satisfies the at-most-property as one atomic action, indicated by angle brackets `<>`, `< a = a+b >` reads
and updates `a`, (critical reference `a`), while the other atomic action, indicated by angle brackets `<>`,
`< b = a-b >` reads and updates `b`, (critical reference `b`). The statements does not both read and update the same 
variable.

The possible final values for `a` and `b` are:
1. `< a = a+b >` executes first, then `< b = a-b >` executes: a = 4+6 = 10 and b = 10-6 = 4.
2. `< b = a-b >` executes first, then `< a = a+b >` executes: b = 4-6 = -2 and a = 4+(-2) = 2.


## Question 3:
Consider the following program:
```
int x = 1;
int y = 3;
co 
    < x = x * y > 
    || 
    x = x + y
oc
```
Does the program meet the requirements of the At-Most-Once-Property? Explain
your answer. What are the possible final values of `x` and `y`? Explain your answer.

### Answer:
This program does not satisfy the at-most-property, as both commands reads and updates `x` (has a critical reference to
`x`) and only one of them is atomic, indicated by angle brackets `<>`. In other word both statements read and update the
same critical reference (`x`), and the second statement `x = x + y` is non-atomic, meaning it can be interrupted in its
execution (interleavings).

The possible final values are:
1. `< x = x*y >` executes first, then `x = x+y` executes: x = 1*3 = 3 => x = 3+3 = 6,  y = 3 (unchanged)
2. `x = x+y` executes first, then `< x = x*y >` executes: x = 1+3 = 4 => x = 4*3 = 12, y = 3 (unchanged)
3. `x = x+y` reads `x`, `< x = x*y >` executes, then `x = x+y` finishes executing:
   x = 1 (when the non-atomic statement reads `x`)
   x = 1*3 = 3 ( the atomic action executes )
   x = 1+3 = 4
   Resulting in the final result: x = 4, y = 3.

## Question 4:
Consider the following program:

```
int x = 2;
int y = 4;
co
    < x = x + y >
    ||
    < y = y - x >
oc
```
Does the program satisfy the at-most-once property? Explain your reasoning.
What are the possible final values of `x` and `y`?

### Answer:
This program satisfies the at-most-property because both statements are atomic, indicated by angle brackets `<>`, so
there's no chance of interleavings (that they interrupt each other). Also, the first statement `< x = x+y >` reads and
updates `x` (critical reference `x`), while the second statement `< y = y-x >` reads and updates `y` (critical reference
`y`). The statements does not both read and update the same variable.

The possible values for `x` and `y` are:
1. `< x = x+y >` executes first, then `< y = y-x >` executes: x = 2+4 = 6, y = 4-6 = -2
2. `< y = y-x >` executes first, then `< x = x+y >` executes: y = 4-2 = 2, x = 2+2 = 2

## Question 5:
```
int a = 5;
int b = 10;
int c = 3;
co
    < a = a + b >
    ||
    < c = a * c >
oc
```
Does the program satisfy the at-most-once property? Explain your reasoning.
What are the possible final values of `a`, `b`, and `c`?

### Answer:
This program satisfies the at-most-property, because both atomic actions, indicated by angle brackets `<>` are reading
and updating different variables. The first command `< a = a+b >`, reads and updates `a` and reads `b`, while the
second statement `< c = a*c >`, reads and updates `c` and reads `a`. Both statements read _and_ update different
variables.

The possible final values are:
1. `< a = a+b >` executes first, then `< c = a*c >` executes: a = 5+10 = 15, b = 10, c = 15*3 = 45
2. `< c = a*c >` executes first, then `< a = a+b >` executes: c = 5*3  = 15, b = 10, a = 5+10 = 15 

## Question 6:
```
int x = 7;
int y = 2;
co
    x = x + y
    ||
    < y = x - y >
oc
```
Does the program satisfy the at-most-once property? Explain your reasoning.
What are the possible final values of `x` and `y`?

### Answer:
This program does not satisfy the at-most-property as one of the statements is not atomic, which could lead to
interleavings. While the first non-atomic statement has read `y` and is in the process of executing `x = x+y`, the
second atomic statement could execute `< y = x-y >` and thus updating `y` before the first statement has finished.

The possible values are therefore:
1. `x = x+y` executes first, then `< y = x-y >` executes: x = 7+2 = 9, y = 9-2 = 7.
2. `< y = x-y >` executes first, then `x = x+y` executes: y = 7-2 = 5, x = 7+5 = 12.
3. `x = x+y` reads `y`, then `< y = x-y >` executes, then `x = x+y` finishes executing:
   x = x+y = 7+2
   y = x-y = 7-2 = 5
   x = 9 (instead of 7+5 = 12) 