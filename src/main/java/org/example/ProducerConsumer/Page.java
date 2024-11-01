package org.example.ProducerConsumer;

public class Page {
    public int i;
    public String text;

    public Page(int i, String text) {
        this.i = i;
        this.text = text;
    }

    public Object getText() { return text; }

    public boolean isPoisonPill() {
        return false;
    }
}
