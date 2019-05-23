package org.vaadin.miki.tagfield.client.tagfield;

import com.vaadin.shared.communication.ServerRpc;

public interface TagFieldServerRpc extends ServerRpc {

	public void tagClicked(String tagName);

}
