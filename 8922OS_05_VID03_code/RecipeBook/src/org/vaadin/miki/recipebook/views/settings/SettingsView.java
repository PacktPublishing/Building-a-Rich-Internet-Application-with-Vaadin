package org.vaadin.miki.recipebook.views.settings;

import org.vaadin.miki.recipebook.services.LoginService.LoginEvent;
import org.vaadin.miki.recipebook.services.LoginService.LoginListener;
import org.vaadin.miki.recipebook.services.LoginService.User;
import org.vaadin.miki.recipebook.services.ServiceProvider;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SettingsView extends VerticalLayout implements View, LoginListener {

	private final TextField name = new TextField("Your name");
	private final TextField email = new TextField("Your email");
	private final PasswordField password = new PasswordField("New password");

	private final Button save = new Button("Save settings");
	private final Button restore = new Button("Revert changes");
	private final Button recipes = new Button("Your recipes");

	public SettingsView() {
		super();
		this.addComponent(this.name);
		this.addComponent(this.email);
		this.addComponent(this.password);
		HorizontalLayout buttons = new HorizontalLayout(this.save,
				this.restore, this.recipes);
		this.save.setSizeFull();
		this.restore.setSizeFull();
		this.recipes.setSizeFull();
		buttons.setWidth("450px");
		buttons.setSpacing(true);
		this.addComponent(buttons);
		ServiceProvider.getInstance().getLoginService().addLoginListener(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		User user = ServiceProvider.getInstance().getLoginService()
				.getCurrentUser();
		// do not show this view if the user is not logged in
		if (user == null)
			event.getNavigator().navigateTo("register");
		else {
			; // bind components to data of the current user
		}
	}

	@Override
	public void userLoggedIn(LoginEvent e) {
		;
	}

	@Override
	public void userLoggedOut(LoginEvent e) {
		UI.getCurrent().getPage().reload();
	}

	@Override
	public void userLoginFailed(LoginEvent e) {
		;
	}

}
