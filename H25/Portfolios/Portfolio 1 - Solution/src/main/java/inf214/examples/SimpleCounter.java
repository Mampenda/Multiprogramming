package inf214.examples;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleCounter {
	static final AtomicInteger count = new AtomicInteger();
	static final int N = 10000;
	static int oldCount = 0;

	public static void main(String[] args) throws InterruptedException {
		Thread c1 = new Thread(new Counter(), "c1");
		Thread c2 = new Thread(new Counter(), "c2");
		c1.start();
		c2.start();
		c1.join();
		c2.join();
		System.out.println("" + N + " + " + N + " = " + count);

	}

	static class Counter implements Runnable {
		Object lock = new Object();

		@Override
		public void run() {
			try {
				for (int i = 0; i < N; i++) {
					count.incrementAndGet();
					Thread.sleep(0);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
