/*
package org.example.ProducerConsumer;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class AnalyseText {

    private static final HashMap<String, Integer> counts = new HashMap<String, Integer>();

    public static void countWord(String word){
        Integer currentCount = counts.get(word);
        if(currentCount == null){
            counts.put(word, 1);
        } else {
            counts.put(word, currentCount + 1);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        */
/* From java.util and is very fitting for producer/consumer patterns. It supports very effective and
        * concurrently implemented put() and take(), and it also blocks when necessary. If you attempt to call take()
        * on an empty queue, this will lock the Thread until the queue is non-empty. And similarly if you call put(),
        * to a queue that's full, it'll lock it until the queue has space to put something in it.
        * *//*

        ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<>(100000);
        HashMap<String, Integer> counts = new HashMap<String, Integer>();

        Thread counter = new Thread(new Counter(queue, counts));
        Thread parser = new Thread(new Parser(queue));

        counter.start();
        parser.start();
        parser.join();

        */
/* This is a special element of the queue that tells it that the data for processing is finished, and
        * that the consumer should finish its work. Similar to how null is used in C++ to denote the end of a string.
        *  *//*

        //queue.put(new PoisonPill());

        counter.join();
    }
}*/
