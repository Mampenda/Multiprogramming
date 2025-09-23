# Exercises

## Exercise 1

Implement a generator `itmp` that behaves like `Array.map`, but produces results lazily.

### Answer:

## Exercise 2

Write a generator fibonacci (infinite).

### Answer:

## Exercise 3

Write a generator cycle for looping indefinitely over an iterable object.

### Answer:

## Exercise 4

Implement a generator `itmp` that behaves like `Array.map`, but produces results lazily.

### Answer:

## Exercise 5

What will be printed when the following JavaScript code is executed? Explain your answer.

```js
function* foo(x) {
    var y = x * (yield false);
    return y;
}

var it = foo(100);
var res = it.next(2);
console.log(res.value); // what will be printed here? Answer: false
res = it.next(3);
console.log(res.value); // what will be printed here? Answer: 300
```

## 4. Practical Generators

### a) Infinite Even Numbers

```js
function* evens() {
    let num = 2;
    while (true) {
        yield num;
        num += 2;
    }
}
```

### b) Fibonacci Sequence

```js
function* fibonacci() {
    let a = 0, b = 1;
    while (true) {
        yield a;
        [a, b] = [b, a + b];
    }
}
```

### c) Cycling Through an Iterable

```js
function* cycle(iterable) {
    while (true) {
        for (const item of iterable) {
            yield item;
        }
    }
}
```

### d) Unique Elements

```js
function* unique(iterable) {
    const seen = new Set();
    for (const item of iterable) {
        if (!seen.has(item)) {
            yield item;
            seen.add(item);
        }
    }
}
```
