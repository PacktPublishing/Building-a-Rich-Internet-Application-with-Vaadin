package org.vaadin.miki.tagfield.client.tagfield;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface TagFieldServerRpc extends ServerRpc {

	// TODO example API
	public void clicked(MouseEventDetails mouseDetails);

}
