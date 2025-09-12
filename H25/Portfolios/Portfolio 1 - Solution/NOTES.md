# INF214 H25 Portfolio Set 1 - Concurrency in Java (and a bit of C++)

## Task A — Classic Monitors and Await Language

Task A focuses on **writing concurrency control “by hand”**. The goal is to understand the conceptual foundations: how
to structure synchronization using **monitors, entry/exit protocols, conditions, and await statements**.

- **Shared monitor object:**  
  `City()` acts as a monitor that manages access to a shared resource (the intersections).

- **Threads:**  
  Each vehicle is represented by a thread that interacts with the monitor to request and release access.

- **Entry/Exit Protocols:**
    - `requestAccess()` / `releaseAccess()` → for regular vehicles
    - `requestSpecialAccess()` / `releaseSpecialAccess()` → for special vehicles (e.g., emergency vehicles)

- **Critical Sections:**
    - **Outer critical sections** (guarded by the `City()` monitor / synchronized methods):
        - `startDay()`
        - `getTrafficLight(int)`
        - `setTrafficLight(int, TrafficLight)`
        - `isAvailable(int)`
        - `getCitySize()`

    - **Inner critical sections** (guarded by each `Intersection()`’s monitor / synchronized methods):
        - `reset()`
        - `isAvailable()`
        - `getTrafficLight()`
        - `setTrafficLight()`

    - **Explicit lock for special vehicle access:**
        - `ReentrantLock` is used to ensure mutual exclusion between regular and special vehicles, applied in:
            - `requestAccess()` (partly)
            - `requestSpecialAccess()`
            - `releaseSpecialAccess()`

- **Atomic counters in `City()`:**
    - `AtomicInteger numWorkersInside` → counts regular vehicles in the intersection
    - `AtomicInteger specialAccess` → counts special vehicles in the intersection

- **Synchronization approach:**
    - No explicit condition variables; instead, relies on:
        - `synchronized` methods → monitor-like mutual exclusion
        - `AtomicInteger` → safe counters for concurrent access
        - `ReentrantLock` → mutual exclusion for special vehicle access

**Conceptual takeaway:** This task shows how to implement **manual monitors and entry/exit protocols** without library
abstractions, using atomic counters and locks to enforce safe concurrent access.
---

### Note about Task A:

In the following code:

```java
    /**
 * ==== Methods from Task A.2 ====
 **/

// Ordinary vehicle request and release access
public void requestAccess() {
    numWorkersInside.incrementAndGet();

    if (specialAccess.get()) {
        numWorkersInside.decrementAndGet();

        // Wait until a special vehicle leaves
        lock.lock();
        lock.unlock();

        numWorkersInside.incrementAndGet();
    }
}
```

The `lock.lock()` and `lock.unlock()` calls are used here to create a blocking point for ordinary vehicles when a
special vehicle is present. This ensures that ordinary vehicles wait until the special vehicle has exited before
proceeding.

Usually, one should have something like this:

```java
lock.lock();
try{

do_something() // critical section
}finally{
        lock.

unlock();
}
```

But in fact, the two lines below work as a barrier, making every thread wait for eachother at this point. 

```java
lock.lock()
lock.unlock()
```

They don’t protect a local critical section, but instead they:

- Enforce mutual exclusion ordering between groups of threads (regular vs. special vehicles).
- Ensure memory visibility between threads, so that counters and flags are consistently observed.
- Act as a kind of synchronization barrier.

---

### Short and simple summary of Task A - Manual Monitors and Locks:

**Goal:** Learn how to control concurrency “by hand” using monitors and locks.

- **Shared resource:** `City()` manages access to intersections.
- **Threads:** Each vehicle is a thread requesting/releasing access.
- **Entry/Exit:**
    - Regular vehicles → `requestAccess()` / `releaseAccess()`
    - Special vehicles → `requestSpecialAccess()` / `releaseSpecialAccess()`
- **Critical sections:**
    - Outer (City-level) → e.g., `getTrafficLight()`, `setTrafficLight()`
    - Inner (Intersection-level) → e.g., `isAvailable()`, `reset()`
    - Special access uses `ReentrantLock` for exclusive access
- **Atomic counters:**
    - `numWorkersInside` → regular vehicles
    - `specialAccess` → special vehicles
- **Concept:** Use synchronized methods and atomic counters to safely control threads **without library-level
  concurrency tools**.

---

## Task B — Java’s Built-in Concurrency Tools

Task B demonstrates the **same problem but using Java’s built-in concurrency tools**, such as atomic variables and
concurrent collections. The goal is to implement **lock-free or library-assisted synchronization**, achieving thread
safety without writing explicit monitors.

