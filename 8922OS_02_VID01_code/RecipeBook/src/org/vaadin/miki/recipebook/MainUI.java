package org.vaadin.miki.recipebook;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class MainUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		final TextField field = new TextField("Type something:");
		field.setImmediate(true);

		final Button button = new Button("Click Me");
		button.setEnabled(false);

		field.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				button.setEnabled(!event.getText().isEmpty());
			}
		});

		field.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				button.setEnabled(!field.getValue().isEmpty());
			}
		});

		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label(field.getValue()));
				field.setValue("");
			}
		});
		layout.addComponent(field);
		layout.addComponent(button);
	}

}