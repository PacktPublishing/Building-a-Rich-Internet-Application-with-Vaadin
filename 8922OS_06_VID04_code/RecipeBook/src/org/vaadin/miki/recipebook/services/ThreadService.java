package org.vaadin.miki.recipebook.services;

/**
 * Service for running background threads.
 * @author miki
 *
 */
public interface ThreadService {

	/**
	 * Runs given runnable in a background thread.
	 * @param runnable Runnable.
	 */
	public void run(Runnable runnable);
	
	/**
	 * Stops (interrupts) all currently running threads.
	 */
	public void stopAllThreads();
}
