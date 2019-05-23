package org.vaadin.miki.recipebook.views.search;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.miki.recipebook.services.ContainerService;
import org.vaadin.miki.recipebook.services.ServiceProvider;

import com.vaadin.data.Container;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Button.ClickEvent;

public class SearchView extends VerticalSplitPanel implements View {

	private final Table searchResults = new Table();
	private final TextField userField = new TextField("Recipe written by");
	private final TextField titleField = new TextField("Recipe's title");
	private final TextField ingredientField = new TextField("Ingredients (comma-separated)");
	private final TextField tagField = new TextField("Tags (comma-separated)");

	private Navigator navigator;
	private String viewName;
	
	public SearchView() {
		super();
		this.searchResults.setSizeFull();
		
		this.userField.setImmediate(true);
		this.titleField.setImmediate(true);
		
		GridLayout form = new GridLayout(2, 3, this.userField, this.titleField, this.ingredientField, this.tagField);
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponent(new Button("Clear query", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				clearSearchForm();
			}
		}));
		
		buttons.addComponent(new Button("Search", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				navigateToParameters();
			}
		}));
		
		buttons.setSpacing(true);
		form.addComponent(buttons);
		
		this.setSecondComponent(this.searchResults);
		this.setFirstComponent(form);
		
		this.setSplitPosition(20, Unit.PERCENTAGE);
		this.setLocked(true);
		
		this.setSizeFull();
	}

	private void navigateToParameters() {
		if(this.navigator != null) {
			String navigationState = this.viewName+"/";
			navigationState += ContainerService.RECIPE_AUTHOR_ATTRIBUTE + "=" +this.userField.getValue()+"/";
			navigationState += ContainerService.RECIPE_TITLE_ATTRIBUTE + "=" + this.titleField.getValue()+"/";
			String helper = this.ingredientField.getValue().replaceAll("\\s*,\\s*", ",");
			navigationState += ContainerService.RECIPE_INGREDIENTS_ATTRIBUTE+"="+helper+"/";
			helper = this.tagField.getValue().replaceAll("\\s*,\\s*", ",");
			navigationState += ContainerService.RECIPE_TAG_ATTRIBUTE+"="+helper+"/";
			this.navigator.navigateTo(navigationState);
		}
	}
	
	private void clearSearchForm() {
		this.userField.setValue("");
		this.titleField.setValue("");
		this.ingredientField.setValue("");
		this.tagField.setValue("");
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
			String[] attributeValue = pair.split("=", 2);
			if(attributeValue.length == 2 )
				parameterMap.put(attributeValue[0], attributeValue[1]);
		}
		return parameterMap;
	}
	
	/**
	 * Updates the ui components based on search parameters.
	 * @param parameterMap
	 */
	private void updateComponents(Map<String, String> parameterMap) {
		if(parameterMap.containsKey(ContainerService.RECIPE_AUTHOR_ATTRIBUTE))
			this.userField.setValue(parameterMap.get(ContainerService.RECIPE_AUTHOR_ATTRIBUTE));
		if(parameterMap.containsKey(ContainerService.RECIPE_TITLE_ATTRIBUTE))
			this.titleField.setValue(parameterMap.get(ContainerService.RECIPE_TITLE_ATTRIBUTE));
		if(parameterMap.containsKey(ContainerService.RECIPE_INGREDIENTS_ATTRIBUTE))
			this.ingredientField.setValue(parameterMap.get(ContainerService.RECIPE_INGREDIENTS_ATTRIBUTE));
		if(parameterMap.containsKey(ContainerService.RECIPE_TAG_ATTRIBUTE))
			this.tagField.setValue(parameterMap.get(ContainerService.RECIPE_TAG_ATTRIBUTE));
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		this.navigator = event.getNavigator();
		this.viewName = event.getViewName();
		Map<String, String> parameters = this.getParameterMap(event.getParameters());
		Container recipeContainer = ServiceProvider.getInstance().getContainerService().getRecipesContainer(parameters, true);
		this.updateComponents(parameters);
		this.searchResults.setContainerDataSource(recipeContainer);
	}

}
