package org.unclesniper.winaux;

public class TagUpdatingTagListener extends AbstractTagUpdatingListener implements TagListener {

	public TagUpdatingTagListener(AuxEngine engine, Iterable<TagProvider> providers) {
		super(engine, providers);
	}

	protected void updateTagGrants(TagEvent event) {
		updateTagGrants(event.getWindow());
	}

	@Override
	public void tagGained(TagEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void tagLost(TagEvent event) {
		updateTagGrants(event);
	}

}
