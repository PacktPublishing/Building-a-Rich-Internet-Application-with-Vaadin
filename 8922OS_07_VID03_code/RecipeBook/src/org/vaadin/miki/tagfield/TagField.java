package org.vaadin.miki.tagfield;

import java.util.ArrayList;

import org.vaadin.miki.tagfield.client.tagfield.TagFieldClientRpc;
import org.vaadin.miki.tagfield.client.tagfield.TagFieldServerRpc;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractField;

import org.vaadin.miki.tagfield.client.tagfield.TagFieldState;

public class TagField extends AbstractField<String> {

	public static final class TagClickEvent {
		private final TagField source;
		private final String tag;
		public TagClickEvent(TagField source, String tag) {
			this.source = source;
			this.tag = tag;
		}
		public String getTag() {
			return tag;
		}
		public TagField getSource() {
			return source;
		}
	}
	
	public static interface TagClickListener {
		public void tagClicked(TagClickEvent event);
	}
	
	private final TagFieldServerRpc rpc = new TagFieldServerRpc() {

		@Override
		public void tagClicked(String tagName) {
			fireTagClicked(tagName);
		}

		}; 
	
	private final ArrayList<TagClickListener> listeners = new ArrayList<TagClickListener>();
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
		this.fireValueChange(false);
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
		this.fireValueChange(false);
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
		this.updateTags();
		this.fireValueChange(false);
	}
	
	protected void fireTagClicked(String tag) {
		final TagClickEvent event = new TagClickEvent(this, tag);
		for(TagClickListener listener: this.listeners)
			listener.tagClicked(event);
	}
	
	public void addTagClickListener(TagClickListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeTagClickListener(TagClickListener listener) {
		this.listeners.remove(listener);
	}
}
