package org.vaadin.miki.recipebook.services.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.miki.recipebook.services.ContainerService;
import org.vaadin.miki.recipebook.services.LoginService;

import com.vaadin.data.Container;
import com.vaadin.ui.UI;

public class FilterContainerLoginService implements LoginService {

	private final ContainerService containerService;

	private final ArrayList<LoginListener> listeners = new ArrayList<LoginService.LoginListener>();

	public FilterContainerLoginService(ContainerService containerService) {
		this.containerService = containerService;
	}

	@Override
	public void logIn(String user, String password) {
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put(ContainerService.USER_EMAIL_ATTRIBUTE, user);
		queryMap.put(ContainerService.USER_PASSWORD_ATTRIBUTE, password);
		
		Container userContainer = this.containerService.getUsersContainer(queryMap, true);

		User userdata = null;
		if(userContainer.size() == 1)
			userdata = new User(user);

		if (userdata != null) {
			this.setCurrentUser(userdata);
			this.fireUserLoggedIn();
		}
		else this.fireUserLoginFailed();
	}

	protected void fireUserLoggedIn() {
		LoginEvent event = new LoginEvent(this, this.getCurrentUser());
		for (LoginListener listener : this.listeners)
			listener.userLoggedIn(event);
	}

	protected void fireUserLoginFailed() {
		LoginEvent event = new LoginEvent(this, null);
		for(LoginListener listener : this.listeners)
			listener.userLoginFailed(event);
	}
	
	protected void fireUserLoggedOut(User user) {
		LoginEvent event = new LoginEvent(this, user);
		for (LoginListener listener : this.listeners)
			listener.userLoggedOut(event);
	}

	@Override
	public void addLoginListener(LoginListener listener) {
		if (!this.listeners.contains(listener))
			this.listeners.add(listener);
	}

	@Override
	public void removeLoginListener(LoginListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public User getCurrentUser() {
		// check if the current user is in the session
		return UI.getCurrent().getSession().getAttribute(User.class);
	}

	protected void setCurrentUser(User currentUser) {
		// store information in the session
		UI.getCurrent().getSession().setAttribute(User.class, currentUser);
	}

	@Override
	public void logOut() {
		User user = this.getCurrentUser();
		if (user != null) {
			this.setCurrentUser(null);
			this.fireUserLoggedOut(user);
		}
	}

}
