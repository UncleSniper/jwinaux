package org.unclesniper.winaux;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

public class Configuration {

	private final List<AuxHotkey> hotkeys = new LinkedList<AuxHotkey>();

	private final List<ShellEventListener> shellEventListeners = new LinkedList<ShellEventListener>();

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

}
