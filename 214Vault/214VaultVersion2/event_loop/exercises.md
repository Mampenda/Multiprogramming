# Exercises - Event Loop

## Exercise 1
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
| mainline execution starts          | at X ms          |
| mainline execution finishes        | at X ms          |
| promise handler starts             | at X ms          |
| promise handler finishes           | at X ms          |
| promise resolved a tiny bit after  | at X ms          |
| `timeoutHandler` starts            | at X ms          |
| `timeoutHandler` finishes          | at X ms          |
| timer fires                        | at X ms          |
| user clicks button                 | at X ms          |
