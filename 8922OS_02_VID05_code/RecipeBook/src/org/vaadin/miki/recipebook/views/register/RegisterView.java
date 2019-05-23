package org.vaadin.miki.recipebook.views.register;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class RegisterView extends FormLayout implements View {

	private TextField email = new TextField("Email address");
	private PasswordField password = new PasswordField("Password");
	private PasswordField retyped = new PasswordField("Retype");
	private CheckBox acceptTerms = new CheckBox("I accept terms and conditions.");
	
	public RegisterView() {
		super();
		this.addComponent(this.email);
		this.addComponent(this.password);
		this.addComponent(this.retyped);
		this.addComponent(this.acceptTerms);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {

	}

}
