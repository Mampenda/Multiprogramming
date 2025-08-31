# INF214 H25 Portfolio Set 1 - Concurrency in Java (and a little bit C++)

## Task A - Classic monitors/await language
This task is about writing concurrency control “by hand” with monitors and conditions. It is to learn about the
conceptual foundation: how to structure synchronization using monitors, entry/exit protocols, conditions, and await
statements.

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