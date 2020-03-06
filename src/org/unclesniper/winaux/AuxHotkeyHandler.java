package org.unclesniper.winaux;

import org.unclesniper.winwin.VirtualKey;
import org.unclesniper.winwin.HotkeyHandler;

public class AuxHotkeyHandler implements HotkeyHandler {

	private final AuxEngine engine;

	private HotkeyAction action;

	public AuxHotkeyHandler(AuxEngine engine, HotkeyAction action) {
		if(engine == null)
			throw new IllegalStateException("Engine cannot be null");
		this.engine = engine;
		this.action = action;
	}

	public AuxEngine getEngine() {
		return engine;
	}

	public HotkeyAction getAction() {
		return action;
	}

	public void setAction(HotkeyAction action) {
		this.action = action;
	}

	@Override
	public void hotkeyPressed(int id, int modifiers, VirtualKey key) {
		if(action == null)
			return;
		if(key != null)
			engine.slate(() -> action.hotkeyDown(engine));
		else
			engine.slate(() -> action.hotkeyUp(engine));
	}

}
