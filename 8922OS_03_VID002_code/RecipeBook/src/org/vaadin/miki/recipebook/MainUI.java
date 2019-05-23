package org.vaadin.miki.recipebook;

import org.vaadin.miki.recipebook.views.food.FoodView;
import org.vaadin.miki.recipebook.views.register.RegisterView;
import org.vaadin.miki.recipebook.views.search.SearchView;
import org.vaadin.miki.recipebook.views.settings.SettingsView;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class MainUI extends UI {

	@Override
	protected void init(VaadinRequest request) {

		final VerticalLayout layout = new VerticalLayout();
		final CssLayout topBar = new CssLayout();
		final CssLayout viewLayout = new CssLayout();

		layout.addComponent(topBar);
		layout.addComponent(viewLayout);

		this.setContent(layout);

		final Navigator navigator = new Navigator(this, viewLayout);
		navigator.addView("food", FoodView.class);
		navigator.addView("register", RegisterView.class);
		navigator.addView("settings", SettingsView.class);
		navigator.addView("search", SearchView.class);
		navigator.addView("", SearchView.class);

		for (String s : new String[] { "food", "register", "settings", "search" })
			topBar.addComponent(this.createNavigationButton(s, navigator));
		
		topBar.addComponent(this.createLoginForm());
	}

	private Button createNavigationButton(final String state, final Navigator navigator) {
		return new Button("Go to " + state, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				navigator.navigateTo(state);
			}
		});
	}
	
	private Component createLoginForm() {
		GridLayout loginForm = new GridLayout(2, 2);
		TextField email = new TextField("Email");
		PasswordField password = new PasswordField("Password");
		
		Button login = new Button("Log in");
		login.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Logging in is not yet supported.", Notification.Type.ERROR_MESSAGE);
			}
		});
		
		Button forgot = new Button("Forgot password?");
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

}