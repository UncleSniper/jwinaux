package org.unclesniper.winaux.boot;

import java.io.File;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Consumer;

public interface FileSet {

	void collectFiles(Consumer<File> sink);

	public static List<File> toList(FileSet files) {
		if(files == null)
			throw new IllegalArgumentException("File set cannot be null");
		Set<File> set = new HashSet<File>();
		List<File> list = new LinkedList<File>();
		files.collectFiles(file -> {
			if(file != null && set.add(file))
				list.add(file);
		});
		return list;
	}

}