- **Shared resource management:**
    - `ParkingLot()`, `Camera()`, and `Supervisor()` are shared resources.
    - Managed via:
        - `ConcurrentMap<String, Car>` → thread-safe storage for parked cars
        - `AtomicInteger numParkedCars` → occupancy counter
        - Multiple `Valet` threads operate concurrently on the same parking lot

- **Thread synchronization without explicit locks/await:**
    - Instead of `await`/`signal` in a monitor:
        - `reserveParking()` → atomic increment that only succeeds if a spot is available
        - `Thread.onSpinWait()` → busy-wait until condition is met
    - This acts as an **entry protocol** for parking/pickup using **lock-free primitives** rather than explicit monitors

- **Bounded resource allocation:**
    - `grabCamera()` and `grabSupervisor()` internally synchronize access to limited resources
    - They function as **monitors or semaphores**, blocking valets until a camera or supervisor is available, and
      releasing them afterward

**Conceptual takeaway:** Task B highlights **modern concurrency primitives** in Java: atomic operations, concurrent
collections, and built-in blocking queues replace explicit monitor/await logic, simplifying synchronization while
maintaining correctness.
---

### Short and simple summary of Task B — Built-in Java Concurrency:

**Goal:** Achieve the same thread safety using Java’s library features.

- **Shared resources:** `ParkingLot()`, `Camera()`, `Supervisor()`
- **Thread-safe structures:**
    - `ConcurrentMap<String, Car>` → parked cars
    - `AtomicInteger numParkedCars` → tracks occupancy
- **Synchronization:**
    - Lock-free entry using `reserveParking()` with atomic increment
    - Busy-wait with `Thread.onSpinWait()` until a spot is free
- **Resource limits:** `grabCamera()` / `grabSupervisor()` block threads until resource is available
- **Concept:** Use atomic operations and concurrent collections instead of manual monitors and conditions.

---

**Summary of differences:**

| Feature         | Task A                                               | Task B                                                  |
|-----------------|------------------------------------------------------|---------------------------------------------------------|
| Synchronization | Manual monitors, synchronized methods, ReentrantLock | Atomic variables, ConcurrentMap, BlockingQueue          |
| Resource access | Explicit entry/exit protocols                        | Lock-free entry via atomic operations & blocking queues |
| Waiting         | Condition variables / await                          | Busy-wait (`onSpinWait`) or library blocking methods    |
| Complexity      | Higher, requires careful manual design               | Lower, uses tested concurrency library constructs       |

--- 

## Task C — Road Maintenance: Concurrency Explanation

**Goal:** Simulate the assembly of maintenance kits by multiple specialists and a supervisor, using semaphores for
synchronization.

### Key Concepts Applied

1. **Processes / Threads**
    - The supervisor (_S_) and the three specialists (_A_, _B_, _C_) are modeled as concurrent processes.
    - Each process runs independently but must synchronize access to the shared “box” resource.

2. **Shared Resource**
    - The “box” that holds two items from the supervisor is a shared resource.
    - Only the correct specialist with the missing third item can add their item and complete the kit.

3. **Synchronization Using Semaphores**
    - A **split binary semaphore** is used to coordinate actions between the supervisor and the specialists.
    - Semaphores:
        - `semCleaningSolution` → signals specialist A
        - `semPaint` → signals specialist B
        - `semSensor` → signals specialist C
        - `semSupervisor` → ensures the supervisor waits until the kit is assembled
    - Workflow:
        1. Supervisor puts two items in the box.
        2. Supervisor signals the semaphore corresponding to the missing item.
        3. The specialist process for that item runs, adds the item, and assembles the box.
        4. Specialist signals `semSupervisor` so the supervisor can proceed to the next iteration.

4. **Mutual Exclusion**
    - The semaphore mechanism ensures **mutual exclusion**: only the correct specialist can act on the box at a time.
    - No two specialists can assemble the same box simultaneously, preventing race conditions.

5. **Deadlock Avoidance**
    - Using **binary semaphores** in a structured “signal → wait → signal” pattern avoids deadlock.
    - The supervisor only proceeds after a kit is completed (`semSupervisor.P()`), ensuring correct sequencing.

6. **Coordination Pattern**
    - This is an example of the **producer-consumer pattern**, where:
        - Supervisor = producer of partially filled boxes
        - Specialist = consumer that completes the box

7. **Randomized Selection**
    - The supervisor’s choice of which two items to place is randomized, ensuring that different specialists are
      signaled across iterations.
    - The random function maps to processes in a deterministic way (1 → A, 2 → B, 3 → C) to ensure the correct
      specialist responds.

---

### Summary of How It Works

1. Supervisor produces two items → signals the missing-item specialist.
2. Specialist waits on their semaphore, adds the missing item, and assembles the kit.
3. Specialist signals supervisor → supervisor continues to next iteration.
4. This ensures **safe, synchronized, deadlock-free assembly of N kits**.

--- 

## Solution:

