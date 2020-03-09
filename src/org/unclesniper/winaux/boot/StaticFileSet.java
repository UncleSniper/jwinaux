package org.unclesniper.winaux.boot;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;

public class StaticFileSet implements FileSet {

	private final List<File> files = new LinkedList<File>();

	public StaticFileSet() {}

	public void addFile(File file) {
		if(file != null)
			files.add(file);
	}

	@Override
	public void collectFiles(Consumer<File> sink) {
		if(sink == null)
			throw new IllegalArgumentException("Sink cannot be null");
		files.stream().forEach(sink);
	}

}
