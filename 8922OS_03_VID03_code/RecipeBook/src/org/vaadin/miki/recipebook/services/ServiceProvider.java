package org.vaadin.miki.recipebook.services;

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

}
