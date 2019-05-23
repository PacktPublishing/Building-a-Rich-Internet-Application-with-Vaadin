package org.vaadin.miki.recipebook.views.register;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
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
		
		for(Field<?> f: new Field<?>[]{this.email, this.password, this.retyped, this.acceptTerms}) {
			f.setRequired(true);
			f.setRequiredError("This field is required.");
		}
		
		this.email.addValidator(new EmailValidator("Unrecognised format of email address."));
		this.password.addValidator(new Validator() {
			
			@Override
			public void validate(Object value) throws InvalidValueException {
				String pass = value == null ? "" : value.toString();
				if(pass.length() < 6) throw new InvalidValueException("Password must be at least 6 characteres long.");
				if(!pass.matches("\\d")) throw new InvalidValueException("Password must contain at least 1 digit.");
				if(!pass.matches("[a-zA-Z]")) throw new InvalidValueException("Password must contain at least 1 letter.");
			}
		});

		this.retyped.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				String retyped = value == null ? "" : value.toString();
				if(!retyped.equals(password.getValue())) throw new InvalidValueException("Retyped password does not match.");
			}
			
		});
		
	}
	
	@Override
	public void enter(ViewChangeEvent event) {

	}

}
