package org.vaadin.miki.recipebook.services;

import org.vaadin.miki.recipebook.services.files.LocalFileService;
import org.vaadin.miki.recipebook.services.files.ResizingService;
import org.vaadin.miki.recipebook.services.login.FilterContainerLoginService;
import org.vaadin.miki.recipebook.services.pool.PoolThreadService;
import org.vaadin.miki.recipebook.services.sql.SqlContainerService;

/**
 * Interface for obtaining different services available in the recipe book.
 * 
 * @author miki
 * 
 */
public class ServiceProvider {

	private ServiceProvider() {
		;
	}

	private static final ServiceProvider INSTANCE = new ServiceProvider();

	public static final ServiceProvider getInstance() {
		return INSTANCE;
	}

	private LoginService loginService = null;
	private ImageService imageService = null;
	private ThreadService threadService = null;

	/**
	 * Returns current instance of the container service.
	 * 
	 * @return Container service.
	 */
	public ContainerService getContainerService() {
		try {
			return new SqlContainerService();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a new instance of the login service.
	 * 
	 * @return
	 */
	public LoginService getLoginService() {
		if (this.loginService == null)
			this.loginService = new FilterContainerLoginService(
					this.getContainerService());
		return this.loginService;
	}

	public FileService getFileService() {
		return new LocalFileService();
	}

	public ImageService getImageService() {
		if (this.imageService == null)
			this.imageService = new ResizingService();
		return this.imageService;
	}

	public ThreadService getThreadService() {
		if(this.threadService == null)
			this.threadService = new PoolThreadService();
		return this.threadService;
	}
}
