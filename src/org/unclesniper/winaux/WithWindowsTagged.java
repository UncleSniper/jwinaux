package org.unclesniper.winaux;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.Collections;
import java.util.IdentityHashMap;

public class WithWindowsTagged implements AuxAction {

	private WindowAction action;

	private final Map<Tag, Void> tags = new IdentityHashMap<Tag, Void>();

	private WindowPredicate filter;

	public WithWindowsTagged() {}

	public WindowAction getAction() {
		return action;
	}

	public void setAction(WindowAction action) {
		this.action = action;
	}

	public Set<Tag> getTags() {
		return Collections.unmodifiableSet(tags.keySet());
	}

	public void addTag(Tag tag) {
		if(tag != null)
			tags.put(tag, null);
	}

	public void removeTag(Tag tag) {
		if(tag != null)
			tags.remove(tag);
	}

	public WindowPredicate getFilter() {
		return filter;
	}

	public void setFilter(WindowPredicate filter) {
		this.filter = filter;
	}

	@Override
	public void perform(AuxEngine engine) {
		if(action == null)
			throw new IllegalStateException("Action not set");
		Set<KnownWindow> windows = new HashSet<KnownWindow>();
		for(Tag tag : tags.keySet())
			windows.addAll(tag.getTaggedWindows());
		for(KnownWindow window : windows) {
			if(filter != null &&!filter.matches(engine, window))
				continue;
			action.perform(engine, window);
		}
	}

}
