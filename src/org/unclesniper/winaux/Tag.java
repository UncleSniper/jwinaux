package org.unclesniper.winaux;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

public final class Tag {

	private static final AtomicLong NEXT_ID = new AtomicLong();

	private String name;

	private final Map<KnownWindow, Set<TagGrant>> windows = new HashMap<KnownWindow, Set<TagGrant>>();

	private final long id = Tag.NEXT_ID.getAndIncrement();

	public Tag() {}

	public Tag(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getID() {
		return id;
	}

	boolean addWindowNoGlobalNotify(TagGrant grant) {
		if(grant == null)
			throw new IllegalArgumentException("Tag grant cannot be null");
		KnownWindow window = grant.getWindow();
		boolean changed;
		synchronized(windows) {
			Set<TagGrant> grants = windows.get(window);
			changed = grants == null;
			if(changed) {
				grants = new HashSet<TagGrant>();
				windows.put(window, grants);
			}
			changed = grants.add(grant);
		}
		return changed;
	}

	boolean removeWindowNoGlobalNotify(TagGrant grant) {
		if(grant == null)
			throw new IllegalArgumentException("Tag grant cannot be null");
		KnownWindow window = grant.getWindow();
		boolean changed;
		synchronized(windows) {
			Set<TagGrant> grants = windows.get(window);
			changed = grants != null;
			if(changed)
				changed = grants.remove(grant);
		}
		return changed;
	}

	boolean removeCompleteWindowNoGlobalNotify(KnownWindow window) {
		if(window == null)
			throw new IllegalArgumentException("Window cannot be null");
		return windows.remove(window) != null;
	}

	public Set<KnownWindow> getTaggedWindows() {
		Set<KnownWindow> set = new HashSet<KnownWindow>();
		synchronized(windows) {
			set.addAll(windows.keySet());
		}
		return set;
	}

	public boolean isTaggedWindow(KnownWindow window) {
		if(window == null)
			return false;
		synchronized(windows) {
			return windows.containsKey(window);
		}
	}

	public String getNameOrID() {
		return name == null ? "#" + id : name;
	}

	public String getNameAndID() {
		String ids = "(" + id + ')';
		return name == null ? ids : name + ' ' + ids;
	}

}
