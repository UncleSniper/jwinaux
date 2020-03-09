package org.unclesniper.winaux;

public final class TagGrant {

	private final AuxEngine engine;

	private final Tag tag;

	private final KnownWindow window;

	private volatile boolean revoked;

	TagGrant(AuxEngine engine, Tag tag, KnownWindow window) {
		this.engine = engine;
		if(tag == null)
			throw new IllegalArgumentException("Tag cannot be null");
		this.tag = tag;
		if(window == null)
			throw new IllegalArgumentException("Window cannot be nul");
		this.window = window;
	}

	public AuxEngine getEngine() {
		return engine;
	}

	public Tag getTag() {
		return tag;
	}

	public KnownWindow getWindow() {
		return window;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public boolean revoke() {
		synchronized(this) {
			if(revoked)
				return false;
			engine.revokeTagGrant(this);
			revoked = true;
		}
		return true;
	}

}
