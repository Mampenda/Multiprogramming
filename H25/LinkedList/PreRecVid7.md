# Linked List Insertion

Let's say we want to insert an element into a linked list. A possible approach is to protect the entire list with a 
single lock, but in this case, no other thread could get access to the list until the lock is unlocked. 
In other words, this can lead to contention and reduced concurrency. 

Instead, we can use fine-grained locking by locking the two nodes on each side of the position where we want to insert 
the new node, also known as hand-over-hand locking. 

This is easy to implement with the class `ReentrantLock()`.

```java
class Main {

    private class Node {
        // Value of this node
        int value;
        
        // Pointers to the previous and next nodes
        Node prev;
        Node next;
        
        // Lock for this node
        ReentrantLock lock = new ReentrantLock();

        Node() {
        }

        Node(int value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    // Dummy head and tail nodes
    private final Node head;
    private final Node tail;

    public Main(){
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.prev = head;
    }
    
    /* 
    * Insert method that guarantees that the list is always sorted, by searching until it finds the first entry that is 
    * less that the new one.
    */
    public void insert(int value) {
        Node current = head; // Start from the head node
        current.lock.lock(); // Lock the head node
        
        Node next = current.next; // The next node after the head
        
        try {
            while (true) { // Infinite loop over the elements in the list
                next.lock.lock(); // Lock the next node
                try {
                    if (next == tail || next.value < value) { // If the next is the tail or the new value is the largest
                        
                        // Insert the new node between current and next
                        Node newNode = new Node(value, current, next);
                        next.prev = newNode;
                        current.next = newNode;
                        return;
                    }
                } finally {
                    current.lock.unlock(); // Unlock the previous node
                }
                // Move to the next pair of nodes
                current = next;
                next = current.next;
            }
        } finally {
            next.lock.unlock(); // Unlock the node after the head
        }
    }

    public static void main(String[] args) {
    }
}
```