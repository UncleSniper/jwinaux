package org.unclesniper.winaux;

public abstract class AbstractTagUpdatingListener {

	protected final AuxEngine engine;

	protected Iterable<TagProvider> providers;

	public AbstractTagUpdatingListener(AuxEngine engine, Iterable<TagProvider> providers) {
		if(engine == null)
			throw new IllegalArgumentException("Engine cannot be null");
		if(providers == null)
			throw new IllegalArgumentException("Tag provider set cannot be null");
		this.engine = engine;
		this.providers = providers;
	}

	protected void updateTagGrants(KnownWindow window) {
		for(TagProvider provider : providers)
			provider.apply(engine, window);
	}

}
