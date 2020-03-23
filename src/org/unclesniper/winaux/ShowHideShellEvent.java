package org.unclesniper.winaux;

public class ShowHideShellEvent extends ShellEvent {

	private final boolean shown;

	private final boolean expected;

	ShowHideShellEvent(AuxEngine engine, KnownWindow window, boolean shown, boolean expected) {
		super(engine, window);
		this.shown = shown;
		this.expected = expected;
	}

	public boolean isShown() {
		return shown;
	}

	public boolean isExpected() {
		return expected;
	}

}
