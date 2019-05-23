package org.vaadin.miki.recipebook.views.food;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.miki.recipebook.services.ContainerService;
import org.vaadin.miki.recipebook.services.FileService;
import org.vaadin.miki.recipebook.services.ImageService;
import org.vaadin.miki.recipebook.services.ImageService.ProcessingEvent;
import org.vaadin.miki.recipebook.services.LoginService.LoginEvent;
import org.vaadin.miki.recipebook.services.LoginService.LoginListener;
import org.vaadin.miki.recipebook.services.LoginService.User;
import org.vaadin.miki.recipebook.services.ServiceProvider;
import org.vaadin.miki.tagfield.TagField;
import org.vaadin.miki.tagfield.TagField.TagClickEvent;
import org.vaadin.miki.texttree.TextTree;
import org.vaadin.teemu.ratingstars.RatingStars;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;

public class FoodView extends CssLayout implements View {

	private final FileService fileService = ServiceProvider.getInstance()
			.getFileService();
	private final ImageService imageService = ServiceProvider.getInstance()
			.getImageService();

	private boolean author = false;
	private Item recipe = null;
	private Navigator navigator = null;
	
	private final Image image = new Image();
	private final TextField title = new TextField();
	private final TagField tagField = new TagField();
	private final TextField tagEdit = new TextField();
	private final TextArea instructions = new TextArea();
	private final Table commentsTable = new Table();
	private final TextTree ingredientsTree = new TextTree();
	private final TextArea ingredientsText = new TextArea();
	private final Upload upload = new Upload();
	private final ProgressBar uploadBar = new ProgressBar();
	private final TextField filename = new TextField();
	private final FieldGroup fields = new FieldGroup();
	
	private final Button addComment = new Button("Add comment", new Button.ClickListener() {

		@Override
		public void buttonClick(ClickEvent event) {
			addComment();
		}
		
	});
	
