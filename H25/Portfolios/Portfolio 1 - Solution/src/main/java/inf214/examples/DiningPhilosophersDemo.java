package inf214.examples;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DiningPhilosophersDemo {

	/**
	 * Random is thread-safe – the random seed is stored in an AtomicLong, which
	 * allows it to be updated atomically even if multiple threads ask for a random
	 * number at the same time (see implementation of {@link Random#next(int)}).
	 * However, if performance matters, we should use {@link ThreadLocalRandom}
	 * instead.
	 */
	static Random random = new Random();

	public static void main(String[] args) throws InterruptedException {
		// Names of our dining philosophers (replace with your own favourite philosophers!)
		List<String> names = List.of("Aristotélēs", "Ibn Sīnā", "Kǒngzǐ", "de Beauvoir", "Pāṇini");
		
		// One chopstick for each philosopher
		List<Chopstick> chopsticks = new ArrayList<>();
		names.forEach(name -> chopsticks.add(new Chopstick()));	
		// TODO: what happens if we add more chopsticks?
		// chopsticks.add(new Chopstick());

		// List of philosophers
		List<DiningPhilosopher> philos = new ArrayList<>();
		int i = 0;
		for (var name : names) {
			philos.add(new DiningPhilosopher(name, chopsticks.get(i % chopsticks.size()),
					chopsticks.get((i + 1) % chopsticks.size())));
			i++;
		}

		// One thread for each philosopher
		List<Thread> threads = philos.stream().map(philo -> new Thread(philo, philo.name)).toList();

		// Start dining
		threads.forEach(thread -> thread.start());

		// The thread management interface lets us inspect running threads and
		// find any deadlocked threads
		ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
		
		while (true) {
			// this is mostly for debug purposes, and not meant as a way to
			// “solve” deadlock problems.
			long[] ids = tmx.findDeadlockedThreads();
			if (ids != null) { // deadlock detected
				ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
				for (var info : infos) {
					System.out.println(info);
				}
				//System.exit(1);
			}
			Thread.sleep(1000);
		}
	}

	/**
	 * Each instance of DiningPhilosopher will be running in a different thread.
	 */
	static class DiningPhilosopher implements Runnable {
		private Chopstick left;
		private Chopstick right;
		private String name;

		public DiningPhilosopher(String name, Chopstick left, Chopstick right) {
			this.name = name;
			this.left = left;
			this.right = right;
		}

		public void run() {
			try {
				while (true) {
					// TODO: experiment with the wait times
					// to see if that makes the deadlock more
					// or less likely
					Thread.sleep(500 + random.nextInt(1));
					System.out.println(name + " is hungry!");
					synchronized (left) { // pick up first chopstick
						System.out.println(name + " picked up " + left);
						//Thread.sleep(random.nextInt(100));
						System.out.println(name + " tries to pick up " + right);
						synchronized (right) { // pick up second chopstick
							System.out.println(name + " picked up " + right + " and is eating!");
							Thread.sleep(random.nextInt(1000));  // spend some time eating
							System.out.println(name + " is done eating!");
						}
					}
					// wait a while before eating again
					Thread.sleep(random.nextInt(1000));
				}
			} catch (InterruptedException ex) {
			}
		}
	}

	/**
	 * We could just use Object (or Integer), if we didn't
	 * care about having a nice toString(). 
	 */
	static class Chopstick {
		static int nChopsticks; // to give each chopstick a unique name
		int n;

		public Chopstick() {
			n = ++nChopsticks;
		}

		public String toString() {
			return "chopstick#" + n;
		}
	}
}
