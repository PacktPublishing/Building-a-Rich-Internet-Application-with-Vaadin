package org.vaadin.miki.recipebook.services;

import java.util.Map;

import com.vaadin.data.Container;

/**
 * Interface for obtaining containers with data.
 * 
 * @author miki
 * 
 */
public interface ContainerService {

	public static final String RECIPE_AUTHOR_ATTRIBUTE = "author";
	public static final String RECIPE_TITLE_ATTRIBUTE = "title";
	public static final String RECIPE_INGREDIENTS_ATTRIBUTE = "ingredients";
	public static final String RECIPE_TAG_ATTRIBUTE = "tags";
	public static final String USER_EMAIL_ATTRIBUTE = "email";
	public static final String USER_NAME_ATTRIBUTE = "name";
	public static final String USER_PASSWORD_ATTRIBUTE = "passwd";
	
	/**
	 * Returns users container.
	 * 
	 * @return Users container.
	 */
	public Container getUsersContainer();

	/**
	 * Returns filtered users container.
	 * 
	 * @param filters
	 *            Map of filters.
	 * @param all
	 *            When <b>true</b>, all filters have to match (AND query).
	 *            Otherwise, any (OR query).
	 * @return Container with filters applied. Unknown attributes are ignored.
	 */
	public Container getUsersContainer(Map<String, String> filters, boolean all);

	/**
	 * Returns container with recipes.
	 * 
	 * @return Recipes container.
	 */
	public Container getRecipesContainer();

	/**
	 * Returns filtered container with recipes.
	 * 
	 * @param filters
	 *            Filter map.
	 * @param all
	 *            When <b>true</b>, all filters have to match (AND query).
	 *            Otherwise, any (OR query).
	 * @return Recipes container, with filters applied. Unknown arguments are
	 *         ignored.
	 */
	public Container getRecipesContainer(Map<String, String> filters,
			boolean all);

	/**
	 * Returns comments container.
	 * 
	 * @return Container with comments.
	 */
	public Container getCommentsContainer();

	/**
	 * Adds a recipe for a given author's email.
	 * @param authorEmail Email address of the author.
	 * @return Id of the added item, or <b>null</b> in case of any errors.
	 */
	public Object addRecipe(String authorEmail);

}
