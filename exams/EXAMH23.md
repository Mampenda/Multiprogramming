# INF214 EXAM H23
## Question 1:
Consider the following program:
```java
int x = 1;
int y = 3;
co < x = x * y; > || x = x + y; oc
```
Does the program meet the requirements of the At-Most-Once-Property? Explain
your answer. What are the possible final values of x and y? Explain your answer.

## Answer: 
The first statement in the `co oc`-block is an atomic action (surrounded by angle brackets `<>`), but the second is not.
The program meet the requirements of the at-most-once property since both processes only modify one variable (`x`). 
The possible final values for `x` and `y` are: 
1. `x = x*y` executes first => `x` = 6, `y` = 3
2. `x = x+y` executes first => `x` = 12, `y` = 3
3. The second process reads the `x` value before the first process has finished executing: `x` = 3, `y`= 3

## Question 2: 
Consider the following program written in the AWAIT language:
```java
bool should_continue = true;
bool can_proceed = false;
co
while (should_continue) {
    can_proceed = true;
    can_proceed = false;
}
||
<await (can_proceed) should_continue = false;>
oc
```

Which of the following statements hold for this program?