# Promises
## Terminology
- A promise represents a future value of some sort
- When a promise is created, it is pending
- At some point in the future, the promise is either fulfilled, i.e. the promise's computation succeeded, or rejected,
  i.e. the promise's computation failed. 
- Once a promise has been fulfilled/rejected, it is considered settled/resolved.

```java
/* A promise wraps code that will run asynchronously 
 * At some point that code is done. 
 * 1. If it succeeds, it will notify the world using the resolve callback
 * 2. If it fails, it will call the reject callback
 */

var p = new Promise(
        function(resolve, reject) {
            // long running computation
            if(success){
                resolve( ... );
            } else {
                reject( ... );
            }
        }  
);
var fulfulled = function(){};
var rejected = function(){};
p.then(fulfilled, rejected); /* We can register interest in the promise by calling its then() method and providing callbacks for the success/failure cases */
```