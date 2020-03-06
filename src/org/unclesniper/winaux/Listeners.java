package org.unclesniper.winaux;

import java.util.Map;
import java.util.IdentityHashMap;
import java.util.function.Consumer;

public final class Listeners<ListenerT> {

	private final Map<ListenerT, Long> listeners = new IdentityHashMap<ListenerT, Long>();

	private volatile Map<ListenerT, Long> cache;

	public Listeners() {}

	public void add(ListenerT listener) {
		if(listener == null)
			return;
		synchronized(listeners) {
			cache = null;
			Long oldCount = listeners.get(listener);
			long newCount = (oldCount == null ? 0l : oldCount.longValue()) + 1l;
			listeners.put(listener, newCount);
		}
	}

	public boolean remove(ListenerT listener) {
		if(listener == null)
			return false;
		synchronized(listeners) {
			cache = null;
			Long oldCount = listeners.get(listener);
			if(oldCount == null)
				return false;
			long newCount = oldCount.longValue() - 1l;
			if(newCount < 0l)
				return false;
			if(newCount == 0l)
				listeners.remove(listener);
			else
				listeners.put(listener, newCount);
		}
		return true;
	}

	public void clear() {
		synchronized(listeners) {
			cache = null;
			listeners.clear();
		}
	}

	public void fire(Consumer<ListenerT> sink) {
		if(sink == null)
			return;
		Map<ListenerT, Long> c = cache;
		if(c == null) {
			c = new IdentityHashMap<ListenerT, Long>();
			synchronized(listeners) {
				for(Map.Entry<ListenerT, Long> entry : listeners.entrySet())
					c.put(entry.getKey(), entry.getValue());
				cache = c;
			}
		}
		for(ListenerT listener : c.keySet())
			sink.accept(listener);
	}

}
