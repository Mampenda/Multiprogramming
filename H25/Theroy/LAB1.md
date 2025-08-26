# INF214 H25 - Lab 1 - Concurrency in Java - for week 36

## Presentation by a group leader

* __Linked List__ (_based on MittUiB pre-recorded video 7_)

## Tasks to solve before the group session and to discuss during the group session

### Task 1

Assume we have variables `x`, `y` and `z`, which all are initialized to 0.
Suppose we have two threads which are as follows.
What are the possible values of variable `x` after running the threads concurrently?
Explain your answer.

```java
Thread t1 = new Thread() {
    public void run() {
        x = y + z;
    }
};
Thread t2 = new Thread() {
    public void run() {
        y = 1;
        z = 2;
    }
};
```

### Possible answer

Possible values for `x`:** `0`, `1`, `2`, or `3`.
There’s no synchronization or `volatile`, so there’s **no happens-before** relationship between the threads. 
That means:

* `t1` reads `y` and `z` independently when evaluating `x = y + z;`.
* `t2` writes `y = 1;` and `z = 2;`

That results in these cases for `t1` reading `y` and `z`:

1. Sees neither update: `y=0`, `z=0` → `x = 0`
2. Sees only `y`’s update: `y=1`, `z=0` → `x = 1`
3. Sees only `z`’s update: `y=0`, `z=2` → `x = 2`
4. Sees both updates: `y=1`, `z=2` → `x = 3`

### Task 2
Implement method `run_both`, which takes two functions as parameters, and calls each of them in a new thread.
The method must return a tuple with the result of the both functions.
You may use any JVM-language (for example, Java, Kotlin, Scala, ...) for this task.

* Please note that Task 1 and Task 2 are **not related** to each other.
* _UPD 31.8.2023 2046CET_: An outline of code for Task 2 is available here: https://git.app.uib.no/Mikhail.Barash/inf214-h23/-/blob/main/lab1-task2-outline.java

### Possible answer (in Java)

```java
import java.util.concurrent.*;

// class `Tuple` represents a tuple

class Tuple<T1, T2> {
    private final T1 first;
    private final T2 second;

    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }
}


public class Main {
    
    public static <T1, T2> Tuple<T1, T2> run_both(Callable<T1> f1, Callable<T2> f2) throws InterruptedException, ExecutionException {

        // TODO: read about `Callable`

        // Hint: look into `ExecutorService`, `Future`, and thread pools
        
        /* TODO: ... code here ... */

        try {
            // Run the two functions `f1` and `f2`
            Future<T1> run1 = /* TODO: ... code here ... */;
            Future<T2> run2 = /* TODO: ... code here ... */;

            // Wait for the both runs to complete
            T1 result1 = run1.get();
            T2 result2 = run2.get();

            return new Tuple<>(result1, result2);
            
        } finally {
            /* TODO: ...*/
        }
    }


    public static void main(String[] args) {

        // example of using the function `run_both`:

        // f1:
        Callable<Integer> f1 = () -> {
            Thread.sleep(1000);
            return 42;
        };

        // f2:
        Callable<String> f2 = () -> "Greetings INF214!";

        try {
            Tuple<Integer, String> res = run_both(f1, f2);
            System.out.println("Result of function1: " + res.getFirst());
            System.out.println("Result of function2: " + res.getSecond());
        }
        catch (InterruptedException | ExecutionException e) { }
    }
}



```