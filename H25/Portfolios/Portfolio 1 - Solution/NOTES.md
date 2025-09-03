# INF214 H25 Portfolio Set 1 - Concurrency in Java (and a little bit C++)

## Task A - Classic monitors/await language
This task is about writing concurrency control “by hand”. It is to learn about the conceptual foundation: how to 
structure synchronization using monitors, entry/exit protocols, conditions, and await statements.

- A shared monitor object (`City()`) that manages access to a shared resource (the intersection).


- Multiple threads (vehicles) that call methods on the monitor to gain/release access.


- Entry/Exit protocols:
    - `requestAccess()` / `releaseAccess()` for regular vehicles.
    - `requestSpecialAccess()` / `releaseSpecialAccess()` for special vehicles.


- Critical sections (all `synchronized` methods):
    - **Outer critical sections (guarded by the `City()` monitor / synchronized methods):**
        - `startDay()`
        - `getTrafficLight(int)`
        - `setTrafficLight(int, TraficLight)`
        - `isAvaliable(int)`
        - `getCitySize()`

    - **Inner critical sections (guarded by each `Intersection()`’s monitor / synchronized methods):**
        - `reset()`
        - `isAvaliable()`
        - `getTrafficLight()`
        - `setTrafficLight()`

    - **Explicit lock (`ReentrantLock()`) for special vehicle access:**
    - `requestAccess()` (partly)
    - `requestSpecialAccess()`
    - `releaseSpecialAccess()`


- Atomic read/writes in `City()`:
    - `AtomicInteger numWorkersInside` that counts the number of regular vehicles inside the intersection.
    - `AtomicInteger specialAccess` that counts the number of special vehicles inside

- **No conditions**, instead it relies on
    - `synchronized` methods for monitor-like behavior
    - `atomic` variables for safe counters/flags
    - `ReentrantLock()` for mutual exclusion between special and regular vehicles

## Task B — Java's built-in concurrency tools
This task is about using Java’s built-in concurrency tools (atomic operations, concurrent collections), which give the
same effect without explicit monitors. Conceptually, it’s still synchronization — but here the Java library provides the
atomic blocks instead of hardcoded await language.

**It’s about atomic operations and lock-free concurrency.**

Still about synchronization between threads (valets), but instead of explicit monitors/conditions, it uses:

- **Shared resource management:**
    - The `ParkingLot()` is a shared resource, managed with:
        - `ConcurrentMap<String,Car>` holds currently parked cars.
        - `AtomicInteger numParkedCars` tracks occupancy.
        - Multiple `Valet` threads operate on the same lot simultaneously.


- **Thread synchronization without explicit locks/await:**
    - Instead of `await`/`signal` in a monitor, threads use:
        - `reserveParking()` → atomic increment, only succeeds if a spot is free.
        - `Thread.onSpinWait()` → busy-wait loop until condition is met.
        - This is effectively an *entry protocol* for car parking/pickup, but implemented with **lock-free primitives**
          instead of monitors.


- **Bounded resource allocation**
    - `grabCamera()` and `grabSupervisor()`, internally synchronize valets access to `Cameras` and `Supervisors`


## Task C


Alternative: 
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
