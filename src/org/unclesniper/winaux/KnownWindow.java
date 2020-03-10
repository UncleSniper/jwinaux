package org.unclesniper.winaux;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.IdentityHashMap;
import org.unclesniper.winwin.HWnd;
import java.util.function.Consumer;

public final class KnownWindow {

	private final HWnd hwnd;

	private final Map<Tag, Set<TagGrant>> tags = new IdentityHashMap<Tag, Set<TagGrant>>();

	KnownWindow(HWnd hwnd) {
		if(hwnd == null)
			throw new IllegalArgumentException("Window handle cannot be null");
		this.hwnd = hwnd;
	}

	public HWnd getHWnd() {
		return hwnd;
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof KnownWindow))
			return false;
		return hwnd.equals(((KnownWindow)other).hwnd);
	}

	@Override
	public int hashCode() {
		return hwnd.hashCode();
	}

	boolean addTagNoGlobalNotify(TagGrant grant) {
		if(grant == null)
			throw new IllegalArgumentException("Tag grant cannot be null");
		Tag tag = grant.getTag();
		boolean changed;
		synchronized(tags) {
			Set<TagGrant> grants = tags.get(tag);
			changed = grants == null;
			if(changed) {
				grants = new HashSet<TagGrant>();
				tags.put(tag, grants);
			}
			changed = grants.add(grant);
		}
		return changed;
	}

	boolean removeTagNoGlobalNotify(TagGrant grant) {
		if(grant == null)
			throw new IllegalArgumentException("Tag grant cannot be null");
		Tag tag = grant.getTag();
		boolean changed;
		synchronized(tags) {
			Set<TagGrant> grants = tags.get(tag);
			changed = grants != null;
			if(changed)
				changed = grants.remove(grant);
		}
		return changed;
	}

	void removeAllTagsNoGlobalNotify(Consumer<Tag> sink) {
		synchronized(tags) {
			try {
				for(Tag tag : tags.keySet()) {
					tag.removeCompleteWindowNoGlobalNotify(this);
					if(sink != null)
						sink.accept(tag);
				}
			}
			finally {
				tags.clear();
			}
		}
	}

	public Set<Tag> getTags() {
		Map<Tag, Void> set = new IdentityHashMap<Tag, Void>();
		synchronized(tags) {
			for(Tag tag : tags.keySet())
				set.put(tag, null);
		}
		return set.keySet();
	}

	public boolean hasTag(Tag tag) {
		if(tag == null)
			return false;
		synchronized(tags) {
			return tags.containsKey(tag);
		}
	}

}
