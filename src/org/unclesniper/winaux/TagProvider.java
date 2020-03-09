package org.unclesniper.winaux;

import java.util.Map;
import java.util.WeakHashMap;

public class TagProvider {

	private Tag tag;

	private WindowPredicate predicate;

	private final Map<KnownWindow, TagGrant> grants = new WeakHashMap<KnownWindow, TagGrant>();

	public TagProvider() {}

	public TagProvider(Tag tag, WindowPredicate predicate) {
		this.tag = tag;
		this.predicate = predicate;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public WindowPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(WindowPredicate predicate) {
		this.predicate = predicate;
	}

	public void apply(AuxEngine engine, KnownWindow window) {
		if(engine == null)
			throw new IllegalArgumentException("Engine cannot be null");
		if(window == null)
			throw new IllegalArgumentException("Window cannot be null");
		if(predicate.matches(engine, window))
			grant(engine, window);
		else
			revoke(window);
	}

	private void grant(AuxEngine engine, KnownWindow window) {
		synchronized(grants) {
			if(!grants.containsKey(window))
				grants.put(window, engine.grantTag(window, tag));
		}
	}

	private void revoke(KnownWindow window) {
		synchronized(grants) {
			TagGrant grant = grants.get(window);
			if(grant != null) {
				grant.revoke();
				grants.remove(window);
			}
		}
	}

}
