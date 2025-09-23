# Exercises

## Exercise 1

Write a generator that produces only odd numbers.

## Exercise 2

Implement a generator `itmp` that behaves like the eager `Array.map`, but produces results lazily, i.e., it doesn't go
through the entire array and apply the function to every element like eager functions does.

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