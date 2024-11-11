# Task about semaphores for practicing when preparing for the exam

Consider Multi-country, which is a country, and Multi-city, which is its capital city. 

The airport of Multi-city has introduced strict entry requirements for arriving passengers because of a global pandemic.
Some of the passengers are vaccinated, while others are not vaccinated. All passengers arriving to Multi-city must go 
through the document/passport control, where their vaccination certificates are checked by border guard officers. 

Upon arriving to Multi-city Airport, passengers, both vaccinated and un-vaccinated, are mingling in the Mingling Zone, 
and they are walking towards to the Documents Checking Zone. There, the border guard checks their vaccination 
certificates, imposes quarantine on the un-vaccinated ones, and in any case lets all the passengers into the city. 

Assume that the passengers enter the Documents Checking Zone in a random order. The only requirement is that there must 
be never un-vaccinated and vaccinated passengers in the Documents Checking Zone at the same time. However, people with 
the same vaccination status are allowed in the Documents Checking Zone at the same time (that is, at any given moment of
time, either all passengers in the Documents Checking Zone are vaccinated, or all passengers in the Documents Checking 
Zone are un-vaccinated). 

Assume that there are `N` vaccinated passengers, and `M` un-vaccinated passengers who have just landed at Multi-city 
Airport.

Your task is to simulate the described situation in the AWAIT language.
👉 Represent passengers as processes.
👉 Use semaphores for synchronization.
👉 Make sure that your solution avoids deadlock.
💡 Your solution need NOT be fair.

```
// Code in the AWAIT language

// Semaphores for synchronization
sem vaxMutex = 1;
sem unvaxMutex = 1;
sem doc_check_zone = 1;

// Counters to track the number of passangers currently in the Documents Checking Zone
int vaxCounter = 0;
int unvaxCounter = 0;

// Process representing vaccinated passangers
process vaccinated[i = 1 to N] {
    while(true) {
        // Mingling Zone: passangers walk towards the Document Checking Zone (no code her, just representing time spent)

        // Enter critical section for adjusting vaccinated passanger count
        P(vaxMutex);                // Lock access to "vaxCounter" for synchronization
        
        // CRITICAL SECTION //
        vaxCounter++;               // Increment nr. of vaccinated passangers in the zone
        if (vaxCounter == 1) {      // If this is the first passanger in the zone...
            P(doc_checking_zone);   // Lock the zone for un-vaccinated passangers
        }
        V(vaxMutex);                // Release access to "vaxCounter"

        // Documents are being checked by the border guard...

        // Exit critical section after vaccinated passangers is done
        P(vaxMutex);                // Lock access to "vaxCounter" for synchronization
        vaxCounter--;               // Decrement nr. of vaccinated passangers in the zone

        // If this is the last passanger in the zone...
        if (vaxCounter == 0) {
            V(doc_checking_zone);   // Release access to "vaxCounter" for synchronization
        }
        V(vaxMutex);                // Release access to "vaxCounter"
    }
}

process unvaccinated[i = 1 to M] {
    while(true) {
        // Mingling Zone: passangers walk towards the Document Checking Zone (no code her, just representing time spent)

        // Enter critical section for adjusting vaccinated passanger count
        P(unvaxMutex);                // Lock access to "unvaxCounter" for synchronization
        
        // CRITICAL SECTION //
        unvaxCounter++;               // Increment nr. of un-vaccinated passangers in the zone
        if (unvaxCounter == 1) {      // If this is the first passanger in the zone...
            P(doc_checking_zone);     // Lock the zone for vaccinated passangers
        }
        V(unvaxMutex);                // Release access to "unvaxCounter"

        // Documents are being checked by the border guard...

        // Exit critical section after unvaccinated passangers is done
        P(unvaxMutex);                // Lock access to "unvaxCounter" for synchronization
        unvaxCounter--;               // Decrement nr. of un-vaccinated passangers in the zone
        
        // If this is the first passanger in the zone...
        if (unvaxCounter == 0) {
            V(doc_checking_zone);     // Release the zone for vaccinated passangers
        }
        V(unvaxMutex);                // Release access to "unvaxCounter"
    }
}
```
