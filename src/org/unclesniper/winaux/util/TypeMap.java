package org.unclesniper.winaux.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class TypeMap {

	private final Map<Class<?>, Object> bindings;

	public TypeMap() {
		bindings = new HashMap<Class<?>, Object>();
	}

	private TypeMap(Map<Class<?>, Object> bindings) {
		this.bindings = bindings;
	}

	public <T> T put(Class<T> key, T value) {
		if(key == null)
			throw new IllegalArgumentException("Key class cannot be null");
		Object was;
		if(value == null)
			was = bindings.remove(key);
		else if(!key.isInstance(value))
			throw new IllegalArgumentException("Cannot bind a " + value.getClass().getName()
					+ " as a " + key.getName());
		else
			was = bindings.put(key, value);
		return was == null ? null : key.cast(was);
	}

	public <T> T remove(Class<T> key) {
		return key == null ? null : put(key, null);
	}

	public <T> T get(Class<T> key) {
		if(key == null)
			throw new IllegalArgumentException("Key class cannot be null");
		Object is = bindings.get(key);
		return is == null ? null : key.cast(is);
	}

	public void clear() {
		bindings.clear();
	}

	public TypeMap unmodifiable() {
		return new TypeMap(Collections.unmodifiableMap(bindings));
	}

}
