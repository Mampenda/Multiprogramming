# Generators and Iterators in JavaScript

## 1. Iterables and Iterators

- **Iterable**: An object that can be looped over (e.g., `arrays`, `strings`).
- **Iterator**: An object with a `.next()` method that returns `{ value, done }`.

###                

```js
const arr = [10, 20]; // 
const it = arr[Symbol.iterator]();

console.log(it.next()); // { value: 10, done: false }
console.log(it.next()); // { value: 20, done: false }
console.log(it.next()); // { value: undefined, done: true }
```

## 2. Generator Functions

- A **generator** (`function*`) automatically creates an iterator.
- Inside a generator, `yield` produces values one at a time and `next` resumes and moves on to the next value.
-
    - `yield` pauses the generator and returns a `value`.
- Calling `.next(value)` can **send data back into the generator**

### A simple generator using `yield` and `next`:

```js
function* simpleGen() {
    yield 1;         // pause here, return 1
    yield 2;         // pause here, return 2
    yield 3;         // pause here, return 3
}

const g = simpleGen();   // Create a generator object

console.log(g.next()); // { value: 1, done: false }
console.log(g.next()); // { value: 2, done: false }
console.log(g.next()); // { value: 3, done: false }
console.log(g.next()); // { value: undefined, done: true }
```

### `yield` and `next` Sending Values

```js
function* echo() {
    const msg = yield "send me something";
    console.log("got:", msg);
}

const g = echo();           // Create a generator object

console.log(g.next());      // { value: "send me something", done: false }
console.log(g.next("hi"));  // { value: "hi", done: true } (done is true because there are no more calls to `next`)
```

### Using `yield` over an Iterable

```js
function* iterate(arr) {
    for (const item of arr) {
        yield item; // pause and return each item
    }
}

const it = iterate(["a", "b", "c"]);
console.log(it.next()); // { value: "a", done: false }
console.log(it.next()); // { value: "b", done: false }
console.log(it.next()); // { value: "c", done: false }
console.log(it.next()); // { value: undefined, done: true }
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

## 5. Generators in JavaScript

### We can use `for...of` directly in generators:

```js
for (const n of evens()) { //evens() defined above returns even numbers
    if (n > 10) break;
    console.log(n); // 2, 4, 6, 8, 10
}
```

## 6. Yield Expressions

`yield` can both **send values out** and **receive values in**. This allows _coroutines_ or complex stateful
computations.

```js
function* g(x) {
    let r = x * (yield -1) + (yield "blabla") + (yield false);
    console.log(r);
}

let p = g(3);

console.log(p.next(32)); // { value: -1, done: false }
console.log(p.next(4));  // { value: "blabla", done: false }
console.log(p.next(5));  // { value: false, done: false }
console.log(p.next(6));  // prints: 3 * 4 + 5 + 6 = 23
```

