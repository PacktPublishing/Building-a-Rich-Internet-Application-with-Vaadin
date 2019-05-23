package org.vaadin.miki.recipebook.services.sql;

import java.sql.SQLException;

import org.vaadin.miki.recipebook.services.ContainerService;

import com.vaadin.data.Container;
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
				"jdbc:postgresql://localhost:5432/recipebook",
				"recipebook",
				"recipeb00k");
		this.usersTable = new TableQuery("users", pool);
		this.recipesTable = new TableQuery("recipes", pool);
		this.commentsTable = new TableQuery("comments", pool);
	}
	
	/**
	 * Constructs a container with a given table query.
	 * @param query Table query to use.
	 * @return SQLContainer that will operate on given table query.
	 */
	private SQLContainer constructContainer(TableQuery query) {
		try {
			return new SQLContainer(query);
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public SQLContainer getUsersContainer() {
		return this.constructContainer(this.usersTable);
	}

	@Override
	public SQLContainer getRecipesContainer() {
		SQLContainer container = this.constructContainer(this.recipesTable);
		container.addReference(this.getUsersContainer(), "user_id", "id");
		return container;
	}

	@Override
	public SQLContainer getCommentsContainer() {
		SQLContainer container = this.constructContainer(this.commentsTable);
		container.addReference(this.getUsersContainer(), "user_id", "id");
		container.addReference(this.getRecipesContainer(), "recipe_id", "id");
		return container;

	}

}
