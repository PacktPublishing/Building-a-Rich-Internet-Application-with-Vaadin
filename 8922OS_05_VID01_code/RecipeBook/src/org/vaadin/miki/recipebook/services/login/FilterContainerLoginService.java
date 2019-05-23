package org.vaadin.miki.recipebook.services.login;

import java.util.ArrayList;

import org.vaadin.miki.recipebook.services.ContainerService;
import org.vaadin.miki.recipebook.services.LoginService;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;

public class FilterContainerLoginService implements LoginService {

	private final ContainerService containerService;

	private final ArrayList<LoginListener> listeners = new ArrayList<LoginService.LoginListener>();

	private User currentUser;

	public FilterContainerLoginService(ContainerService containerService) {
		this.containerService = containerService;
	}

	@Override
	public boolean logIn(String user, String password) {
		Container container = this.containerService.getUsersContainer();

		User userdata = null;

		if (container instanceof Container.Filterable) {
			Container.Filterable filterable = (Container.Filterable) container;
			filterable.removeAllContainerFilters();
			filterable.addContainerFilter(new Compare.Equal("email", user));
			filterable
					.addContainerFilter(new Compare.Equal("passwd", password));
			if (filterable.size() == 1)
				userdata = new User(user);
			filterable.removeAllContainerFilters();
		} else {
			for (Object userId : container.getItemIds()) {
				Item item = container.getItem(userId);
				if (user.equals(item.getItemProperty("email"))
						&& password.equals(item.getItemProperty("passwd")))
					userdata = new User(user);
			}
		}
		if (userdata != null) {
			this.setCurrentUser(userdata);
			this.fireUserLoggedIn();
		}
		return userdata != null;
	}

	protected void fireUserLoggedIn() {
		LoginEvent event = new LoginEvent(this, this.getCurrentUser());
		for (LoginListener listener : this.listeners)
			listener.userLoggedIn(event);
	}

	protected void fireUserLoggedOut(User user) {
		LoginEvent event = new LoginEvent(this, this.getCurrentUser());
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

	public User getCurrentUser() {
		return currentUser;
	}

	protected void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
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
