package org.vaadin.miki.recipebook.services.pool;

import java.util.HashSet;
import java.util.Set;

import org.vaadin.miki.recipebook.services.ThreadService;

public class PoolThreadService implements ThreadService {

	private final class LocalThread extends Thread {
		private final Runnable runnable;

		public LocalThread(Runnable runnable) {
			super();
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
				threads.add(this);
				super.run();
				this.runnable.run();
			} finally {
				threads.remove(this);
			}
		}
	}

	private final Set<LocalThread> threads = new HashSet<LocalThread>();

	@Override
	public void run(Runnable runnable) {
		new LocalThread(runnable).start();
	}

	@Override
	public synchronized void stopAllThreads() {
		for(LocalThread thread: threads)
			if(thread.isAlive())
				thread.interrupt();
	}

}
