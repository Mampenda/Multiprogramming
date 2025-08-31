/*
package org.example.ProducerConsumer;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

import static org.example.ProducerConsumer.AnalyseText.countWord;

public class Counter implements Runnable {
    // Declare queue of pages
    private BlockingQueue<Page> queue;

    // Declare map for counting words
    private Map<String, Integer> counts;

    // Constructor
    public Counter(BlockingQueue<Page> queue, Map<String, Integer> counts) {
        this.queue = queue;
        this.counts = counts;
    }

    @Override
    public void run() {
        try {
           while (true) {
               Page page = queue.take();

               if (page.isPoisonPill()){
                   break;
               }

               // Actual counting
               Iterable<String> words = new Words(page.getText());
               for (String word : words) {
                   countWord(word);
               }
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
*/
