package org.vaadin.miki.recipebook.services;

import com.vaadin.data.Container;

/**
 * Interface for obtaining containers with data.
 * @author miki
 *
 */
public interface ContainerService {

	/**
	 * Returns users container.
	 * @return Users container.
	 */
	public Container getUsersContainer();
	
	/**
	 * Returns container with recipes.
	 * @return Recipes container.
	 */
	public Container getRecipesContainer();
	
	/**
	 * Returns comments container.
	 * @return Container with comments.
	 */
	public Container getCommentsContainer();
	
}
