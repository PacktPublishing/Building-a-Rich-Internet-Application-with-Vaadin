package org.vaadin.miki.recipebook.views.food;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.vaadin.miki.recipebook.services.LoginService.User;
import org.vaadin.miki.recipebook.services.ServiceProvider;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload;

public class FoodView extends CssLayout implements View {

	private FieldGroup buildFieldsForViewer() {
		this.removeAllComponents();

		TextField title = new TextField();
		Tree ingredients = new Tree();
		Image image = new Image();
		TextArea instructions = new TextArea();
		Table comments = new Table();
		FieldGroup fields = new FieldGroup();
		
		this.addComponent(title);
		this.addComponent(ingredients);
		this.addComponent(image);
		this.addComponent(instructions);
		this.addComponent(comments);

		title.addStyleName("recipetitle");
		ingredients.addStyleName("ingredients");
		image.addStyleName("foodphoto");
		instructions.addStyleName("instructions");
		comments.addStyleName("comments");

		fields.bind(ingredients, "ingredients");
		fields.bind(instructions, "instructions");
		fields.bind(title, "title");

		title.setReadOnly(true);
		instructions.setReadOnly(true);

		title.setWidth("100%");
		comments.setWidth("100%");

		return fields;
	}

	private FieldGroup buildFieldsForAuthor() {
		this.removeAllComponents();
		
		TextField title = new TextField("Recipe title");
		TextArea ingredients = new TextArea("Ingredients");
		Image image = new Image();
		TextArea instructions = new TextArea("Instructions");
		Table comments = new Table();
		final FieldGroup fields = new FieldGroup();

		final Button save = new Button("Update recipe", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					fields.commit();
					Notification.show("Recipe updated", Notification.Type.HUMANIZED_MESSAGE);
				} catch (CommitException e) {
					Notification.show("Could not save changes", e.getMessage(), Notification.Type.WARNING_MESSAGE);
					e.printStackTrace();
				}
			}
		});
		
		Upload upload = new Upload("Upload photo", new Upload.Receiver() {
			
			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					return new FileOutputStream(new File("c:/recipebookfiles/"+filename));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Notification.show("Starting upload failed!", e.getMessage(), Notification.Type.WARNING_MESSAGE);
					return null;
				}
			}
		});
		final TextField filename = new TextField();
		upload.addFinishedListener(new Upload.FinishedListener() {
			
			@Override
			public void uploadFinished(FinishedEvent event) {
				filename.setValue(event.getFilename());
				save.click();
			}
		});
		
		this.addComponent(save);
		this.addComponent(title);
		this.addComponent(ingredients);
		this.addComponent(image);
		this.addComponent(upload);
		this.addComponent(instructions);
		this.addComponent(comments);

		ingredients.addStyleName("ingredients");
		image.addStyleName("foodphoto");
		instructions.addStyleName("instructions");
		comments.addStyleName("comments");

		fields.bind(ingredients, "ingredients");
		fields.bind(instructions, "instructions");
		fields.bind(title, "title");
		fields.bind(filename, "photo");

		comments.setWidth("100%");
		title.setWidth("100%");
		return fields;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		String parameters = event.getParameters();
		if (parameters == null || parameters.isEmpty())
			event.getNavigator().navigateTo(""); // go to main page instead
		else {
			String[] params = parameters.split("/");
			try {
				int recipeId = Integer.valueOf(params[0]);
				Container container = ServiceProvider.getInstance()
						.getContainerService().getRecipesContainer();
				Item recipe = container.getItem(new RowId(recipeId));
				if (recipe == null)
					throw new IllegalArgumentException("no such recipe");
				// check if the current user is an author or not
				FieldGroup fields;
				User currentUser = ServiceProvider.getInstance().getLoginService().getCurrentUser();
				if (currentUser != null && currentUser
						.getUsername()
						.equals(ServiceProvider
								.getInstance()
								.getContainerService()
								.getUsersContainer()
								.getItem(new RowId(
										recipe.getItemProperty("user_id")
												.getValue()))
								.getItemProperty("email").getValue()))
					fields = this.buildFieldsForAuthor();
				else
					fields = this.buildFieldsForViewer();
				fields.setItemDataSource(recipe);
			} catch (Exception e) {
				; // no such recipe
				event.getNavigator().navigateTo("");
			}
		}
	}

}
