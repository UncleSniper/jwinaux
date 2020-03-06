package org.unclesniper.winaux;

public final class ShellEvent {

	private final KnownWindow window;

	ShellEvent(KnownWindow window) {
		this.window = window;
	}

	public KnownWindow getWindow() {
		return window;
	}

}
