package org.vaadin.miki.recipebook.views.register;

import org.vaadin.miki.recipebook.MainUI;
import org.vaadin.miki.recipebook.services.LoginService.LoginEvent;
import org.vaadin.miki.recipebook.services.LoginService.LoginListener;
import org.vaadin.miki.recipebook.services.ServiceProvider;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class RegisterView extends FormLayout implements View,
		Button.ClickListener, LoginListener {

	private TextField email = new TextField("Email address");
	private PasswordField password = new PasswordField("Password");
	private PasswordField retyped = new PasswordField("Retype");
	private CheckBox acceptTerms = new CheckBox(
			"I accept terms and conditions.");

	private Button register = new Button("Register", this);

	private Container container;

	private FieldGroup fieldGroup = new FieldGroup();

	public RegisterView() {
		super();
		this.addComponent(this.email);
		this.addComponent(this.password);
		this.addComponent(this.retyped);
		this.addComponent(this.acceptTerms);

		this.addComponent(this.register);
		this.register.setVisible(false);

		for (Field<?> f : new Field<?>[] { this.email, this.password,
				this.retyped, this.acceptTerms }) {
			f.setRequired(true);
			f.setRequiredError("This field is required.");
		}

		this.email.addValidator(new EmailValidator(
				"Unrecognised format of email address."));
		this.password.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				String pass = value == null ? "" : value.toString();
				if (pass.length() < 6)
					throw new InvalidValueException(
							"Password must be at least 6 characteres long.");
				if (!pass.matches(".*[0-9].*"))
					throw new InvalidValueException(
							"Password must contain at least 1 digit.");
				if (!pass.matches(".*[a-zA-Z].*"))
					throw new InvalidValueException(
							"Password must contain at least 1 letter.");
			}
		});

		this.retyped.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				String retyped = value == null ? "" : value.toString();
				if (!retyped.equals(password.getValue()))
					throw new InvalidValueException(
							"Retyped password does not match.");
			}

		});

		this.acceptTerms
				.addValueChangeListener(new Property.ValueChangeListener() {

					@Override
					public void valueChange(Property.ValueChangeEvent event) {
						register.setVisible(acceptTerms.getValue());
					}
				});

		this.fieldGroup.bind(this.email, "email");
		this.fieldGroup.bind(this.password, "passwd");

		ServiceProvider.getInstance().getLoginService().addLoginListener(this);
		
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// show this view only when there is no user logged in
		if (ServiceProvider.getInstance().getLoginService().getCurrentUser() == null) {

			this.container = ServiceProvider.getInstance()
					.getContainerService().getUsersContainer();
			Object newUserId = this.container.addItem();
			if (newUserId == null)
				Notification.show("Could not add user to the database.",
						Notification.Type.ERROR_MESSAGE);
			else {
				Item newUser = this.container.getItem(newUserId);
				this.fieldGroup.setItemDataSource(newUser);
			}
		} else
			// navigate to settings when the user is logged in
			event.getNavigator().navigateTo("settings");
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (this.fieldGroup.isValid()) {
			try {
				this.fieldGroup.commit();
				this.register.setEnabled(false);
				Notification.show("Your account has been created.");
			} catch (CommitException e) {
				e.printStackTrace();
				Notification.show("The data could not be saved.",
						"Error message: " + e.getMessage(),
						Notification.Type.ERROR_MESSAGE);
			}
		} else
			Notification.show("One or more fields contain invalid values.");
	}

	@Override
	public void userLoggedIn(LoginEvent e) {
		UI.getCurrent().getPage().reload();
	}

	@Override
	public void userLoggedOut(LoginEvent e) {
		;
	}

	@Override
	public void userLoginFailed(LoginEvent e) {
		;
	}

}
