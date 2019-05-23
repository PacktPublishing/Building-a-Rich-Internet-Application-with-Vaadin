package org.vaadin.miki.tagfield.client.tagfield;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class TagFieldWidget extends HorizontalPanel {

	public static final String CLASSNAME = "tagfield";

	public static interface TagClickListener {
		public void tagClicked(String tag);
	}
	
	private Button[] buttons = new Button[0];
	private final HashMap<Button, HandlerRegistration> handlers = new HashMap<Button, HandlerRegistration>();
	
	private final ArrayList<TagFieldWidget.TagClickListener> listeners = new ArrayList<TagFieldWidget.TagClickListener>();
	
	private final ClickHandler buttonHandler = new ClickHandler() {		
		@Override
		public void onClick(ClickEvent event) {
			fireClickEvent(((Button)event.getSource()).getText());
		}
	};

	public TagFieldWidget() {

		// setText("TagField sets the text via TagFieldConnector using TagFieldState");
		setStyleName(CLASSNAME);

	}

	protected void fireClickEvent(String tag) {
		for(TagClickListener listener: this.listeners)
			listener.tagClicked(tag);
	}
	
	private void removeButtons() {
		// clear buttons
		for(Button b: this.buttons) {
			this.remove(b);
			if(this.handlers.containsKey(b))
				this.handlers.remove(b).removeHandler();
		}
		
	}
	
	public void createButtons(String[] captions) {
		this.removeButtons();
		for(String s: captions) {
			Button tagButton = new Button();
			tagButton.setText(s);
			this.handlers.put(tagButton, tagButton.addClickHandler(this.buttonHandler));
			this.add(tagButton);
		}
	}
	
	public void addTagClickListener(TagClickListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeTagClickListener(TagClickListener listener) {
		this.listeners.remove(listener);
	}
	
}