package org.vaadin.miki.recipebook.services;

import org.vaadin.miki.recipebook.services.login.FilterContainerLoginService;
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
	 * @return
	 */
	public LoginService getLoginService() {
		if(this.loginService == null)
		  this.loginService = new FilterContainerLoginService(this.getContainerService());
		return this.loginService;
	}
	
}
