package org.unclesniper.winaux;

import org.unclesniper.winwin.VirtualKey;

public enum KeyModifier {

	ALT(VirtualKey.MOD_ALT),
	CTRL(VirtualKey.MOD_CONTROL),
	SHIFT(VirtualKey.MOD_SHIFT),
	WIN(VirtualKey.MOD_WIN);

	private final int bit;

	private KeyModifier(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}

}
