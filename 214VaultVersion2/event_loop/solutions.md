# Examples - Event Loop

## Example 1 - Macrotask
JavaScript has single-threaded execution model, hence only one task can be executed at a time.
This means that all created tasks have to wait in a queue until their turns for execution comes.

```javascript
<button id={"first"}></button>
<button id={"second"}></button>
<script>
    const first = document.getElementById("first");
    const second = document.getElementById("second");
    first.addEventListener("click", () => {/* code that runs for 8 ms */});
    second.addEventListener("click", () => {/* code that runs for 5 ms */});
    /* code that runs for 15 ms*/
</script>
```
A super user who 
- clicks the first button after 5 ms the script stats executing, and 
- clicks the second button 12 ms after the script start executing. 

### Explanation: 

| What happens                  | at what time |
|-------------------------------|--------------|
| Mainline execution starts     | after 0 ms   |
| User clicks first button      | after 5 ms   |
| User clicks first button      | after 12 ms  |
| Mainline execution finishes   | after 15 ms  |
| First event handler starts    | after 15 ms  |
| First event handler finishes  | after 23 ms  |
| Second event handler starts   | after 23 ms  |
| Second event handler finishes | after 28 ms  |


$$\begin{align}
& \textbf{Macrotask queue:} 
& \textbf{Mainline JS} \rightarrow \textbf{click first button } \rightarrow \textbf{ click second button} 
&&&&&&&&&&&&&&&&&& 
\end{align}$$

## Example 2 - Macrotask and Microtask
Now the code includes a promise in the first button-click handler.
Promise handlers (i.e., the callbacks that are attached to the promise’s then() method) are always called 
asynchronously, even if when attached to an already resolved promise.

```javascript
<button id={"first"}></button>
<button id={"second"}></button>
<script>
    const first = document.getElementById("first");
    const second = document.getElementById("second");
    first.addEventListener("click", () => {
        Promise.resolve().then( () => { /* some promise handling code that runs for 4 ms */ })
        /* code that runs for 8 ms */
    });

    second.addEventListener("click", () => {/* code that runs for 5 ms */});
    {/* code that runs for 15 ms */}
</script>
```
### Explanation:

| What happens                  | at what time |
|-------------------------------|--------------|
| Mainline execution starts     | after 0 ms   |
| User clicks first button      | after 5 ms   |
| User clicks first button      | after 12 ms  |
| Mainline execution finishes   | after 15 ms  |
| First event handler starts    | after 15 ms  |
| First event handler finishes  | after 23 ms  |
| Promise handler starts        | after 23 ms  |
| Promise handler finished      | after 27 ms  |
| Second event handler starts   | after 23 ms  |
| Second event handler finishes | after 28 ms  |

$$\begin{align}
& \textbf{Macrotask queue: }
\textbf{Mainline JS} \rightarrow \textbf{click first button } \rightarrow \textbf{ click second button} 
\newline
& \textbf{Microtask queue: }  
\textbf{ Promise Success}
&&&&&&&&&&&&&&&&&&
\end{align}$$

The engine calls all promise callbacks asynchronously after the first handler is done. By creating a new microtask 
queue and pushing the promise onto the microtask queue. 

### Microtask, macrotask, and GUI re-rendering
A re-render can occur between two macrotasks only if there are no microtasks in between. 

In Example 2: 
- the page can be rendered between the mainline JavaScript execution and the first click handler. But it can’t be 
rendered immediately after the first click handler because microtasks have priority over rendering! 
- A render can also occur after a microtask, but only if no other microtasks are waiting in the microtask queue. After 
the promise handler occurs (but before the event loop moves onto the second click handler), the browser can re-render 
the page. 

Nothing stops the promise success microtask from queueing other microtasks. All these microtasks will have priority over
the second button handler task. The event loop will re-render the page and move onto the second button handler task only 
when the microtask queue is empty.

### Time-Outs and Intervals
Timers enable delaying the execution of a piece of code by at least a certain number of milliseconds. This capability 
can be used to break long-running tasks into smaller tasks that won’t clog the event loop, thereby stopping the browser 
from rendering, and making the application slow and unresponsive

How to create and manipulate timers?
• `setTimeout()`
• `setInterval()`
• `clearTimeout()`
• `clearInterval()`

**Note** 
These are methods of the window object (global context). Timers aren’t defined within the JavaScript itself. Instead, 
they are provided by the host environment.

### setTimeout and clearTimeout

`id = setTimeout(func, delay)`
- Initiates a timer that will execute the passed callback once after the specified delay has elapsed 
- Returns a value that uniquely identifiers the timer

`clearTimeout(id)`
- cancels (“clears”) the timer identified by the passed value if the timer hasn’t yet fired 

`id = setInterval(func, delay)`
- Initiates a timer that will continually try to execute the passed callback at the specified delay interval, until 
cancelled 
- Returns a value that uniquely identifies the timer

