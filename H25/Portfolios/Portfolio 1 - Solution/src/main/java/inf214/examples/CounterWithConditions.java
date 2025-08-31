package inf214.examples;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CounterWithConditions {
	private final Lock lock = new ReentrantLock();
	private final Condition notZero = lock.newCondition();
	private final Condition notMax = lock.newCondition();
	
	private int count = 0;
	private final int limit = 50;
	
	public int increment() throws InterruptedException {
		lock.lock();
		try {
			while(count >= limit) {
				notMax.await();
			}
			count ++;
			notZero.signal();
			
			return count;
		} finally {
			lock.unlock();
		}
	}
	
	public int decrement() throws InterruptedException {
		lock.lock();
		try {
			while(count <= 0) {
				notZero.await();
			}
			count --;
			notMax.signal();
			
			return count;
		} finally {
			lock.unlock();
		}
	}
}
