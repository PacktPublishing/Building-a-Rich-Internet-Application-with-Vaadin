package org.vaadin.miki.tagfield.client.tagfield;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

import org.vaadin.miki.tagfield.TagField;
import org.vaadin.miki.tagfield.client.tagfield.TagFieldWidget;
import org.vaadin.miki.tagfield.client.tagfield.TagFieldServerRpc;

import com.vaadin.client.communication.RpcProxy;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.client.MouseEventDetailsBuilder;

import org.vaadin.miki.tagfield.client.tagfield.TagFieldClientRpc;
import org.vaadin.miki.tagfield.client.tagfield.TagFieldState;

import com.vaadin.client.communication.StateChangeEvent;

@Connect(TagField.class)
public class TagFieldConnector extends AbstractComponentConnector {

	TagFieldServerRpc rpc = RpcProxy
			.create(TagFieldServerRpc.class, this);
	
	public TagFieldConnector() {

		getWidget().addTagClickListener(new TagFieldWidget.TagClickListener() {
			
			@Override
			public void tagClicked(String tag) {
				rpc.tagClicked(tag);
			}
		});

	}

	@Override
	protected Widget createWidget() {
		return GWT.create(TagFieldWidget.class);
	}

	@Override
	public TagFieldWidget getWidget() {
		return (TagFieldWidget) super.getWidget();
	}

	@Override
	public TagFieldState getState() {
		return (TagFieldState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		// TODO do something useful
		final String[] tags = getState().tags;
		getWidget().createButtons(tags);
	}

}

