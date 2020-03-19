package org.unclesniper.winaux.boot;

import java.net.URL;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Consumer;

public interface URLSet {

	void collectURLs(Consumer<URL> sink);

	public static List<URL> toList(URLSet urls) {
		if(urls == null)
			throw new IllegalArgumentException("URL set cannot be null");
		Set<URL> set = new HashSet<URL>();
		List<URL> list = new LinkedList<URL>();
		urls.collectURLs(url -> {
			if(url != null && set.add(url))
				list.add(url);
		});
		return list;
	}

}
