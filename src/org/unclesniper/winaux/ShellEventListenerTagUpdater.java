package org.unclesniper.winaux;

public class ShellEventListenerTagUpdater implements TagUpdater {

	public ShellEventListenerTagUpdater() {}

	@Override
	public void registerListener(Iterable<TagProvider> providers, AuxEngine engine) {
		engine.addShellEventListener(new TagUpdatingShellEventListener(engine, providers));
	}

}
