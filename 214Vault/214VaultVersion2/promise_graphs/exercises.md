# Promise Graphs
![EdgesInThePromiseGraph.png](../images/EdgesInThePromiseGraph.png)

## Question 1:
```javascript
var a = promisify({});
var b = a.onResolve(x => x + 1);
a.resolve(42);
```

### Answer: 
```javascript
var a = promisify({});              // 1 - new promise (P1/a)
var b = a.onResolve(x => x + 1);    // 2 - new resolve handler for (P1/a), (f2/b/x => x+1)
a.resolve(42);                      // 3 - root (V3/42) for (P1/a) 
```
`(V3/42) --Resolve--> (P1/a) --Resolve--> (f2/b/x => x+1) --> (V4/43)`

## Question 2:
```javascript
var a = promisify({});
var b = a.onResolve(x => x + 1);
var c = a.onReject(x => x - 1);
a.resolve(42);
```
### Answer: 
```javascript
var a = promisify({});              // 1 - new promise (P1/a) 
var b = a.onResolve(x => x + 1);    // 2 - new resolve handler for (P1/a), (f2/b/x => x+1) 
var c = a.onReject(x => x - 1);     // 3 - new reject handler for (P1/a), (f3/c/x => x-1) 
a.resolve(42);                      // 4 - root (V4/42) resolving (P1/a) 
```
```
(V4/42) --Resolve--> (P1/a) --Resolve--> (f2/b/x => x+1) --> (V2/43)
                        |
                        v
                        + ----Reject---> (f3/c/x => x-1) --> (V3/41) --Resolve--> (P3/c)
```

## Question 3: 
```javascript
var a = promisify ({}) ;
var b = promisify ({}) ;
var c = b.onReject(x => x + 1);
a.link(b);
a.reject(42);
```

### Answer: 
```javascript
var a = promisify ({}) ;        // 1 - new promise (P1/a)
var b = promisify ({}) ;        // 2 - new promise (P2/b)
var c = b.onReject(x => x + 1); // 3 - reject handler for (P2/b), (f3/c/x => x+1) 
a.link(b);                      // 4 - link (P1/a) to (P2/b)
a.reject(42);                   // 5 - root (V5/42) rejecting (P1/a) 
```
`(V5/42) --Reject--> (P1/a) --Link--> (P2/b) --Reject--> (f3/c/x => x+1) --> (V4/43) --> (P3/c)`

## Question 4: 
```javascript
var a = promisify({});
var b = a.onResolve(x => x + 1);
var c = a.onResolve(y => y - 1);
a.resolve(100);
```

### Answer: 
```javascript
var a = promisify({});              // 1 - new promise (P1/a) 
var b = a.onResolve(x => x + 1);    // 2 - new resolve handler for (P1/a), (f2/b/x => x+1)
var c = a.onResolve(y => y - 1);    // 3 - new resolve handler for (P1/a), (f3/c/y => y-1)
a.resolve(100);                     // 4 - root (V4/100) resolving (P1/a)
```
```
(V4/100) --Resolve--> (P1/a) --Resolve--> (f2/b/x => x+1) --> (V2/101) --> (P2/b)
                        |
                        v
                        + -----Resolve--> (f3/c/y => y-1) --> (V3/99) ---> (P3/c)
```