`clearInterval(id)`
- cancels the interval timer identifier by the passed value

## Example 3 - Timer execution within the event loop 
```javascript
<button id={"myButton"}></button>
<script>
    {/* Mainline JS execution runs for 18 ms*/}
    setTimeout(function timeoutHandler() { /* code that runs for 6 ms */ }, 10); 
    setInterval(function intervalHandler() { /* code that runs for 8 ms */ }, 10); 
    
    const myButton = document.getElementById("myButton");
    myButton.addEventListener("click", () => function clickHandler() { /* code that runs for 10 ms */ });
    
    {/* Mainline JS execution  runs for 18 ms */}
</script>
```
### Explanation:

| What happens                  | at what time |
|-------------------------------|--------------|
| Mainline execution starts     | after 0 ms   |
| User clicks  button           | after 6 ms   |
| Timeout fires (not executes)  | after 10 ms  |
| Interval fires (not executes) | after 10 ms  |
| Mainline execution finishes   | after 18 ms  |
| Click event handler starts    | after 18 ms  |
| Interval fires (not executes) | after 20 ms  |
| Click event handler finishes  | after 28 ms  |
| Timeout event starts          | after 28 ms  |
| Interval fires (not executes) | after 30 ms  |
| Timeout event finishes        | after 34 ms  |
| Interval fires (executes)     | after 34 ms  |
| Interval fires (executes)     | after 42 ms  |
| Interval fires (executes)     | after 50 ms  |
| Interval fires (executes)     | after 60 ms  |
| Interval fires (executes)     | after 70 ms  |

$$\begin{align}
& \textbf{Macrotask queue: }
\textbf{Mainline JS} \rightarrow \textbf{click event } \rightarrow \textbf{ timer's event} \rightarrow 
\textbf{ intervals's event}  \rightarrow \textbf{ intervsal's event}  \rightarrow \textbf{ intervals's event}
\end{align}$$

# Exercises - Event Loop

## Exercise 1 (Exam H22)
Consider the following HTML/JavaScript

```html
```html

<button id="myButton"></button>
<script>
    setTimeout(function timeoutHandler() {
        /* code that runs for 6 ms*/
    }, 10);

    setInterval(function intervalHandler() {
        /* code that runs for 8 ms */
    }, 10);

    const myButton = document.getElementById("myButton");
    
    myButton.addEventListener("click", function clickHandler() {
        Promise.resolve().then(() => { /* some promise handling code that runs for 4 ms */ });
        /* click-handling code that runs for 10 ms */
    });
    /* code that runs for 18 ms */ 
</script>
```

| What happens...                    | ...at what time? |
|------------------------------------|------------------|
| `clickHandler` finishes            | at X ms          |
| `clickHandler` starts              | at X ms          |
| interval fires for the first time  | at X ms          |
| interval fires for the second time | at X ms          |
| interval fires for the third time  | at X ms          |
| interval fires for the fourth time | at X ms          |
| `intervalHandler` starts           | at X ms          |
| `intervalHandler` finishes         | at X ms          |
| mainline execution starts          | at 0 ms          |
| mainline execution finishes        | at X ms          |
| promise handler starts             | at X ms          |
| promise handler finishes           | at X ms          |
| promise resolved a tiny bit after  | at X ms          |
| `timeoutHandler` starts            | at X ms          |
| `timeoutHandler` finishes          | at X ms          |
| timer fires                        | at X ms          |
| user clicks button                 | at 6 ms          |

### Answer:

| What happens...                    | ...at what time? |
|------------------------------------|------------------|
| `clickHandler` finishes            | at 28 ms         |
| `clickHandler` starts              | at 18 ms         |
| interval fires for the first time  | at 10 ms         |
| interval fires for the second time | at 20 ms         |
| interval fires for the third time  | at 30 ms         |
| interval fires for the fourth time | at 40 ms         |
| `intervalHandler` starts           | at 38 ms         |
| `intervalHandler` finishes         | at 46 ms         |
| mainline execution starts          | at 0 ms          |
| mainline execution finishes        | at 18 ms         |
| promise handler starts             | at 28 ms         |
| promise handler finishes           | at 32 ms         |
| promise resolved a tiny bit after  | at 18 ms         |
| `timeoutHandler` starts            | at 32 ms         |
| `timeoutHandler` finishes          | at 38 ms         |
| timer fires                        | at 10 ms         |
| user clicks button                 | at 6 ms          |

$$\begin{align}
& \textbf{Macrotask queue: }
\textbf{Mainline JS} \rightarrow \textbf{click event } \rightarrow \textbf{ timer's event} \rightarrow
\textbf{ intervals's event}  \rightarrow \textbf{ intervsal's event}  \rightarrow \textbf{ intervals's event}
\newline
& \textbf{Microtask queue: }  
\textbf{ Promise Success}
&&&&&&&&&&&&&&&&&&
\end{align}$$