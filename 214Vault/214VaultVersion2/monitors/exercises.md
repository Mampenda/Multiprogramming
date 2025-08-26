# Exercises - Monitors

## Question 1 - Readers/Writers (another variant)
Consider the following variant of the Readers/Writers problem:
Reader processes query a database and writer processes examine and modify it. Readers may access the database
concurrently, but writers require exclusive access. Although the database is shared, we cannot encapsulate it by a
monitor, because readers could not then access it concurrently since all code within a monitor executes with mutual
exclusion.

Instead, we use a monitor merely to arbitrate access to the database. The database itself is global to the readers and
writers. The arbitration monitor grants permission to access the database. To do so, it requires that processes inform
it when they want access and when they have finished. There are two kinds of processes and two actions per process, so
the monitor has four procedures: `request_read`, `request_write`, `release_read`, `release_write`. These procedures are
used in the obvious ways.

For example, a reader calls `request_read` before reading the database and calls `release_read` after reading the
database. To synchronize access to the database, we need to record how many processes are reading and how many processes
are writing.

In the implementation below, `readers` is the number of readers, and `writers` is the number of writers; both of them 
are initially 0. Each variable is incremented in the appropriate request procedure and decremented in the appropriate
release procedure. A software developer has started on the implementation of this monitor. Your task is to fill
in the missing parts. Your solution does not need to arbitrate between readers and writers.

## Question 2 - Bank account:
A savings account is shared by several people (processes). Each person may deposit or withdraw funds from the account.
The current balance in the account is the sum of all deposits to date minus the sum of all withdrawals to date. The
balance must never become negative. A deposit never has to delay (except for mutual exclusion), but a withdrawal has to
wait until there are sufficient funds. A junior software developer was asked to implement a monitor to solve this
problem, using Signal-and-Continue discipline. Here is the code the junior developer has written so far:
```java
monitor Account(){
    int balance = 0;
    cond sufficient_funds;
    
    procedure deposit(int amount) { balance = balance + amount; }
    procedure withdraw(int amount) { balance = balance - amount; }
}
```
This solution is incorrect. Help the junior developer implement the monitor correctly.
