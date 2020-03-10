package org.unclesniper.winaux;

public final class TagEvent {

	private final AuxEngine engine;

	private final KnownWindow window;

	private final Tag tag;

	private final boolean windowDestroyed;

	TagEvent(AuxEngine engine, KnownWindow window, Tag tag, boolean windowDestroyed) {
		this.engine = engine;
		this.window = window;
		this.tag = tag;
		this.windowDestroyed = windowDestroyed;
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

	public boolean isWindowDestroyed() {
		return windowDestroyed;
	}

}
