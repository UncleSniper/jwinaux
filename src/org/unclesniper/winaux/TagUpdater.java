package org.unclesniper.winaux;

public interface TagUpdater {

	void registerListener(Iterable<TagProvider> provider, AuxEngine engine);

}
