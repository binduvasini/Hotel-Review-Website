package cs601.concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 * A reentrant read/write lock that allows: 1) Multiple readers (when there is
 * no writer). 2) One writer (when nobody else is writing or reading). 3) A
 * writer is allowed to acquire a read lock while holding the write lock. The
 * assignment is based on the assignment of Prof. Rollins (original author).
 */
public class ReentrantReadWriteLock {

	// TODO: Add instance variables : you need to keep track of the read lock
	// holders and the write lock holders.
	// We should be able to find the number of read locks and the number of
	// write locks
	// a thread with the given threadId is holding
	private Map<Thread, Integer> readingThreads;
	private Map<Thread, Integer> writingThreads;

	/**
	 * Constructor for ReentrantReadWriteLock
	 */
	public ReentrantReadWriteLock() {
		// FILL IN CODE
		readingThreads = new HashMap<>();
		writingThreads = new HashMap<>();
	}

	/**
	 * Returns true if the current thread holds a read lock.
	 * 
	 * @return true if the reading threads map contains the current thread. Else
	 *         return false
	 */
	public synchronized boolean isReadLockHeldByCurrentThread() {
		// FILL IN CODE
		if (readingThreads.containsKey(Thread.currentThread()))
			return true;
		else
			return false;
	}

	/**
	 * Returns true if the current thread holds a write lock.
	 * 
	 * @return true if the writing threads map contains the current thread. Else
	 *         return false
	 */
	public synchronized boolean isWriteLockHeldByCurrentThread() {
		// FILL IN CODE
		if (writingThreads.containsKey(Thread.currentThread()))
			return true;
		else
			return false;

	}

	/**
	 * Non-blocking method that tries to acquire the read lock. Returns true if
	 * successful.
	 * 
	 * @return true if writing threads map is null or contains the current
	 *         thread. Else return false. Increment the reading threads to the
	 *         map
	 */
	public synchronized boolean tryAcquiringReadLock() {
		if (writingThreads.get(Thread.currentThread()) != null || writingThreads.size() == 0) {
			if (readingThreads.containsKey(Thread.currentThread())) {
				readingThreads.put(Thread.currentThread(), (readingThreads.get(Thread.currentThread()) + 1));
			} else {
				readingThreads.put(Thread.currentThread(), 1);
			}
			return true;
		} else
			return false;
	}

	/**
	 * Non-blocking method that tries to acquire the write lock. Returns true if
	 * successful.
	 * 
	 * @return true if writing threads map already contains the current thread
	 *         and the reading threads map is null
	 */
	public synchronized boolean tryAcquiringWriteLock() {
		if (readingThreads.get(Thread.currentThread()) != null) {
			return false;
		} else if (writingThreads.size() > 0 && writingThreads.get(Thread.currentThread()) == null) {
			return false;
		} else {
			if (writingThreads.containsKey(Thread.currentThread())) {
				writingThreads.put(Thread.currentThread(), (writingThreads.get(Thread.currentThread()) + 1));
			} else {
				writingThreads.put(Thread.currentThread(), 1);
			}
			return true;
		}
	}

	/**
	 * Blocking method - calls tryAcquiringReadLock and returns only when the
	 * read lock has been acquired, otherwise waits.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void lockRead() {
		// FILL IN CODE
		while (!tryAcquiringReadLock()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Releases the read lock held by the current thread.
	 * 
	 * Decrement the count if reading threads map already has the current
	 * thread. Else remove the record from the map. In the end, notify other
	 * threads
	 */
	public synchronized void unlockRead() {
		// FILL IN CODE
		if (readingThreads.containsKey(Thread.currentThread())) {
			if (readingThreads.get(Thread.currentThread()) > 1) {
				readingThreads.put(Thread.currentThread(), (readingThreads.get(Thread.currentThread()) - 1));
			} else {
				readingThreads.remove(Thread.currentThread());
			}
			notifyAll();
		} else {
			System.out.println("no reading threads");
		}

	}

	/**
	 * Blocking method that calls tryAcquiringWriteLock and returns only when
	 * the write lock has been acquired, otherwise waits.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void lockWrite() {
		// FILL IN CODE
		while (!tryAcquiringWriteLock()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Releases the write lock held by the current thread. Decrement the no. of
	 * writing threads if the count is more than 1. Else, remove the record from
	 * the map.
	 * 
	 * Decrement the count if writing threads map already has the current
	 * thread. Else remove the record from the map. In the end, notify other
	 * threads
	 */

	public synchronized void unlockWrite() {
		// FILL IN CODE
		if (writingThreads.containsKey(Thread.currentThread())) {
			if (writingThreads.get(Thread.currentThread()) > 1) {
				writingThreads.put(Thread.currentThread(), (writingThreads.get(Thread.currentThread()) - 1));
			} else {
				writingThreads.remove(Thread.currentThread());
			}
			notifyAll();
		} else {
			System.out.println("no writing threads");
		}
	}
}
