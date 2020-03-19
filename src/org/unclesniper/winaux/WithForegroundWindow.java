package org.unclesniper.winaux;

import org.unclesniper.winwin.HWnd;

public class WithForegroundWindow implements AuxAction {

	private WindowAction action;

	public WithForegroundWindow() {}

	public WindowAction getAction() {
		return action;
	}

	public void setAction(WindowAction action) {
		this.action = action;
	}

	@Override
	public void perform(AuxEngine engine) {
		HWnd hwnd = HWnd.getForegroundWindow();
		if(hwnd == null)
			return;
		if(action == null)
			throw new IllegalStateException("Action not set");
		action.perform(engine, engine.internWindow(hwnd));
	}

}
