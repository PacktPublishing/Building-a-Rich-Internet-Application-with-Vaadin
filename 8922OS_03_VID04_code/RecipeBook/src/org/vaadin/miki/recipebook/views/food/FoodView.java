package org.vaadin.miki.recipebook.views.food;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Tree;

public class FoodView extends CssLayout implements View {

	private final Tree ingredients = new Tree();
	private final Image image = new Image();
	private final TextArea instructions = new TextArea();
	private final Table comments = new Table();
	
	
	public FoodView() {
		super();
		this.addComponent(this.ingredients);
		this.addComponent(this.image);
		this.addComponent(instructions);
		this.addComponent(this.comments);
		
		this.ingredients.addStyleName("ingredients");
		this.image.addStyleName("foodphoto");
		this.instructions.addStyleName("instructions");
		this.comments.addStyleName("comments");
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
