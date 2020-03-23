package org.unclesniper.winaux;

import java.util.Set;
import java.util.Map;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.IdentityHashMap;
import org.unclesniper.winwin.HWnd;
import java.util.function.Consumer;
import org.unclesniper.winwin.WindowsException;

public final class KnownWindow {

	private static final long MAX_SHOW_HIDE_DELAY = 200l;

	private final HWnd hwnd;

	private final Map<Tag, Set<TagGrant>> tags = new IdentityHashMap<Tag, Set<TagGrant>>();

	private Boolean wantsShow;

	private Boolean shouldHide;

	private final Deque<Long> anticipatedShow = new LinkedList<Long>();

	private final Deque<Long> anticipatedHide = new LinkedList<Long>();

	KnownWindow(HWnd hwnd, Boolean wantsShow) {
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

	public Boolean getWantsShow() {
		return wantsShow;
	}

	public boolean getWantsShow(boolean defaultValue) {
		return wantsShow == null ? defaultValue : wantsShow.booleanValue();
	}

	public Boolean getShouldHide() {
		return shouldHide;
	}

	public boolean getShouldHide(boolean defaultValue) {
		return shouldHide == null ? defaultValue : shouldHide.booleanValue();
	}

	boolean adviseWindowShowHideMessage(boolean shown) {
		Deque<Long> anticipated = shown ? anticipatedShow : anticipatedHide;
		synchronized(anticipated) {
			long now = System.currentTimeMillis();
			while(!anticipated.isEmpty()) {
				long ts = anticipated.getFirst();
				if(ts >= now)
					break;
				anticipated.removeFirst();
			}
			if(anticipated.isEmpty()) {
				wantsShow = shown;
				return false;
			}
			anticipated.removeFirst();
			return true;
		}
	}

	public void definitiveShowWindow(HWnd.ShowWindow nCmdShow) {
		boolean shown = nCmdShow != null && nCmdShow != HWnd.ShowWindow.SW_HIDE;
		Deque<Long> anticipated = shown ? anticipatedShow : anticipatedHide;
		synchronized(anticipated) {
			long max = System.currentTimeMillis() + KnownWindow.MAX_SHOW_HIDE_DELAY;
			anticipated.addLast(max);
			try {
				hwnd.showWindow(nCmdShow);
			}
			catch(WindowsException we) {
				anticipated.removeLast();
				throw we;
			}
		}
	}

}
