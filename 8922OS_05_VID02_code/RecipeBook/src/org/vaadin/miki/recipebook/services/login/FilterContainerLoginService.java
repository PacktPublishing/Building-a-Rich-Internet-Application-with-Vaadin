package org.vaadin.miki.recipebook.services.login;

import java.util.ArrayList;

import org.vaadin.miki.recipebook.services.ContainerService;
import org.vaadin.miki.recipebook.services.LoginService;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.UI;

public class FilterContainerLoginService implements LoginService {

	private final ContainerService containerService;

	private final ArrayList<LoginListener> listeners = new ArrayList<LoginService.LoginListener>();

	public FilterContainerLoginService(ContainerService containerService) {
		this.containerService = containerService;
	}

	@Override
	public void logIn(String user, String password) {
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
