# Exam task
## Question:
Using JavaScript, define a promise which is immediately resolved. Use console.log to print out the value of the promise.

### Answer:
```javascript
const promise = new Promise((resolve, reject) => {
	resolve("Promise is resolved");
});

promise.then((value) => {
	console.log(value);
});
```


## Question:
Using JavaScript, define a promise which is immediately rejected. Use console.log to print out the value of the promise.

### Answer:
```javascript
const promise2 = new Promise((resolve, reject) => {
	reject("Promise is rejected");
});

promise2.catch((value) => {				
	console.log(value);
});
```

# Task from Oblig2:

## Question:

Implement a function `itmp`, whose behavior is similar to method `.map()` of `Array`, with the difference that this function `itmp` returns an _iterable_ (rather than an `Array`), and produces its results on demand.

**The solution should be less than 10 lines of code.**

Here is a skeleton of the solution:

```js
....... itmp(iterable_input, func_to_be_applied) {
  // ..........
}

const p = itmp( [10, 20],  x => x * x );

console.log( p.next().value ); // 100
console.log( p.next().value ); // 400
```


### Answer:

```js
function* itmp(iterable_input, func_to_be_applied) {
  for (const item of iterable_input) {
    yield func_to_be_applied(item);
  }
}


const p = itmp([10, 20], x => x * x);

console.log(p.next().value); // 100
console.log(p.next().value); // 400
```
# Extra tasks:

## Question:

Implement a generator function `evens` that generates even numbers indefinitely. The first invocation of `next()` should return 2, the second 4, and so on.

### Answer:

```javascript
function* evens() {
  let num = 2;
  while (true) {
    yield num;
    num += 2;
  }
}

const evenNumbers = evens();

console.log(evenNumbers.next().value); // 2
console.log(evenNumbers.next().value); // 4
```


## Question:

Implement a generator function `fibonacci` that generates Fibonacci sequence numbers indefinitely. The first invocation of `next()` should return 0, the second 1, the third 1, the fourth 2, and so on.

### Answer:
```javascript
function* fibonacci() {
  let a = 0, b = 1; let prev = 0;
  while (true) {
    yield a;
    prev = a;
    a = a + b;
    b = prev;
  }
}

const fibSeq = fibonacci();

console.log(fibSeq.next().value); 
console.log(fibSeq.next().value); 
console.log(fibSeq.next().value); 
console.log(fibSeq.next().value); 
console.log(fibSeq.next().value);
console.log(fibSeq.next().value);
console.log(fibSeq.next().value);
console.log(fibSeq.next().value);
console.log(fibSeq.next().value);
console.log(fibSeq.next().value);
console.log(fibSeq.next().value);
console.log(fibSeq.next().value);
```


## Question:
Implement a generator function `cycle` that takes an iterable and endlessly cycles through its elements. The first invocations of `next()` should return elements of the iterable in order.

### Answer:

```javascript
function* cycle(iterable){
	for (const item of iterable){
		yield item;
	}
}

let list = [9,23,42,75,26,58,25,14]

const cyc = cycle(list)


console.log(cyc.next().value)
console.log(cyc.next().value)
console.log(cyc.next().value)
console.log(cyc.next().value)
console.log(cyc.next().value)
console.log(cyc.next().value)
console.log(cyc.next().value)
console.log(cyc.next().value)

```


## **Question:**

Implement a generator function `unique` that takes an iterable and generates unique elements indefinitely. The first invocations of `next()` should return unique elements of the iterable.

### **Answer:**

```javascript
function* unique(iterable){
	let seen = []
	for (element of iterable){
		if (!seen.include(element)){
			yield element;
		}
		seen.add(element);
	}
}

myList = [123,4,4,4,4,4,53,12,465,124]

const uni = unique(myList)

console.log(uni.next().value)
console.log(uni.next().value)
console.log(uni.next().value)
console.log(uni.next().value)
console.log(uni.next().value)
```



