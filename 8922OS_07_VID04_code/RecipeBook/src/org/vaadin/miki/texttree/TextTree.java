package org.vaadin.miki.texttree;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Tree;

public class TextTree extends CustomField<String> {

	private final Tree tree= new Tree();
	
	@Override
	protected Component initContent() {
		this.tree.setSizeFull();
		this.tree.setSelectable(false);
		return this.tree;
	}

	@Override
	protected void setInternalValue(String newValue) {
		this.updateTree(newValue);
		super.setInternalValue(newValue);
	}
	
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		super.setPropertyDataSource(newDataSource);
	}
	
	private int countLeadingSpaces(String string) {
		int result = 0;
		if(string.length() > 0)
			while(string.charAt(result) == ' ')
				result++;
		return result;
	}
	
	private String removeLeadingSpaces(String string, int spaces) {
		return string.isEmpty() ? string : string.substring(spaces);
	}
	
	private void updateTree(String text) {
		String[] lines = text.split("\\s*\\n");
		HierarchicalContainer container = new HierarchicalContainer();
		
		if(lines.length > 0) {
			int baseLevel = this.countLeadingSpaces(lines[0]);
			int lastLevel = baseLevel;
			Object lastAddedElement = this.removeLeadingSpaces(lines[0], lastLevel);
			container.addItem(lastAddedElement);
			for(int zmp1=1; zmp1<lines.length; zmp1++) {
				int currentLevel = this.countLeadingSpaces(lines[zmp1]);
				Object addedItem = this.removeLeadingSpaces(lines[zmp1], currentLevel);
				container.addItem(addedItem);
				// if the level is more than last added level, parent is recently added item
				if(currentLevel > lastLevel)
					container.setParent(addedItem, lastAddedElement);
				else if(currentLevel == baseLevel)
					container.setParent(addedItem, null);
				else if(currentLevel == lastLevel && container.getParent(lastAddedElement) != null)
					container.setParent(addedItem,  container.getParent(lastAddedElement));
				else {
					Object parent = container.getParent(lastAddedElement);
					while(parent != null && currentLevel-- > baseLevel)
						parent = container.getParent(parent);
					container.setParent(addedItem, parent);
				}
				lastLevel = currentLevel;
				lastAddedElement = addedItem;
			}
		}
		
		this.tree.setContainerDataSource(container);
		
	}
	
	@Override
	public Class<? extends String> getType() {
		return String.class;
	}

}
