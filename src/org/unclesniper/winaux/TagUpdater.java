package org.unclesniper.winaux;

public interface TagUpdater {

	void registerListener(Iterable<TagProvider> providers, AuxEngine engine);

}
