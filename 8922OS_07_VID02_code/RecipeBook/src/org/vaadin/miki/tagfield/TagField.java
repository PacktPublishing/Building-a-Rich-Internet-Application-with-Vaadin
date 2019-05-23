package org.vaadin.miki.tagfield;

import org.vaadin.miki.tagfield.client.tagfield.TagFieldClientRpc;
import org.vaadin.miki.tagfield.client.tagfield.TagFieldServerRpc;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractField;

import org.vaadin.miki.tagfield.client.tagfield.TagFieldState;

public class TagField extends AbstractField<String> {

	private TagFieldServerRpc rpc = new TagFieldServerRpc() {
		private int clickCount = 0;

		public void clicked(MouseEventDetails mouseDetails) {
//			// nag every 5:th click using RPC
//			if (++clickCount % 5 == 0) {
//				getRpcProxy(TagFieldClientRpc.class).alert(
//						"Ok, that's enough!");
//			}
//			// update shared state
//			getState().text = "You have clicked " + clickCount + " times";
		}
	};  

	private String tagSeparator = "\\s*,\\s*";
	
	public TagField() {
		registerRpc(rpc);
	}

	private void updateTags() {
		String value = this.getValue();
		if(value == null) this.getState().tags = new String[0];
		else if(this.getTagSeparator() == null) this.getState().tags = new String[]{value};
		else this.getState().tags = this.getValue().split(this.getTagSeparator());
		this.markAsDirty();
	}
	
	public int getTagCount() {
		return getState().tags.length;
	}
	
	public String getTag(int index) {
		return getState().tags[index];
	}
	
	public void setTag(int index, String tag) {
		getState().tags[index] = tag;
		this.markAsDirty();
	}
	
	public void setTagCount(int count) {
		String[] tags = new String[count];
		int min = Math.min(count, this.getTagCount());
		for(int zmp1=0; zmp1<min; zmp1++)
			tags[zmp1] = getTag(zmp1);
		for(int zmp1=min; zmp1<count; zmp1++)
			tags[zmp1] = "";
		getState().tags = tags;
		this.markAsDirty();
	}
	
	@Override
	protected void setInternalValue(String newValue) {
		super.setInternalValue(newValue);
		this.updateTags();
	}
	
	@Override
	public TagFieldState getState() {
		return (TagFieldState) super.getState();
	}

	@Override
	public Class<? extends String> getType() {
		return String.class;
	}

	public String getTagSeparator() {
		return tagSeparator;
	}

	public void setTagSeparator(String tagSeparator) {
		this.tagSeparator = tagSeparator;
	}
}
