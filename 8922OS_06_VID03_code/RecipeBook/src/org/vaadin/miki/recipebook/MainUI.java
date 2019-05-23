package org.vaadin.miki.recipebook;

import org.vaadin.miki.recipebook.services.LoginService;
import org.vaadin.miki.recipebook.services.LoginService.LoginEvent;
import org.vaadin.miki.recipebook.services.LoginService.User;
import org.vaadin.miki.recipebook.services.ServiceProvider;
import org.vaadin.miki.recipebook.theme.RecipeBookTheme;
import org.vaadin.miki.recipebook.views.food.FoodView;
import org.vaadin.miki.recipebook.views.register.RegisterView;
import org.vaadin.miki.recipebook.views.search.SearchView;
import org.vaadin.miki.recipebook.views.settings.SettingsView;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

/**
 * Main UI class
 */
@Theme("recipebooktheme")
@SuppressWarnings("serial")
public class MainUI extends UI implements LoginService.LoginListener {

	private LoginService loginService;
	private final CssLayout userFormSpace = new CssLayout();

	@Override
	protected void init(VaadinRequest request) {
		VaadinService.getCurrent().addSessionDestroyListener(
				new SessionDestroyListener() {

					@Override
					public void sessionDestroy(SessionDestroyEvent event) {
						ServiceProvider.getInstance().getThreadService()
								.stopAllThreads();
					}
				});
		this.loginService = ServiceProvider.getInstance().getLoginService();
		this.loginService.addLoginListener(this);

		final CssLayout layout = new CssLayout();
		final CssLayout topBar = new CssLayout();
		final CssLayout viewLayout = new CssLayout();

		topBar.setHeight("90px");
		topBar.setWidth("100%");
		viewLayout.setSizeFull();

		layout.addComponent(topBar);
		layout.addComponent(viewLayout);
		layout.setSizeFull();

		this.setContent(layout);

		final Navigator navigator = new Navigator(this, viewLayout);
		navigator.addView("food", FoodView.class);
		navigator.addView("register", RegisterView.class);
		navigator.addView("settings", SettingsView.class);
		navigator.addView("search", SearchView.class);
		navigator.addView("", SearchView.class);

		for (String s : new String[] { "food", "register", "settings", "search" })
			topBar.addComponent(this.createNavigationButton(s, navigator));

		User user = ServiceProvider.getInstance().getLoginService()
				.getCurrentUser();

		if (user == null)
			this.userFormSpace.addComponent(this.createLoginForm());
		else
			this.userFormSpace.addComponent(this.createLogoutPanel(user));

		topBar.addComponent(this.userFormSpace);
	}

	private Button createNavigationButton(final String state,
			final Navigator navigator) {
		return new Button("Go to " + state, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				navigator.navigateTo(state);
			}
		});
	}

	private Component createLoginForm() {
		GridLayout loginForm = new GridLayout(2, 2);
		final TextField email = new TextField("Email");
		email.addStyleName(RecipeBookTheme.VISIBLE_FORM_ITEM);
		email.addStyleName(RecipeBookTheme.CENTER);
		final PasswordField password = new PasswordField("Password");
		password.addStyleName(RecipeBookTheme.VISIBLE_FORM_ITEM);
		password.addStyleName(RecipeBookTheme.CENTER);
		Button login = new Button("Log in");
		login.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				loginService.logIn(email.getValue(), password.getValue());
			}
		});

		Button forgot = new Button("Forgot password?");
		forgot.addStyleName(Runo.BUTTON_LINK);
		forgot.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Window resetWindow = new PasswordResetWindow();
				UI.getCurrent().addWindow(resetWindow);
			}
		});

		loginForm.addComponent(email);
		loginForm.addComponent(password);
		loginForm.addComponent(login);
		loginForm.addComponent(forgot);

		return loginForm;
	}

	private Component createLogoutPanel(LoginService.User user) {
		final Label label = new Label("Welcome, " + user.getUsername());
		label.addStyleName("welcomelabel");
		final Button logout = new Button("Log out", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				loginService.logOut();
			}
		});
		logout.addStyleName("logoutbutton");
		HorizontalLayout result = new HorizontalLayout(label, logout);
		result.setSpacing(true);
		return result;
	}

	@Override
	public void userLoggedIn(LoginEvent e) {
		Notification.show("Login successful!");
		this.userFormSpace.removeAllComponents();
		this.userFormSpace.addComponent(this.createLogoutPanel(e.getUser()));
	}

	@Override
	public void userLoggedOut(LoginEvent e) {
		this.userFormSpace.removeAllComponents();
		this.userFormSpace.addComponent(this.createLoginForm());
		Notification.show("You are now logged out.");
	}

	@Override
	public void userLoginFailed(LoginEvent e) {
		Notification.show("Wrong email and/or password. Try again.",
				Notification.Type.WARNING_MESSAGE);
	}
}