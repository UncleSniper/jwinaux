package org.unclesniper.winaux;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

public class Configuration {

	private final List<AuxHotkey> hotkeys = new LinkedList<AuxHotkey>();

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

}
