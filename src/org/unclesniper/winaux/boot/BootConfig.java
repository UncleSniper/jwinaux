package org.unclesniper.winaux.boot;

import java.net.URL;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class BootConfig {

	private static final URL[] URL_ARRAY_TEMPLATE = new URL[0];

	private final List<URLSet> classpath = new LinkedList<URLSet>();

	public BootConfig() {}

	public void addClasspathURL(URLSet urls) {
		if(urls != null)
			classpath.add(urls);
	}

	public URL[] getClasspath() {
		return classpath
			.stream()
			.flatMap(url -> URLSet.toList(url).stream())
			.collect(Collectors.toList())
			.toArray(BootConfig.URL_ARRAY_TEMPLATE);
	}

}
