package org.vaadin.miki.recipebook.services;

import java.io.File;

/**
 * Interface for storing and retrieving files.
 * @author miki
 *
 */
public interface FileService {

	public File getFile(String filename);
	
	public void deleteFile(String filename);
	
}
