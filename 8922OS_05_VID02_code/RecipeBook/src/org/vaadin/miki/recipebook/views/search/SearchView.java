package org.vaadin.miki.recipebook.views.search;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalSplitPanel;

public class SearchView extends VerticalSplitPanel implements View {

	private final Table searchResults = new Table();
	
	public SearchView() {
		super();
		this.searchResults.setSizeFull();
		
		this.setFirstComponent(new Label("this will be replaced with a configuration panel for searching"));
		this.setSecondComponent(this.searchResults);
		
		this.setSplitPosition(20, Unit.PERCENTAGE);
		this.setLocked(true);
		
		this.setSizeFull();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
