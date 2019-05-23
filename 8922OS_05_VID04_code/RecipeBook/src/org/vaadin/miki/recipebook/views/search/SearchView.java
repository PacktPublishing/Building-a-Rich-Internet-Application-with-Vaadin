package org.vaadin.miki.recipebook.views.search;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.miki.recipebook.services.ServiceProvider;

import com.vaadin.data.Container;
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

	/**
	 * Converts parameter string to a more usable map of parameters.
	 * @param parameters Parameter string.
	 * @return Map of parameters.
	 */
	private Map<String, String> getParameterMap(String parameters) {
		String[] attributeValuePairs = parameters == null ? new String[0] : parameters.split("\\/");
		Map<String, String> parameterMap = new HashMap<String, String>();
		for(String pair: attributeValuePairs) {
			String[] attributeValue = pair.split("=", 1);
			if(attributeValue.length == 2 )
				parameterMap.put(attributeValue[0], attributeValue[1]);
		}
		return parameterMap;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		Map<String, String> parameters = this.getParameterMap(event.getParameters());
		Container recipeContainer = ServiceProvider.getInstance().getContainerService().getRecipesContainer(parameters, true);
		this.searchResults.setContainerDataSource(recipeContainer);
	}

}
