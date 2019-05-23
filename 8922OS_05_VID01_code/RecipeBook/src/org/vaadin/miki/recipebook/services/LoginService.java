package org.vaadin.miki.recipebook.services;

/**
 * An interface for a service that manages logging in users.
 * @author miki
 *
 */
public interface LoginService {

	/**
	 * Attempts to log in a user with a given name and password.
	 * @param user Username.
	 * @param password Password.
	 * @return <b>true</b> when the user has been successfully logged in, <b>false</b> otherwise.
	 */
	public boolean logIn(String user, String password);

	/**
	 * Logs out current user. No effect when there is no user.
	 */
	public void logOut();
	
	/**
	 * Adds a login listener.
	 * @param listener Listener.
	 */
	public void addLoginListener(LoginListener listener);
	
	/**
	 * Removes a login listener.
	 * @param listener Listener.
	 */
	public void removeLoginListener(LoginListener listener);
	
	/**
	 * Class that holds information about a user.
	 * @author miki
	 *
	 */
	public static final class User {
		private final String username;
		
		public User(String username) {
			this.username = username;
		}
		
		public String getUsername() {
			return this.username;
		}
	}
	
	/**
	 * Interface for listeners to login events.
	 * @author miki
	 *
	 */
	public static interface LoginListener {
		public void userLoggedIn(LoginEvent e);
		public void userLoggedOut(LoginEvent e);
	}
	
	/**
	 * Event that holds information about what happened during logging in.
	 * @author miki
	 *
	 */
	public static final class LoginEvent {
		private final User user;
		private final LoginService source;
		public LoginEvent(LoginService source, User user) {
			this.user = user;
			this.source = source;
		}
		public User getUser() {
			return this.user;
		}
		public LoginService getSource() {
			return this.source;
		}
	}
	
}
