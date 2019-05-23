package org.vaadin.miki.recipebook.services.files;

import java.io.File;

import org.vaadin.miki.recipebook.services.FileService;

public class LocalFileService implements FileService {

	private static final String BASE_PATH = "c:/recipebookfiles/";

	public LocalFileService() {
		// check if there is a directory - if not, make it
		File base = new File(BASE_PATH);
		if (!base.exists() && !base.mkdir())
			throw new IllegalArgumentException(
					"Cannot create base directory for files.");
	}

	@Override
	public File getFile(String filename) {
		return new File(BASE_PATH, filename);
	}

	@Override
	public void deleteFile(String filename) {
		if (filename != null) {
			File file = this.getFile(filename);
			if (file != null && file.exists())
				file.delete();
		}
	}

}
