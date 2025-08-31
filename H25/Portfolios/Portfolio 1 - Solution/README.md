# INF214 H25 Portfolio Set 1 - Concurrency in Java (and a little bit C++)


## Delivering this Portfolio Set 

- The Portfolio Set will be delivered and graded through CodeGrade.
- The submission link will be made available at a later time on [MittUiB](https://mitt.uib.no/courses/53850/assignments/108454).
- :rotating_light: Deadline: **26th September 2025 at 23:59**. :rotating_light:


## Task A. :vertical_traffic_light: Safety City Traffic Lights
Welcome to _Safety City_, the global benchmark for traffic safety and urban mobility. Renowned as the city with the safest traffic in the world, _Safety City_ combines cutting-edge technology, meticulously planned infrastructure, and a deeply ingrained culture of road awareness to create an environment where accidents are nearly nonexistent. From "intelligent" traffic signals and autonomous public transport to comprehensive pedestrian zones and rigorous driver education, every element of the city’s design prioritizes the protection of its residents and visitors. In _Safety City_, safety isn't just a policy - it's a way of life.

### Problem A.1: :lock: Locked for Safety

The current system allows only one person at a time to take control of a traffic light and while this is very safe, the city manager finds it far too slow. They therefore want you to implement a solution that keeps the same level of safety, but allows multiple people to take different traffic lights at the same time.

Have a look at [`City::getTrafficLight(int i)`](<src/main/java/inf214/portfolioSet1/cityTrafficLights/City.java>) - is it possible to access different locations concurrently?
For example, if one thread calls `city.getTrafficLight(1)` and another one calls `city.getTrafficLight(2)`, will one of them have to wait for the other to complete? That is probably a bad idea: if we have a big city and a million cars, it would be a pity if the `getTrafficLight()` method became a bottleneck.¹ In this case, it's fairly easy to solve this: instead of storing `Trafficlight` or `null` directly, we can have each city location store an **`Intersection`** object, and let the `Intersection` keep track of whether it has a `Trafficlight` or not. Since every Java object has its own intrinsic lock, and every location has its own `Intersection` object, we can now lock each location individually instead of locking the entire city.  
We can also solve this by using a datastructure such as `AtomicReferenceArray`, but that will be a more complicated solution.

:arrow_right: **Your task is to modify the implementation of [`City`](<src/main/java/inf214/portfolioSet1/cityTrafficLights/City.java>) so different `Intersection`s can be accessed concurrently. Specifically, make it so that both `getTrafficLight` and `setTrafficLight` no longer needs to be synchronized.**

:books: Here is some relevant documentation for possible solutions:
 * [`Synchronized Methods` (Oracle)](<https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html>)
 * [`AtomicReferenceArray` (Oracle)](<https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicReferenceArray.html>)


### Problem A.2: :rotating_light: Special Access
Now that the congestion problem has been fixed, the city has decided to tackle its next problem. Even though extremely rare, accidents do still happen, and when they do, it is crucial that the emergency vehicles can arrive at the scene as fast as possible. To solve this, the city has decided on a solution where the emergency vehicles will be able to request special access to the trafficlights.

This special access will work as follows:
  1. The emergency vehicle requests special access.
  1. Any other ordinary vehicles will then be blocked from accessing the traffic lights and will instead have to wait for the emergency vehicles to pass.
  1. Any ordinary vehicles that have already taken a traffic light, will finish and clear out.
  1. When all vehicles have cleared out and all traffic lights are free, the special vehicle will drive to and from the accident and then unlock the traffic lights afterwards.
  1. The ordinary vehicles will then resume their normal route.

Before starting this task you will have to uncomment all code pieces marked with `Uncomment for task A2`. There will be one in the method [`CityTrafficLightsDemo::makeVehicles`](<src/main/java/inf214/portfolioSet1/cityTrafficLights/CityTrafficLightsDemo.java>) and two in the method [`OrdinaryVehicle::goToWork`](<src/main/java/inf214/portfolioSet1/cityTrafficLights/OrdinaryVehicle.java>).

:arrow_right: **Your task is to implement the following methods:**
 - **`City::requestAccess()`**
 - **`City::releaseAccess()`**
 - **`City::requestSpecialAccess()`**
 - **`City::releaseSpecialAccess()`.**
 - **`City::isCityEmpty()`**

_**Note**: You can assume that there will be at most one emergency vehicle at a time._

:bulb: _**Hint:** This problem can also be viewed as a simplified version of the [Readers and Writers problem](<https://en.wikipedia.org/wiki/Readers%E2%80%93writers_problem>) with priority given to the emergency vehicles. Please note that we do not care about fairness of the solution._


## Task B. :parking: Safety City Parking
Thanks to you, the traffic flow in _Safety City_ has been greatly improved, eliminating the slow-moving congestion that once plagued its streets. However, this success has led to an unexpected challenge: a growing parking problem. As more drivers take advantage of the now-efficient roads, demand for parking spaces has surged, especially in busy commercial and residential areas. While the city continues to lead in traffic safety, the local parking lot must now hire more [valets](<https://en.wikipedia.org/wiki/Valet_parking>) to keep up with the steady stream of incoming vehicles.

### Problem B.1: :eyes: Supervised Training
Because of all the new hirees, the management at the parking lot have discovered two problems that needs fixing. 

:one: The _first_ one is that the parking lot now needs to be able to handle all the new valets trying to park and pickup cars simultaniously, since we wouldn't want a valet to try and park a car on top of another.
For this sake, management deviced a scheme where before parking a car, the valet will first reserve a parking spot. Then, only when they have been able to reserve a spot, will they park the car.
Similarly, before a valet attempts to pickup a car, they will first check if the car has been parked. If the car has not been parked yet, they will have to wait until it has been parked and then pick it up. This is needed as sometimes the customer can change their mind about parking and will want their car back before a valet has time to park it.

:two: The _second_ is that we need to make sure that the new valets are up to standard.
To make sure that the new hirees are up to standard, the old valets has taken up the role of supervisors and will oversee and train the new hires. This means that a valet will not be allowed to park or pickup a car without a supervisor togheter with them in the car. Management has also, for insurance reasons, required that all valets carry with them a camera when parking and picking up the cars.
Unfortunately, there are more new valets than there are supervisors and all the new cameras that management has ordered has not arrived yet. All this creates the need for a smart solution of distributing supervisors and cameras amongst the valets, and management has request you to solve this for them.

:arrow_right: **Your task is to:**

:arrow_right: **1. Implement the methods in the [`ParkingLot`](<src/main/java/inf214/portfolioSet1/cityParking/ParkingLot.java>) class. This means to implement the functions: `reserveParking()`, `parkCar(Car car)`, `isParked(String regNr)`, and `pickupCar(String regNr)`. You will also have to decide on a data structure for the parking lot.**
    
_**Note**: For reserving a parking spot, you only need to make sure that a valet will not move on to parking a car unless there is an available spot. This means that the number of items in you datastructure should never exceed the number specified by the variable `ParkingLot::numParkingSpots`._

:arrow_right: **2. Implement the missing methods in the [`Valet`](<>) class. To do this, you might have to make some changes to the methods [`CityParking::grabSupervisor(Valet valet)`](<src/main/java/inf214/portfolioSet1/cityParking/CityParking.java>) and [`CityParking::grabCamera`](<src/main/java/inf214/portfolioSet1/cityParking/CityParking.java>).**

_**Note:** After a valet grabs a supervisor and camera, and has parked a car, they need to make a call to the [`Supervisor::superviseParking`](<src/main/java/inf214/portfolioSet1/cityParking/Supervisor.java>) method so that the supervisor can log the event. Similarly, after a valet grabs a supervisor and camera, and has picked up a car, they need to make a call to the [`Supervisor::supervisePickUp`](<src/main/java/inf214/portfolioSet1/cityParking/Supervisor.java>) method._

_**Note:** You can assume that there will always be at least as many cameras available as supervisors when solving the task._

### Problem B.2: :bar_chart: Registration Number Statistics
Now that the valet service itself is running smoothly, management has decided that the valets has enough time to do some book keeping for them. Management has started considering putting in chargers for electrical vehicles, but as this is a considerable cost they first want to look at the actual need. To do this, they will have the valets input the first two letters of the [registration numbers](<https://www.vegvesen.no/kjoretoy/eie-og-vedlikeholde/skilt/skiltserier/>) of the cars they park into a shared database (data structure). As the valets are able to input this information from their phones, the "database" needs to be able to handle concurrent accesses, even on the same field. Management also wants to be able to query this "database" at any point. And since you already know the systems, management has again requested you to fix this.

:arrow_right: **You are free to implement this task as you see fit, there is only two requirement: At any point, one should be able to do a query through the method [`CityParking::queryDatabase(String regChars)`](<src/main/java/inf214/portfolioSet1/cityParking/CityParking.java>), and get back the amount of times any two characters have been seen so far. You should also be able to handle getting a request for a two characters that haven't been seen yet.**

_**Note:** For this task, you may assume that any registration number you get will contain two characters + a number. For example: BT1, RB22, LB435_ and so on.

:books: Here is some relevant documentation for a possible solution:
 * [`ConcurrentMap` (Oracle)](<https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentMap.html>)

## Task C. :construction: Road Maintenance

The city supervisor _S_ invited three specialists _A_, _B_, and _C_ to prepare road maintenance kits for maintaining the smart roads.

Each maintenance kit contains a bottle of _cleaning solution_, a can of road _paint_, and  temperature _sensor_:

- Specialist _A_ has an unlimited supply of _cleaning solution_.
- Specialist _B_ has an endless supply of road _paint_.
- Specialist _C_ has an unlimited supply of temperature _sensors_.
- Supervisor _S_ has an unlimited supply of all three items.
 
Here is what happens:

- Supervisor _S_ puts two different items into the box (e.g., a bottle of cleaning solution and a sensor).
- The specialist who has the third missing item (in this case, specialist _B_ with road paint) adds their own item and assembles a road maintenance kit, and sends it to the field team.
- Supervisor _S_ waits until that kit is finished.
- This cycle repeats for `N` iterations.

:arrow_right: **Your task is to write code in the AWAIT/ALANG language that simulates this situation. Represent the supervisor S, and the specialists A, B, and C as processes.**  
**You must use a _split binary semaphore_ for synchronization. Make sure that your solution avoids deadlock. You must log what items have been produced and when a box is assembled, you can do this by calling the functions [`produce`](<taskC/alang.h>) and [`assemble`](<taskC/alang.h>) which can be found at the end of the `alang.h` file.**

_**Note**: You do not need to implement the `while(true)` and `for` loops that runs the processes. This is done for you in the [`demo.cpp`](<taskC/demo.cpp>) file._

_**Note**: The numbers given from the random function should correspond to processes in the following order:_ 
```
1 -> processCleaningSolution -> A
2 -> processPaint -> B
3 -> processSensor -> C
```

_**:warning: :warning: :warning: Warning 1:** DO NOT make changes to the `alang.h` file. Any changes you make here will not work on CodeGrade and you risk getting 0 points for this task._  
_**:warning: :warning: :warning: Warning 2:** You are given random function that gives you a number in the interval [1, 3], DO NOT swap this out as the CodeGrade tests will fail, you and risk getting 0 points for this task._

### Sample output
```
S put cleaning solution into the box
S put sensor into the box
B put paint into the box
B assembled the box

S put paint into the box
S put sensor into the box
A put cleaning solution into the box
A assembled the box

S put cleaning solution into the box
S put paint into the box
C put sensor into the box
C assembled the box

All 3 boxes assembled
```

:arrow_right: **Implement the missing parts in the file [`roadMaintenance`](<taskC/roadMaintenance.cpp>).**

:bulb: _**Hint:** The `produce` and `assemble` functions can be used in the following way:_
```c++
produce("A", "cleaning solution");
assemble("A");
```

### Running the code for Task C
#### Running locally on your machine
Navigate to the [`taskC`](<taskC>) folder in the terminal and execute the commands
```bash
g++ -std=c++20 demo.cpp -o taskC
./taskC
```
_We will not be able to provide help with installing a C++ compiler... But you can use REPL.IT - it's much easier! (See below)._

#### Running through REPL.IT
- You can "remix" this REPL.IT: https://replit.com/@mikbar/INF214-H25-Portfolio-Set-1-Task-C#roadMaintenance.cpp 
- After solving the task in REPL.IT, you can manually replace the contents of the file [`roadMaintenance.cpp`](<taskC/roadMaintenance.cpp>)
in your fork of the GitLab repository with your solution from the file [`roadMaintenance.cpp`](https://replit.com/@mikbar/INF214-H25-Portfolio-Set-1-Task-C#roadMaintenance.cpp) in REPL.IT. (That is, just manually copy-paste from REPL.IT to GitLab.)


:books: Here is some relevant documentation about the AWAIT/ALANG:  
https://mitt.uib.no/courses/53850/files/folder/other?preview=7028342


# How to run tests for Task A and Task B
To run the tests for Task A and Task B, you can either use a built-in test runner in your editor of choice,
or you can run them through maven with the command

```
mvn clean compile test
```

:warning: Please note that CodeGrade's `maven` is setup to use **Java 17**, so this might fail if you have a different Java version installed.

The tests are split into two folders: one for [task A](<src/test/java/inf214/portfolioSet1/cityTrafficlights>) and one for [task B](<src/test/java/inf214/portfolioSet1/cityParking>).
You have fulfilled a task when all tests for it are marked as "passed" on CodeGrade.

Tests for **Task A** are structured in the following way:
  - Two test classes, [`CityTest`](<src/test/java/inf214/portfolioSet1/cityTrafficlights/CityTest.java>) and [`TrafficLightTest`](<src/test/java/inf214/portfolioSet1/cityTrafficlights/TrafficLightTest.java>), that must pass in order to get points on either of the problems in **Task A**.
  - The [`LockedForSafetyTest`](<src/test/java/inf214/portfolioSet1/cityTrafficlights/LockedForSafetyTest.java>) must pass in order to get points on **Problem A.1**.
  - The [`SpecialAccessTest`](<src/test/java/inf214/portfolioSet1/cityTrafficlights/SpecialAccessTest.java>) must pass in order to get points on **Problem A.2**. 

Tests for **Task B** are structured in the following way:
  - Two test classes, [`CityParkingSimTest`](<src/test/java/inf214/portfolioSet1/cityParking/CityParkingSimTest.java>) and [`SupervisorTest`](<src/test/java/inf214/portfolioSet1/cityParking/SupervisorTest.java>), that must pass in order to get points on either of the problems in **Task B**.
  - The [`SupervisedTrainingTest`](<src/test/java/inf214/portfolioSet1/cityParking/SupervisedTrainingTest.java>) must pass in order to get points on **Problem B.1**.
  - The [`RegistratrationNumberStatTest`](<src/test/java/inf214/portfolioSet1/cityParking/RegistratrationNumberStatTest.java>) must pass in order to get points on **Problem B.2**. 

_**Note**: When looking through the tests, you might notice that some tests have a `timeout` annotation. The value of this timeout will be adjusted on codegrade to fit with valid solutions, but you might have to change the value to have the tests pass on your personal computer._


# How to run tests for Task C

Please note that we do not show you the tests for Task C.