package org.unclesniper.winaux;

public class TagListenerTagUpdater implements TagUpdater {

	public TagListenerTagUpdater() {}

	@Override
	public void registerListener(Iterable<TagProvider> providers, AuxEngine engine) {
		engine.addTagListener(new TagUpdatingTagListener(engine, providers));
	}

}
