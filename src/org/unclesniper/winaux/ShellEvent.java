package org.unclesniper.winaux;

public class ShellEvent {

	private final AuxEngine engine;

	private final KnownWindow window;

	ShellEvent(AuxEngine engine, KnownWindow window) {
		this.engine = engine;
		this.window = window;
	}

	public AuxEngine getEngine() {
		return engine;
	}

	public KnownWindow getWindow() {
		return window;
	}

}
