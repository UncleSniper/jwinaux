package org.unclesniper.winaux;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Collections;

public class Configuration {

	private final List<AuxHotkey> hotkeys = new LinkedList<AuxHotkey>();

	private final List<ShellEventListener> shellEventListeners = new LinkedList<ShellEventListener>();

	private final Map<Class<?>, TagUpdater> tagUpdaters = new HashMap<Class<?>, TagUpdater>();

	private final List<TagProvider> tagProviders = new LinkedList<TagProvider>();

	private final List<TagListener> tagListeners = new LinkedList<TagListener>();

	public Configuration() {}

	public List<AuxHotkey> getHotkeys() {
		return Collections.unmodifiableList(hotkeys);
	}

	public void addHotkey(AuxHotkey hotkey) {
		if(hotkey != null)
			hotkeys.add(hotkey);
	}

	public boolean removeHotkey(AuxHotkey hotkey) {
		return hotkey != null && hotkeys.remove(hotkey);
	}

	public List<ShellEventListener> getShellEventListeners() {
		return Collections.unmodifiableList(shellEventListeners);
	}

	public void addShellEventListener(ShellEventListener listener) {
		if(listener != null)
			shellEventListeners.add(listener);
	}

	public boolean removeShellEventListener(ShellEventListener listener) {
		return listener != null && shellEventListeners.remove(listener);
	}

	public Map<Class<?>, TagUpdater> getTagUpdaters() {
		return Collections.unmodifiableMap(tagUpdaters);
	}

	public TagUpdater setTagUpdater(Class<?> clazz, TagUpdater updater) {
		if(clazz == null || updater == null)
			return null;
		return tagUpdaters.put(clazz, updater);
	}

	public TagUpdater removeTagUpdater(Class<?> clazz) {
		return tagUpdaters.remove(clazz);
	}

	public List<TagProvider> getTagProviders() {
		return Collections.unmodifiableList(tagProviders);
	}

	public void addTagProvider(TagProvider provider) {
		if(provider != null)
			tagProviders.add(provider);
	}

	public boolean removeTagProvider(TagProvider provider) {
		return provider != null && tagProviders.remove(provider);
	}

	public List<TagListener> getTagListeners() {
		return Collections.unmodifiableList(tagListeners);
	}

	public void addTagListener(TagListener listener) {
		if(listener != null)
			tagListeners.add(listener);
	}

	public boolean removeTagListener(TagListener listener) {
		return listener != null && tagListeners.remove(listener);
	}

}
