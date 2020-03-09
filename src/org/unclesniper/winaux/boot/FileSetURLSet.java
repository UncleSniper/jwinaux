package org.unclesniper.winaux.boot;

import java.net.URL;
import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.net.MalformedURLException;

public class FileSetURLSet implements URLSet {

	private final List<FileSet> files = new LinkedList<FileSet>();

	public FileSetURLSet() {}

	public void addFile(FileSet file) {
		if(file != null)
			files.add(file);
	}

	@Override
	public void collectURLs(Consumer<URL> sink) {
		if(sink == null)
			throw new IllegalArgumentException("Sink cannot be null");
		files.stream().flatMap(file -> FileSet.toList(file).stream()).map(FileSetURLSet::fileToURL).forEach(sink);
	}

	private static URL fileToURL(File f) {
		try {
			return f.toURI().toURL();
		}
		catch(MalformedURLException mue) {
			throw new IllegalStateException("Cannot convert file '" + f.getAbsolutePath()
					+ "' to URL: " + mue.getMessage(), mue);
		}
	}

}
