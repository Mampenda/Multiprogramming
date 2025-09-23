# Communicating Sequential Processes (CSP)

CSP follows a guarded command language (GCL) for expression logic.

---

## Brief Explanation

- **Goal:** Model concurrent systems using processes that communicate *only* through message passing.
- **Main ideas:**
    - **Processes:** Independent sequential components.
    - **Channels:** Processes synchronize by *sending* (`!`) and *receiving* (`?`) messages.
    - **Synchronization:** Communication is synchronous — both sender and receiver must be ready.
    - **Guards & Nondeterminism:** Control which actions are possible; if multiple actions are possible, one is chosen
      nondeterministically.
    - **Composability:** Complex systems are built from simple communicating processes.

> Nondeterministic: If multiple choices are available when at least one guard succeeds, the system may pick any of them
> and is it not possible to predict which it'll pick.

--- 

## Basic Communication

In CSP, an exclamation mark, `!`, is for sending, and a question mark `?`, is for receiving data.

```
// The following code outputs (sends) data stored in `o` to process `B`
process A { 
    B!o; 
}
```

```
// The following code **inputs (receives)** from process `D` and stores it in `i`

process C { 
    D?i; 
}
```

The **general form** for of communication is:

```
Destination!channel(e1, ..., en) // Sending n expressions
Source?channel(x1, ..., xn)      // Receiving into n variables
```

- **Destination / Source** → the process we’re communicating with. (like `B` or `D` above).
- **(e1, …, en)** or **(x1, …, xn)** → multiple values can be sent/received at once.
- **channel** → the “channel” or “name of communication slot” between processes. Both sending and receiving refer to the
  same communication channel.

So in the simple example, the “channel” is implicit — we just wrote `B!o` instead of `B!someChannel(o)`.

If we write `Source[*]?channel(x1, ..., xn)`, it means "receive from any process in an array of processes in the
channel."

## Guarded Commands

### Syntax

In the following code, the guard `B` is a boolean condition that which when succeeding (returns `true`) will allow `C`
to execute immediately, and blocks communication `C` in case of failure:

```js 
B;
C => S;
````

### Conditional (if)

In the following pseudocode program, one of the guards succeeding will result in one of the communications to be chosen
nondeterministically, if both guards fail, then nothing happens.

```
if
  B1;           // Guard 1: Boolean expression (must be true for this branch to be eligible)
  C1 => S1;     // Communication (C1) succeeds AND then Statement S1 executes
  []            // "or else" – separates alternative guarded commands
  B2;           // Guard 2: Boolean expression
  C2 => S2;     // Communication (C2) succeeds AND then Statement S2 executes
fi              // End of the if-block
```

- If B1 is true and C1 can execute, then S1 runs.
- If B2 is true and C2 can execute, then S2 runs.
- If both are possible, then the system chooses nondeterministically.
- If neither is possible, then nothing happens.

### Loop (do)

The `do` command makes the following pseudocode program repeat the loop as long as at least one guard succeeds.

```
do 
    B;
    C => S;
od
```

### Conditionals (if ... fi) vs. Loops (do ... od)

- `if ... fi `: Choose **one** of the successful guards one time (or none if all fail).
- `do ... od`: Choose one of the successful guards repeatedly until all fail.

```
if
    B1; C1 -> S1; // If B1 is true and C1 can execute, then run S1
[]  B2; C2 -> S2; // Else if B2 is true and C2 can execute, then run S2
fi                // Exit the program
```

```
do
    B1; C1 -> S1;   // If B1 is true and C1 can execute, then run S1
