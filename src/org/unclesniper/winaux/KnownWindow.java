package org.unclesniper.winaux;

import org.unclesniper.winwin.HWnd;

public final class KnownWindow {

	private final HWnd hwnd;

	public KnownWindow(HWnd hwnd) {
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

}
