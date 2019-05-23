package org.vaadin.miki.recipebook.services;

import java.io.File;

/**
 * Interface for a service that checks if a given file is an image and resizes it to proper dimensions.
 * @author miki
 *
 */
public interface ImageService {

	public static final class ProcessingEvent {
		private final File file;
		public ProcessingEvent(File file) {
			this.file = file;
		}
		public File getFile() {
			return this.file;
		}
	}
	
	public static interface ProcessingListener {
		public void processingSuccess(ProcessingEvent event);
		public void processingFailure(ProcessingEvent event, Exception cause);
	}
	
	public void addProcessingListener(ProcessingListener listener);
	public void removeProcessingListener(ProcessingListener listener);
	
	/**
	 * Processes a file. Broadcasts an event once processing is done.
	 * @param file File to process.
	 */
	public void processFile(File file);
}
