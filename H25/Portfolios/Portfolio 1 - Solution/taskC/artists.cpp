#define _DEFAULT_SOURCE

#include <pthread.h>              // pthread_t, pthread_create(), pthread_join()
#include <stdlib.h>               // atoi(), rand(), srand(), exit()
#include <string.h>               // strcmp()
#include <unistd.h>               // usleep()
#include <stdio.h>                // printf()
#include <time.h>                 // time()
#include <stdbool.h>              // bool, true, false
#include <mutex>
#include <condition_variable>

// =====================
// Global State (C++)
// =====================

// painting quotas for each artist
int paintingsLeft[3];  // set from CLI

// synchronization primitives
std::mutex m;
std::condition_variable cvTable;          // supplier waits for table to become free
std::condition_variable cvArtist[3];      // each artist waits for their turn

// shared flags/state under 'm'
bool tableFree      = true;               // true when no one is using the table
int  targetArtist   = -1;                 // which artist is allowed next (-1 = none)
bool stopAll        = false;              // set true when everyone is done

// =====================
// Utility Functions
// =====================

/// Returns true if artist 'id' still has paintings left.
static inline bool has_paintings_left(int id) {
    return paintingsLeft[id] > 0;
}

/// Pick an artist who still has work left. Returns -1 if all are done.
/// IMPORTANT: call only while holding lock 'm'.
int pick_artist_locked() {
    bool a = has_paintings_left(0);
    bool b = has_paintings_left(1);
    bool c = has_paintings_left(2);

    if (!a && !b && !c) return -1;

    // choose uniformly among the remaining artists
    int pool[3], n = 0;
    if (a) pool[n++] = 0;
    if (b) pool[n++] = 1;
    if (c) pool[n++] = 2;

    int idx = rand() % n;
    return pool[idx];
}

// pretty-print which pair the supplier put (based on target artist)
void print_supplies_for(int who) {
    switch (who) {
        case 0: // Artist 0 has Paint; needs Brushes + Canvas
            printf("Supplier produced Brushes and Canvas\n");
            break;
        case 1: // Artist 1 has Brushes; needs Paint + Canvas
            printf("Supplier produced Paint and Canvas\n");
            break;
        case 2: // Artist 2 has Canvas; needs Paint + Brushes
            printf("Supplier produced Paint and Brushes\n");
            break;
    }
}

// =====================
// Supplier Thread
// =====================

void *supplierThdFunc(void * /*arg*/) {
    while (true) {
        std::unique_lock<std::mutex> lock(m);

        // Wait until the table is free or we’re stopping
        cvTable.wait(lock, [] { return tableFree || stopAll; });

        if (stopAll) break; // clean exit if all done

        // Pick an artist who still needs supplies
        int who = pick_artist_locked();
        if (who == -1) {
            // Everyone finished — wake any waiting artists, then stop
            stopAll = true;
            cvArtist[0].notify_all();
            cvArtist[1].notify_all();
            cvArtist[2].notify_all();
            cvTable.notify_all();
            break;
        }

        // Occupy the table and announce the supplies for 'who'
        tableFree = false;
        targetArtist = who;
        print_supplies_for(who);

        // Let the chosen artist proceed
        cvArtist[who].notify_one();

        // Wait until the artist finishes and frees the table
        cvTable.wait(lock, [] { return tableFree || stopAll; });
        if (stopAll) break;
    }

    pthread_exit(nullptr);
    return nullptr;
}

// =====================
// Artist Thread
// =====================

void *artistThdFunc(void *arg) {
    int id = *static_cast<int*>(arg);
    delete static_cast<int*>(arg);

    printf("Artist %d starts working...\n", id);

    while (true) {
        // Wait for our turn (or for global stop)
        std::unique_lock<std::mutex> lock(m);
        cvArtist[id].wait(lock, [id] {
            return stopAll || (!tableFree && targetArtist == id);
        });

        if (stopAll) break;               // graceful exit if everything’s done
        if (!has_paintings_left(id)) {
            // In case we were awakened but quota is already zero, free the table and exit.
            tableFree = true;
            targetArtist = -1;
            lock.unlock();
            cvTable.notify_one();
            break;
        }

        // We own the table now. Release lock while "painting" to avoid blocking supplier prints.
        lock.unlock();

        // Simulate painting time (0–1500 ms)
        usleep((rand() % 1500) * 1000);

        // Update state: one painting finished, free table
        lock.lock();
        if (paintingsLeft[id] > 0) {
            paintingsLeft[id]--;
        }

        // pretty color output
        switch (id) {
            case 0: printf("\033[0;31mArtist %d completed a painting\033[0m\n", id); break;
            case 1: printf("\033[0;32mArtist %d completed a painting\033[0m\n", id); break;
            case 2: printf("\033[0;34mArtist %d completed a painting\033[0m\n", id); break;
        }

        tableFree = true;
        targetArtist = -1;

        // If everyone is done after this, set stop flag and wake all
        if (!has_paintings_left(0) && !has_paintings_left(1) && !has_paintings_left(2)) {
            stopAll = true;
            lock.unlock();
            cvTable.notify_all();
            cvArtist[0].notify_all();
            cvArtist[1].notify_all();
            cvArtist[2].notify_all();
            break;
        } else {
            lock.unlock();
            cvTable.notify_one();  // tell supplier the table is free
        }
    }

    printf("Artist %d has finished all paintings.\n", id);
    pthread_exit(nullptr);
    return nullptr;
}

// =====================
// Main
// =====================

int main(int argc, char **argv) {
    srand(static_cast<unsigned int>(time(nullptr))); // seed RNG

    // CLI: -p <paintings per artist: 1..10>
    if (argc != 3) {
        printf("Usage: %s -p <1..10>\n", argv[0]);
        return 1;
    }
    if (strcmp(argv[1], "-p") != 0) {
        printf("Error: first arg must be -p\n");
        return 1;
    }
    int quota = atoi(argv[2]);
    if (quota < 1 || quota > 10) {
        printf("Error: invalid number of paintings (1..10)\n");
        return 1;
    }

    // init quotas
    for (int i = 0; i < 3; ++i) paintingsLeft[i] = quota;

    // create threads
    pthread_t supplierThd;
    pthread_t artistThd[3];

    pthread_create(&supplierThd, nullptr, &supplierThdFunc, nullptr);
    for (int i = 0; i < 3; ++i) {
        int *id = new int(i);
        pthread_create(&artistThd[i], nullptr, &artistThdFunc, id);
    }

    // join
    pthread_join(supplierThd, nullptr);
    for (int i = 0; i < 3; ++i) {
        pthread_join(artistThd[i], nullptr);
    }

    return 0;

    // =====================
    // HOW TO RUN
    // =====================

    // cd into H25/Portfolios/Portfolio 1 - Solution/taskC
    // compile (in case it has not been done already): g++ -o artists artists.cpp -pthread
    // run the program: ./artists -p 3
}
