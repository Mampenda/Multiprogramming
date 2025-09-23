# Generators and Iterators in JavaScript

## 1. Iterables and Iterators

- **Iterable**: An object that can be looped over (e.g., `arrays`, `strings`).
- **Iterator**: An object with a `.next()` method that returns `{ value, done }`.

###                                           

```js
const arr = [10, 20];               // Array of iterable objects
const it = arr[Symbol.iterator]();  // Iterator object

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
console.log(g.next("hi"));  // { value: undefined , done: true }
```

In this example the `msg` receives `"hi"`. The generator logs `got: "hi"`. The returned object is
`{ value: undefined, done: true }`, not `{ value: "hi", done: true }` because the generator finished without returning
anything.

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

### Generators as Iterables

Because generators implement the iterable protocol, we can use them directly with `for...of`:

```js
function* evens() {
    let num = 2;
    while (true) {
        yield num; // pause here and return num
        num += 2;  // update num for next iteration
    }
}

for (const n of evens()) {
    if (n > 10) break;
    console.log(n); // 2, 4, 6, 8, 10
}
```

## 3. Practical Generator Examples

### a)  Fibonacci Sequence

```js
function* fibonacci() {
    let a = 0, b = 1;
    while (true) {
        yield a;                // pause here and return current number
        [a, b] = [b, a + b];    // update number pair
    }
}
```

### b) Cycling Through an Iterable

```js
function* cycle(iterable) {
    while (true) {
        for (const item of iterable) { // iterates over any iterable (array, string, set, etc.)
            yield item; // pause here and return item, then continue infinite while loop
        }
    }
}
```

### c) Unique Elements

```js
function* unique(iterable) {
    const seen = new Set();         // set of seen items
    for (const item of iterable) {  // for...of iterable protocol over iterable object
        if (!seen.has(item)) {      // if current item has not been seen before
            yield item;             // pause here and return item
            seen.add(item);         // add item to set (remember item)
        }
    }
}
```

## 4. Sending Data Into Generators

`next(value)` sends data back into the generator, replacing the `yield` expression.

```js
function* foo(x) {
    var y = x * (yield false); // yield false, then wait for a value
    return y;
}

var it = foo(100);      // Create generator object

var res = it.next(2);   // Call generator with input 2
console.log(res.value); // false (from the yield)

res = it.next(3);       // Call generator again with input 3
console.log(res.value); // 300

```

## 5. Yield Expressions

`yield` can both **send values out** and **receive values in**. This allows _coroutines_ or complex stateful
computations.

```js
function* g(x) {
    let r = x * (yield -1) + (yield "blabla") + (yield false);
    console.log(r);
}

let p = g(3);

console.log(p.next(32)); // { value: -1, done: false }       -> 32 is ignored (not at a yield yet becasue all first calls are ignored)
console.log(p.next(4));  // { value: "blabla", done: false } -> "yield -1" becomes 4
console.log(p.next(5));  // { value: false, done: false }    -> "yield 'blabla'" becomes 5
console.log(p.next(6));  // prints 23 becasuse 3 * 4 + 5 + 6 = 23

```

