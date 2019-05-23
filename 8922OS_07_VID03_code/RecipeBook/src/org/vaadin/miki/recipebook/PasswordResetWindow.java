package org.vaadin.miki.recipebook;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Window for resetting password.
 * @author Miki
 *
 */
public class PasswordResetWindow extends Window {

	public PasswordResetWindow() {
		
		final TextField email = new TextField("Enter your email address");
		
		final Button cancel = new Button("Close", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		final Button confirm = new Button("Reset password", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(!email.getValue().isEmpty()) {
					close();
					Notification.show("Password changed", "We have sent the instructions on how to reset the password to "+email.getValue(), Notification.Type.WARNING_MESSAGE);
				}
			}
		});
		
		VerticalLayout contents = new VerticalLayout(email, new HorizontalLayout(cancel, confirm));		
		this.setContent(contents);
		this.setModal(true);
		this.center();
	}
	
}