	private final Button switchToEdit = new Button("Edit",
			new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					updateView(true);
				}
			});
	private final Button deleteImage = new Button("Delete photo",
			new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					deletePhoto();
				}
			});
	private final Button closeEdit = new Button("Exit edit mode",
			new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					updateView(false);
				}
			});
	private final Button saveChanges = new Button("Save changes",
			new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						fields.commit();
						Notification.show("Recipe updated",
								Notification.Type.HUMANIZED_MESSAGE);
					} catch (CommitException e) {
						Notification.show("Could not save changes",
								e.getMessage(),
								Notification.Type.WARNING_MESSAGE);
						e.printStackTrace();
					}
				}
			});
	private int recipeId;

	public FoodView() {

		this.addComponent(this.switchToEdit);
		this.addComponent(this.closeEdit);
		this.addComponent(this.saveChanges);
		this.addComponent(this.title);
		this.addComponent(this.tagField);
		this.addComponent(this.tagEdit);
		this.addComponent(this.ingredientsText);
		this.addComponent(this.ingredientsTree);
		this.addComponent(this.instructions);
		this.addComponent(this.image);
		this.addComponent(new HorizontalLayout(this.upload, this.uploadBar,
				this.deleteImage));
		this.addComponent(this.addComment);
		this.addComponent(this.commentsTable);

		this.title.addStyleName("recipetitle");
		this.ingredientsText.addStyleName("ingredients");
		this.ingredientsTree.addStyleName("ingredients");
		this.image.addStyleName("foodphoto");
		this.instructions.addStyleName("instructions");
		this.commentsTable.addStyleName("comments");

		this.uploadBar.setVisible(false);
		this.uploadBar.setImmediate(true);
		this.tagEdit.setVisible(false);
		this.addComment.setVisible(false);

		this.title.setReadOnly(true);
		this.ingredientsText.setCaption("Ingredients");

		this.tagField.addTagClickListener(new TagField.TagClickListener() {
			
			@Override
			public void tagClicked(TagClickEvent event) {
				if(navigator != null)
					navigator.navigateTo("search/tags="+event.getTag());
			}
		});
		
		this.filename
				.addValueChangeListener(new Property.ValueChangeListener() {
					@Override
					public void valueChange(ValueChangeEvent event) {
						updateImage(event.getProperty().getValue());
					}
				});

		this.upload.setCaption("Upload photo");
		this.upload.setReceiver(new Upload.Receiver() {

			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					return new FileOutputStream(fileService.getFile(filename));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Notification.show("Starting upload failed!",
							e.getMessage(), Notification.Type.WARNING_MESSAGE);
					return null;
				}
			}
		});
		this.upload.addFinishedListener(new Upload.FinishedListener() {

			@Override
			public void uploadFinished(FinishedEvent event) {
				uploadBar.setIndeterminate(true);
				imageService.processFile(fileService.getFile(event
						.getFilename()));
			}
		});
		this.upload.addFailedListener(new Upload.FailedListener() {

			@Override
			public void uploadFailed(FailedEvent event) {
				uploadBar.setVisible(false);
				Notification.show("Uploading file failed.");
			}
		});
		this.upload.addStartedListener(new Upload.StartedListener() {

			@Override
			public void uploadStarted(StartedEvent event) {
				uploadBar.setIndeterminate(false);
				uploadBar.setVisible(true);
			}
		});
		this.upload.addProgressListener(new Upload.ProgressListener() {

			@Override
			public void updateProgress(final long readBytes,
					final long contentLength) {
				uploadBar.setValue((readBytes * 1.0f) / (contentLength * 1.0f));
			}
		});

		this.fields.bind(instructions, "instructions");
		this.fields.bind(title, "title");
		this.fields.bind(filename, "photo");

		this.title.setWidth("100%");
		this.commentsTable.setWidth("100%");
		this.tagEdit.setWidth("100%");
		this.tagField.setWidth("100%");

		this.imageService
				.addProcessingListener(new ImageService.ProcessingListener() {

					@Override
					public void processingSuccess(final ProcessingEvent event) {
						UI.getCurrent().access(new Runnable() {
							public void run() {
								uploadBar.setVisible(false);
								filename.setValue(event.getFile().getName());
								saveChanges.click();
								image.setSource(new FileResource(event
										.getFile()));
								UI.getCurrent().push();
							}
						});
					}

					@Override
					public void processingFailure(final ProcessingEvent event,
							final Exception cause) {
						UI.getCurrent().access(new Runnable() {
							public void run() {
								uploadBar.setVisible(false);
								cause.printStackTrace();
								fileService.deleteFile(event.getFile()
										.getName());
								Notification
										.show("The uploaded file is not a valid image file, or it cannot be resized.\n",
												cause.getMessage(),
												Notification.Type.ERROR_MESSAGE);
								UI.getCurrent().push();
							}
						});
					}

				});

		ServiceProvider.getInstance().getLoginService()
				.addLoginListener(new LoginListener() {

					@Override
					public void userLoginFailed(LoginEvent e) {
						;
					}

					@Override
					public void userLoggedOut(LoginEvent e) {
						updateAuthorStatus();
						updateView(false);
					}

					@Override
					public void userLoggedIn(LoginEvent e) {
						updateAuthorStatus();
						updateView(false);
					}
				});

	}

	private void updateAuthorStatus() {
		User user = ServiceProvider.getInstance().getLoginService()
				.getCurrentUser();
		author = (user != null && recipe != null && user.getUsername().equals(
				ServiceProvider
						.getInstance()
						.getContainerService()
						.getUsersContainer()
						.getItem(
								new RowId(recipe.getItemProperty("user_id")
										.getValue())).getItemProperty("email")
						.getValue()));

	}

	private void updateImage(Object filename) {
		if (filename == null)
			this.image.setSource(null);
		else
			this.image.setSource(new FileResource(this.fileService
					.getFile(filename.toString())));
	}

	private void deletePhoto() {
		String filename = this.filename.getValue();
		this.filename.setValue(null);
		this.fileService.deleteFile(filename);
		this.saveChanges.click();

	}

	private void updateView(boolean editMode) {
		this.switchToEdit.setVisible(this.author && !editMode);
		this.closeEdit.setVisible(this.author && editMode);
		this.saveChanges.setVisible(this.author && editMode);
		this.deleteImage.setVisible(this.author && editMode);
		
		this.addComment.setVisible(ServiceProvider.getInstance().getLoginService().getCurrentUser() != null);

		this.tagEdit.setCaption("Tags (comma-separated)");
		this.tagEdit.setVisible(editMode);
		this.tagField.setVisible(!editMode);
		
		this.ingredientsText.setVisible(editMode);
		this.ingredientsTree.setVisible(!editMode);
		this.title.setReadOnly(!editMode);
		this.instructions.setReadOnly(!editMode);
		this.upload.setVisible(editMode);
		if (editMode)
			this.title.setCaption("Recipe title");
		else
			this.title.setCaption(null);
		this.unbind(ingredientsText);
		this.unbind(ingredientsTree);
		this.unbind(tagField);
		this.unbind(tagEdit);
		if (!editMode) {
			this.fields.bind(ingredientsTree, "ingredients");
			this.fields.bind(tagField, "tags");
		}
		else {
			this.fields.bind(ingredientsText, "ingredients");
			this.fields.bind(tagEdit, "tags");
		}
	}

	protected void addComment() {
		final Container comments = ServiceProvider.getInstance().getContainerService().getCommentsContainer();
		final Window window = new Window("Add a comment");
		FormLayout layout = new FormLayout();
		final TextArea commentBody = new TextArea("Your comment");
		final RatingStars rating = new RatingStars();
		rating.setCaption("Your rating");
		window.setContent(layout);
		rating.setMaxValue(10);
		rating.setImmediate(true);
		Button submit = new Button("Submit", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(commentBody.getValue() == null || commentBody.getValue().isEmpty())
					Notification.show("Please type a comment.");
				else {
				Item comment = comments.getItem(comments.addItem());
				comment.getItemProperty("body").setValue(commentBody.getValue());
				comment.getItemProperty("rating").setValue((int)Math.round(rating.getValue()));
				comment.getItemProperty("recipe_id").setValue(recipeId);
				Map<String, String> query = new HashMap<String, String>();
				query.put(ContainerService.USER_EMAIL_ATTRIBUTE, ServiceProvider.getInstance().getLoginService().getCurrentUser().getUsername());
				Container users = ServiceProvider.getInstance().getContainerService().getUsersContainer(query, true);
				comment.getItemProperty("user_id").setValue(
						((RowId)users.getItemIds().iterator().next()).getId()[0]
						);
				window.close();
				updateCommentsTable();
				Notification.show("Comment has been added.");
				}
			}
		});
		layout.addComponents(commentBody, rating, submit);
		window.center();
		window.setModal(true);
		window.setResizable(false);
		UI.getCurrent().addWindow(window);
	}
	
	private void updateCommentsTable() {
		this.commentsTable.setContainerDataSource(ServiceProvider.getInstance().getContainerService().getCommentsContainer(this.recipeId));
	}
	
	private void unbind(Field<?> field) {
		try {
			this.fields.unbind(field);
		}
		catch(FieldGroup.BindException fgbex) {
			;
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		String parameters = event.getParameters();
		if (parameters == null || parameters.isEmpty())
			event.getNavigator().navigateTo(""); // go to main page instead
		else {
			this.navigator = event.getNavigator();
			String[] params = parameters.split("/");
			try {
				this.recipeId = Integer.valueOf(params[0]);
				Container container = ServiceProvider.getInstance()
						.getContainerService().getRecipesContainer();
				this.recipe = container.getItem(new RowId(this.recipeId));
				if (this.recipe == null)
					throw new IllegalArgumentException("no such recipe");
				this.fields.setItemDataSource(recipe);
				this.updateAuthorStatus();
				this.updateView(false);
				this.updateCommentsTable();
			} catch (Exception e) {
				e.printStackTrace(); // no such recipe
				this.recipeId = -1;
				Notification.show("Could not display recipe", e.getMessage(),
						Notification.Type.WARNING_MESSAGE);
				event.getNavigator().navigateTo("");
			}
		}
	}

}
