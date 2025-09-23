# Exercises

## Exercise 1

Write a generator that produces only odd numbers.

### Answer:

```js
function* odds() {
    let n = 1;
    while (true) { // loops forever
        yield n;
        n += 2;
    }
}

// Example usage

const it = odds();

console.log(it.next()); // { value: 1, done: false }
console.log(it.next()); // { value: 3, done: false }
console.log(it.next()); // { value: 5, done: false }
console.log(it.next()); // { value: 7, done: false }

// alternatively can break loop early with for...of
for (const n of odds()) {
    if (n > 9) break;
    console.log(n);
}
```

## Exercise 2

Implement a generator `itmp` that behaves like the eager `Array.map`, but produces results lazily, i.e., it doesn't go
through the entire array and apply the function to every element like eager functions does.

### Answer:

```js
// behaves like nums.map(x => x * 2), but values are generated only when .next() is called
function* itmp(iterable, fn) {
    for (const item of iterable) {
        yield fn(item); // apply mapping function lazily
    }
}

// Example usage:
const nums = [1, 2, 3, 4];
const doubled = itmp(nums, x => x * 2);

console.log(doubled.next()); // { value: 2, done: false }
console.log(doubled.next()); // { value: 4, done: false }
console.log(doubled.next()); // { value: 6, done: false }
console.log(doubled.next()); // { value: 8, done: false }
console.log(doubled.next()); // { value: undefined, done: true }
```

## Exercise 3

For the following exercises, explain what happens in each `console.log()` in the JavaScript code and what will be logged
when its executed.

### Exercise 3.1

```js
function* foo(x) {
    var y = x * (yield false);
    return y;
}

var it = foo(100);
var res = it.next(2);
console.log(res.value);
res = it.next(3);
console.log(res.value);
```

#### Answer:

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

1. First `console.log(res.value);` will print `false` because of the first `yield`.
2. Second `console.log(res.value);` will print `300` because the first `yield` became `3` and `100 * 3 = 300`.

### Exercise 3.2

```js
function* g(x) {
    let r = x * (yield -1) + (yield "blabla") + (yield false);
    console.log(r);
}

let p = g(3);

console.log(p.next(32));
console.log(p.next(4));
console.log(p.next(5));
console.log(p.next(6));
```

#### Answer

```js
function* g(x) {
    let r = x * (yield -1) + (yield "blabla") + (yield false);
    console.log(r);
}

let p = g(3);            // Create generator object

console.log(p.next(32)); // { value: -1, done: false }       -> 32 is ignored (not at a yield yet because all first calls are ignored)
console.log(p.next(4));  // { value: "blabla", done: false } -> "yield -1" becomes 4
console.log(p.next(5));  // { value: false, done: false }    -> "yield 'blabla'" becomes 5
console.log(p.next(6));  // prints 23 because 3 * 4 + 5 + 6 = 23
```

### Exercise 3.3

```js
function* foo() {
    console.log("start");
    const a = yield 1;
    console.log("a =", a);
    const b = yield a + 1;
    console.log("b =", b);
    return b * 2;
}

const it = foo();

console.log(it.next());
console.log(it.next(10));
console.log(it.next(20));

```

#### Answer

```js
function* foo() {
    console.log("start");
    const a = yield 1;
    console.log("a =", a);
    const b = yield a + 1;
    console.log("b =", b);
    return b * 2;
}

const it = foo();         // Create generator object

console.log(it.next());   // Prints "start" and returns { value: 1, done: false } 
console.log(it.next(10)); // Prints "a = 10" and returns { value: 11, done: false } 
console.log(it.next(20)); // Prints "b = 20" and returns { value: 40, done: false } (because it returns b * 2)
```

### Exercise 3.4

```js
function* counter() {
    let i = 0;
    while (i < 3) {
        yield i++;
    }
    return "done";
}

const it = counter();

console.log(it.next());
console.log(it.next());
console.log(it.next());
console.log(it.next());
```

#### Answer

```js
function* counter() {
    let i = 0;
    while (i < 3) {
        yield i++;
    }
    return "done";
}

const it = counter();   // Create generator object

console.log(it.next()); // { value: 0, done: false }
console.log(it.next()); // { value: 1, done: false } 
console.log(it.next()); // { value: 2, done: false } 
console.log(it.next()); // { value: "done", done: true } 
```

### Exercise 3.5

```js
function* echoTwice() {
    const x = yield "first";
    yield x;
    yield x * 2;
}

const it = echoTwice();

console.log(it.next());
console.log(it.next(5));
console.log(it.next());
console.log(it.next());

```

#### Answer

```js
function* echoTwice() {
    const x = yield "first";
    yield x;
    yield x * 2;
}

const it = echoTwice(); // Create generator object

console.log(it.next());  // { value: "first", done: false }
console.log(it.next(5)); // { value: 5, done: false } (assigns x = 5, then yields 5)
console.log(it.next());  // { value: 10, done: false } (because 5 * 2 = 10)
console.log(it.next());  // { value: undefined, done: true } (generator finished)
```

### Exercise 5.6

```js
function* trickySum(x) {
    let a = yield x;
    let b = yield a + x;
    return a + b + x;
}

const it = trickySum(2);

console.log(it.next());
console.log(it.next(3));
console.log(it.next(4));
```

#### Answer

```js
function* trickySum(x) {
    let a = yield x;
    let b = yield a + x;
    return a + b + x;
}

const it = trickySum(2); // Create generator object

console.log(it.next());  // { value: 2, done: false }
console.log(it.next(3)); // { value: 5, done: false } (a + x = 3 + 2 = 5)
console.log(it.next(4)); // { value: 9, done: true } (a + b + x = 3 + 4 + 2 = 9)
```