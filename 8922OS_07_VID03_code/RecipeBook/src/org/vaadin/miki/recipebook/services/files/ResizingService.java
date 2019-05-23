package org.vaadin.miki.recipebook.services.files;

import java.io.File;
import java.util.ArrayList;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.vaadin.miki.recipebook.services.ImageService;
import org.vaadin.miki.recipebook.services.ServiceProvider;

public class ResizingService implements ImageService {

	private final ArrayList<ProcessingListener> listeners = new ArrayList<ImageService.ProcessingListener>();

	@Override
	public void addProcessingListener(ProcessingListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeProcessingListener(ProcessingListener listener) {
		this.listeners.remove(listener);
	}

	protected void fireProcessingSuccess(File file) {
		ProcessingEvent event = new ProcessingEvent(file);
		for (ProcessingListener listener : this.listeners)
			listener.processingSuccess(event);
	}

	protected void fireProcessingFailure(File file, Exception cause) {
		ProcessingEvent event = new ProcessingEvent(file);
		for (ProcessingListener listener : this.listeners)
			listener.processingFailure(event, cause);
	}

	@Override
	public void processFile(final File file) {
		ServiceProvider.getInstance().getThreadService().run(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);

					// important: on Windows-based systems make sure
					// IM4JAVA_TOOLPATH is
					// set as environment variable, even if IM is in your PATH
					// eclipse restart is needed if you modified your system
					// variables
					// while it was running

					// create command
					ConvertCmd cmd = new ConvertCmd();

					// create the operation, add images and operators/options
					IMOperation op = new IMOperation();
					op.addImage(file.getAbsolutePath());
					op.resize(320, 240);
					op.addImage(file.getAbsolutePath());

					// useful for debugging
					// replace path to your liking and see if the script runs
					// cmd.createScript("q:/resizing.sh", op);
					// execute the operation
					cmd.run(op);
					// report success
					fireProcessingSuccess(file);
				} catch (Exception e) {
					e.printStackTrace();
					fireProcessingFailure(file, e);
				}
			}
		});
	}
}
