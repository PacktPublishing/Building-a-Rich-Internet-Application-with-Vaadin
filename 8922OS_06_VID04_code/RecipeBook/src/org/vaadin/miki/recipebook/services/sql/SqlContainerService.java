package org.vaadin.miki.recipebook.services.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.miki.recipebook.services.ContainerService;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

public class SqlContainerService implements ContainerService {

	private final TableQuery usersTable;
	private final TableQuery recipesTable;
	private final TableQuery commentsTable;

	public SqlContainerService() throws SQLException {
		JDBCConnectionPool pool = new SimpleJDBCConnectionPool(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost:5432/recipebook", "recipebook",
				"recipeb00k");
		this.usersTable = new TableQuery("users", pool);
		this.recipesTable = new TableQuery("recipes", pool);
		this.commentsTable = new TableQuery("comments", pool);
	}

	/**
	 * Constructs a container with a given table query.
	 * 
	 * @param query
	 *            Table query to use.
	 * @return SQLContainer that will operate on given table query.
	 */
	private SQLContainer constructContainer(TableQuery query) {
		try {
			SQLContainer container = new SQLContainer(query);
			container.setAutoCommit(true);
			return container;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public SQLContainer getUsersContainer() {
		return this.constructContainer(this.usersTable);
	}

	private SQLContainer applyFilters(SQLContainer container,
			Collection<Container.Filter> containerFilters, boolean all) {
		if (containerFilters.size() == 1)
			container.addContainerFilter(containerFilters.iterator().next());
		else if (containerFilters.size() > 1) {
			Container.Filter[] array = containerFilters
					.toArray(new Container.Filter[containerFilters.size()]);
			if (all)
				container.addContainerFilter(new And(array));
			else
				container.addContainerFilter(new Or(array));
		}
		return container;
	}

	@Override
	public SQLContainer getUsersContainer(Map<String, String> filters,
			boolean all) {
		SQLContainer container = this.getUsersContainer();
		ArrayList<Container.Filter> containerFilters = new ArrayList<Container.Filter>();
		if (filters.containsKey(USER_EMAIL_ATTRIBUTE))
			containerFilters.add(new Compare.Equal("email", filters
					.get(USER_EMAIL_ATTRIBUTE)));
		if (filters.containsKey(USER_NAME_ATTRIBUTE))
			containerFilters.add(new Like("name", filters
					.get(USER_EMAIL_ATTRIBUTE), false));
		return this.applyFilters(container, containerFilters, all);
	}

	@Override
	public SQLContainer getRecipesContainer() {
		SQLContainer container = this.constructContainer(this.recipesTable);
		container.addReference(this.getUsersContainer(), "user_id", "id");
		return container;
	}

	@Override
	public SQLContainer getRecipesContainer(Map<String, String> filters,
			boolean all) {
		SQLContainer container = this.getRecipesContainer();
		ArrayList<Container.Filter> containerFilters = new ArrayList<Container.Filter>();
		if (filters.containsKey(RECIPE_AUTHOR_ATTRIBUTE)) {
			Map<String, String> usersQuery = new HashMap<String, String>();
			usersQuery.put(USER_EMAIL_ATTRIBUTE,
					filters.get(RECIPE_AUTHOR_ATTRIBUTE));
			usersQuery.put(USER_NAME_ATTRIBUTE,
					filters.get(RECIPE_AUTHOR_ATTRIBUTE));
			Container usersQueried = this.getUsersContainer(usersQuery, false);
			Collection<?> userIds = usersQueried.getItemIds();
			if (!userIds.isEmpty()) {
				Container.Filter[] userIdFilters = new Container.Filter[userIds
						.size()];
				int counter = 0;
				for (Object userId : userIds)
					userIdFilters[counter++] = new Compare.Equal("user_id",
							userId);
				containerFilters.add(new Or(userIdFilters));
			}
		}
		if(filters.containsKey(RECIPE_TITLE_ATTRIBUTE))
			containerFilters.add(new Like("title", filters.get(RECIPE_TITLE_ATTRIBUTE), false));
		for(String attribute: new String[]{RECIPE_INGREDIENTS_ATTRIBUTE, RECIPE_TAG_ATTRIBUTE})
			if(filters.containsKey(attribute)) {
				String[] attributeValues = attribute.split("\\s*,\\s*");
				for(String value: attributeValues)
					containerFilters.add(new Like(attribute, value, false));
			}
		return this.applyFilters(container, containerFilters, all);
	}

	@Override
	public SQLContainer getCommentsContainer() {
		SQLContainer container = this.constructContainer(this.commentsTable);
		container.addReference(this.getUsersContainer(), "user_id", "id");
		container.addReference(this.getRecipesContainer(), "recipe_id", "id");
		return container;

	}
	
	@Override
	public Object addRecipe(String authorEmail) {
		HashMap<String, String> queryMap = new HashMap<String, String>();
		queryMap.put(USER_EMAIL_ATTRIBUTE, authorEmail);
		SQLContainer users = this.getUsersContainer(queryMap, true);
		if(users.size() == 0)
			return null;
		else {
			SQLContainer recipes = this.getRecipesContainer();
			Object recipeId = recipes.addItem();
			if(recipeId != null) {
				Item recipeItem = recipes.getItem(recipeId);
				recipeItem.getItemProperty("user_id").setValue(users.getItem(users.getIdByIndex(0)).getItemProperty("id").getValue());
				recipeItem.getItemProperty("added_on").setValue(Calendar.getInstance().getTime());
				recipeItem.getItemProperty("title").setValue("New awesome recipe");
				try {
					recipes.commit();
				}
				catch (UnsupportedOperationException e) {
					e.printStackTrace();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return recipeId;
		}
	}
	
}