[]  B2; C2 -> S2;   // Else if B2 is true and C2 can execute, then run S2
od                  // Repeat until no guard succeeds
```

## Simple Examples

```
// Copy process
process Copy {
  char c;           
  do West?c -> East!c; od
}
```

- Guard: `West?c` (waits for input value from `West`).
- Statement: `East!c` (sends that value as output to `East` )
- Loop: Keep copying values (characters) from `West` to `East` until `West` stops sending (guard fails).

### Copy process with two inputs

```
process Copy {
  char c1, c2;
  West?c1;                          // First, receive one character into c1
  do 
     West?c2 -> East!c1; c1 = c2;   // Option 1: input new char c2, send old c1, shift c2 into c1
  [] East!c1 -> West?c1;            // Option 2: send c1 immediately, then wait for a new char
  od
}

```

- Starts by reading a single input into `c1`.
- Inside the loop, two alternatives:
    - Option 1: Receive a new char `c2`, send the old `c1`, then replace `c1` with `c2`.
    - Option 2: Send the current `c1` and then wait for a new one.
- The nondeterministic choice (`[]`) allows either branch when both are possible.

### Bounded Buffer

```
process Buffer {
    char buffer[10];
    int front = 0, rear = 0, count = 0;

    do 
       count < 10; West?buffer[rear] -> count++; rear = (rear + 1) mod 10;
    [] count > 0; East!buffer[front] -> count--; front = (front + 1) mod 10;
    od
}
```

- Implements a circular queue of size 10.
- Guard 1: `count < 10` → buffer not full → input from `West` is allowed.
- Store the new value in `buffer[rear]`, increment `rear modulo 10`, increase `count`.
- Guard 2: `count > 0` → buffer is not empty → output to `East` is allowed.
- Send value from `buffer[front]`, increment `front modulo 10`, decrease `count`.
- The process nondeterministically accepts input or produces output depending on buffer state.

### Symmetric Exchange

```
process P1 {
  int value1 = 1, value2;

  if P2!value1 -> P2?value2;      // Option 1: send first, then receive
  [] P2?value2 -> P2!value1;      // Option 2: receive first, then send
}

process P2 {
  int value1, value2 = 1;

  if P1!value2 -> P1?value1;      // Option 1: send first, then receive
  [] P1?value1 -> P1!value2;      // Option 2: receive first, then send
}
```

- Both processes try to exchange values.
- Each has two choices: either send first then receive, or receive first then send.
- Nondeterminism ensures both processes eventually synchronize in one of the two patterns.
- This models handshaking between processes with no fixed order.

### Greatest Common Denominator

```
process GCD {
  int id, x, y;
  do true ->                            // Loop forever
       Client[*]?args(id, x, y);        // Receive arguments from any client
       do x > y -> x = x - y;           // Subtract smaller from larger (Euclidean algorithm)
       [] x < y -> y = y - x;           // Repeat until x == y
       od
       Client[id]!result(x);            // Send back the gcd to the requesting client
  od
}
```

- Acts like a server process.
- Waits for clients to send a pair `(x, y)`.
- Use the subtraction-based Euclidean algorithm to compute gcd.
- Send the result back to the requesting client.
- Loop repeats, allowing it to handle multiple requests.

## Modern CSP Notation

So far we’ve seen CSP in guarded command style with explicit loops and conditionals.
In _modern CSP_, processes are often specified more algebraically, focusing on **sequences of events**.

- **Prefix operator (`->`):** `a -> P` means “first do event `a`, then behave like process `P`.”
- **Recursion:** A process can be defined in terms of itself, creating infinite behavior.
- **Choice (`[]`):** `a -> P [] b -> Q` means “either perform `a` then continue as `P`, or perform `b` then continue as
  `Q`.”

### Examples

#### Vending Machine

```
Machine = coin -> chocolate -> Machine
```

1. The machine first waits for the `coin` event.
2. Then it does the `chocolate` event (i.e., dispenses chocolate).
3. Then it behaves like `Machine` again (ready for the next customer).

This is an **infinite loop** modeled using recursion.

#### Drink Choices

```
Drink = coffee -> Drink [] tea -> Drink
```

1. The process **nondeterministically** chooses `coffee` or `tea`.
2. After each choice, it loops back to `Drink`. 