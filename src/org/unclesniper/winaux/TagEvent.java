package org.unclesniper.winaux;

public final class TagEvent {

	private final AuxEngine engine;

	private final KnownWindow window;

	private final Tag tag;

	TagEvent(AuxEngine engine, KnownWindow window, Tag tag) {
		this.engine = engine;
		this.window = window;
		this.tag = tag;
	}

	public AuxEngine getEngine() {
		return engine;
	}

	public KnownWindow getWindow() {
		return window;
	}

	public Tag getTag() {
		return tag;
	}

}
