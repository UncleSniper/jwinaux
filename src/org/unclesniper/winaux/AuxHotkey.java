package org.unclesniper.winaux;

import org.unclesniper.winwin.VirtualKey;

public class AuxHotkey {

	private int modifiers;

	private VirtualKey key;

	private HotkeyAction action;

	private boolean lowLevel;

	public AuxHotkey() {}

	public AuxHotkey(int modifiers, VirtualKey key, HotkeyAction action) {
		this(modifiers, key, action, false);
	}

	public AuxHotkey(int modifiers, VirtualKey key, HotkeyAction action, boolean lowLevel) {
		this.modifiers = modifiers;
		this.key = key;
		this.action = action;
		this.lowLevel = lowLevel;
	}

	public int getModifiers() {
		return modifiers;
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	public void addModifiers(int mask) {
		modifiers |= mask;
	}

	public void removeModifiers(int mask) {
		modifiers &= ~mask;
	}

	public void addModifier(KeyModifier modifier) {
		if(modifier != null)
			modifiers |= modifier.getBit();
	}

	public void removeModifier(KeyModifier modifier) {
		if(modifier != null)
			modifiers &= ~modifier.getBit();
	}

	public VirtualKey getKey() {
		return key;
	}

	public void setKey(VirtualKey key) {
		this.key = key;
	}

	public HotkeyAction getAction() {
		return action;
	}

	public void setAction(HotkeyAction action) {
		this.action = action;
	}

	public boolean isLowLevel() {
		return lowLevel;
	}

	public void setLowLevel(boolean lowLevel) {
		this.lowLevel = lowLevel;
	}

}