```cpp
#include "alang.h"
#include <cstdlib>
#include <ctime>

// Semaphores for synchronization
semaphore semCleaningSolution(0);
semaphore semPaint(0);
semaphore semSensor(0);
semaphore semSupervisor(1);

bool done = false; // Global flag to indicate when all processes are done

void processSupervisor() {
  // This function from alang.h causes segmentation error because it might not yet be initialized when called
  // int randNum = randomProcess();

  // Random nr from 1, 2, or 3
  int randNum = (rand() % 3) + 1;

  // Wait for permission
  semSupervisor.P();

  if (randNum == 1) {
    printf("enter processSupervisor 1\n");

    // Put paint and sensor on the table
    produce("S", "paint");
    produce("S", "sensor");

    // Signal for cleaning solution
    semCleaningSolution.V();

  } else if (randNum == 2) {
    printf("enter processSupervisor 2\n");

    // Put sensor and cleaning solution on the table 
    produce("S", "cleaning solution");
    produce("S", "sensor");

    // Signal for paint
    semPaint.V();

  } else if (randNum == 3) {
    printf("enter processSupervisor 3\n");

    // Put cleaning solution and paint on the table
    produce("S", "cleaning solution");
    produce("S", "paint");

    // Signal for sensor
    semSensor.V();
  } else {
    throw std::invalid_argument("Invalid value.");
  }
}

void processCleaningSolution() {
  while(!done){

      semCleaningSolution.P();
      if (done) break; 

      produce("A", "cleaning solution");
      assemble("A");
      semSupervisor.V();
  }
}

void processPaint() {
  while (!done){

    semPaint.P();
    if (done) break; 

    produce("B", "paint");
    assemble("B");
    semSupervisor.V();
  }
}

void processSensor() {
  while (!done){

  semSensor.P();
  if (done) break; 

  produce("C", "sensor");
  assemble("C");
  semSupervisor.V();
  }
}
```

```cpp
#include "roadMaintenance.cpp"

int main() {
  srand(time(0));   // Seed random number generator
  const int N = 10; // Number of boxes to assemble
  processes ps;     // Process pool

  // Supervisor process
  ps += [&]() -> void {
    for (int _ = 0; _ < N; _++)
      processSupervisor();

    // Wait for the last worker to finish the final assemble/print, signal the supervisor, and print in console
    semSupervisor.P();
    std::cout << "All " << N << " boxes assembled" << std::endl;
  
    // Set global bool to true to signal threads to stop
    done = true;

    // Signal threads (wake them up so they can exit their sem_wait loop)
    semCleaningSolution.V();
    semPaint.V();
    semSensor.V();

    // exit(0); // Let workers finish before exiting
  };

  // Each worker llopps until 'done' internally (removed outer while loop)
  ps += [&]() -> void { processCleaningSolution(); };
  ps += [&]() -> void { processPaint(); };
  ps += [&]() -> void { processSensor(); };
}
```

### Explaining the Race Condition

#### How does the solution use split binary semaphore? 

**Regular Binary Semaphore** 

In a regular binary semaphore, one process does both `P()` (waits) and later `V()`s (signals).

Imagine specialist `B` (paint) owns a private lock to control when he himself produces paint.
- He locks (`P()`/ `lock.lock()`) before starting.
- He unlocks (`V()` / `lock.unlock()`) when finished.

```cpp
semaphore semPaint = 1;  // B controls his own access

void processPaint() {
  while (true) {
    semPaint.P();  // B waits until he can produce paint
    produce("B", "paint");
    assemble("B");
    semPaint.V();  // B signals he's done, so he can do it again
  }
}
```
**Split Binary Semaphore**

A `split binary semaphore` is just a binary semaphore (value ∈ `{0,1}`), but the `P()` and `V()` operations are distributed across different processes: the producer does the `V()` and the consumer does the `P()`.

This splits responsibility across processes enforces rendezvous/barriers.

In Task C the Supervisor `S` signals (`V()`) when paint is needed, and specialists `A,B,C` waits (`P()`) until that signal arrives.

```cpp
// ... other semaphores
semaphore semPaint = 0;

void processSupervisor() {
  // ... S puts cleaning solution + sensor in the box
  semPaint.V();  // S signals B that paint is needed
}

void processPaint() {
  semPaint.P();  // B waits until S asks for paint
  produce("B", "paint");
  assemble("B");
  semSupervisor.V(); // let S continue
}
```
#### Why is there a race condition? 

The race comes from the fact that the specialists `A,B,C` are running infinite loops: 
```cpp
#include "roadMaintenance.cpp"

int main() {
  // ... Other logic 

  ps += [&]() -> void {
    while (true)
      processCleaningSolution();
  };
  ps += [&]() -> void {
    while (true)
      processPaint();
  };
  ps += [&]() -> void {
    while (true)
      processSensor();
  };
}
```

