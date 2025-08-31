/*
package org.example.ProducerConsumer;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class Parser implements Runnable{

    // declare queue of pages
    private BlockingQueue<Page> queue;

    // constructor
    public Parser(BlockingQueue<Page> queue) {
        this.queue = queue;
    }

    */
/*
    * This method has the outer loop similar to the one we first implemented in AnalyseText(), but instead of counting
    * the words in every page, it puts each page into the queue.
    * *//*

    @Override
    public void run() {
        try {
            // Declare iterable pages
            Iterable<Page> pages = new Page(100000, "dumpfile.xml");
            for (Page page : pages) {
                queue.put(page); // Put each page into the queue
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/
