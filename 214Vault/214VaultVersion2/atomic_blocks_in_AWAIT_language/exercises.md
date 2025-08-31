# Exercises - Atomic Blocks in AWAIT Language
An `atomic block` is a section of code that runs completely without interruption by other threads. This means that once 
an atomic block starts executing, no other operations can interleave with it until it completes. 

**Example in Java:** A method marked `synchronized` or code using `ReentrantLock`'s `lock()`/`unlock()`. 
**Example in Python:** Using `threading.Lock()` to create a critical section.

**Purpose:** Atomic blocks are used to prevent race conditions (the outcome of program when multiple threads access the 
same data depends on the non-deterministic timing or order in which these threads execute.) and ensure data integrity 
and consistency when multiple threads access shared resources. 

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
- What are the possible final values for `x` and `y`? Explain your reasoning.

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
- What are the possible final values for `a` and `b`?  Explain your reasoning.

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
your answer. What are the possible final values of `x` and `y`?  Explain your reasoning.

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
Does the program satisfy the `at-most-once property`? Explain your reasoning.
What are the possible final values of `a`, `b`, and `c`?

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