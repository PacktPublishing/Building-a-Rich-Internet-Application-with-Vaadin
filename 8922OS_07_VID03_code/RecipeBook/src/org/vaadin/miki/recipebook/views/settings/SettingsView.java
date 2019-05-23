package org.vaadin.miki.recipebook.views.settings;

import org.vaadin.miki.recipebook.services.LoginService.LoginEvent;
import org.vaadin.miki.recipebook.services.LoginService.LoginListener;
import org.vaadin.miki.recipebook.services.LoginService.User;
import org.vaadin.miki.recipebook.services.ServiceProvider;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class SettingsView extends VerticalLayout implements View, LoginListener {

	private final TextField name = new TextField("Your name");
	private final TextField email = new TextField("Your email");
	private final PasswordField password = new PasswordField("New password");

	private final Button save = new Button("Save settings");
	private final Button restore = new Button("Revert changes");
	private final Button recipes = new Button("Your recipes");
	private final Button add = new Button("Add recipe", new Button.ClickListener() {
		
		@Override
		public void buttonClick(ClickEvent event) {
			addRecipe();
		}
	});
	
	private Navigator navigator;

	public SettingsView() {
		super();
		this.addComponent(this.name);
		this.addComponent(this.email);
		this.addComponent(this.password);
		HorizontalLayout buttons = new HorizontalLayout(this.save,
				this.restore, this.recipes, this.add);
		this.save.setSizeFull();
		this.restore.setSizeFull();
		this.recipes.setSizeFull();
		this.add.setSizeFull();
		
		buttons.setWidth("450px");
		buttons.setSpacing(true);
		this.addComponent(buttons);
		ServiceProvider.getInstance().getLoginService().addLoginListener(this);
	}

	/**
	 * Handles adding recipe. Redirects away to a recipe view, showing newly created recipe.
	 * Displays notification if something goes wrong.
	 */
	private void addRecipe() {
		String username = ServiceProvider.getInstance().getLoginService().getCurrentUser().getUsername();
		Object newRecipeId = ServiceProvider.getInstance().getContainerService().addRecipe(username);
		if(newRecipeId == null)
			Notification.show("Could not add recipe", "More information available in system log.", Notification.Type.ERROR_MESSAGE);
		else
			this.navigator.navigateTo("food/"+newRecipeId.toString());
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		User user = ServiceProvider.getInstance().getLoginService()
				.getCurrentUser();
		this.navigator = event.getNavigator();
		
		// do not show this view if the user is not logged in
		if (user == null)
			this.navigator.navigateTo("register");
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
