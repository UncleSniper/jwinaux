package org.unclesniper.winaux;

public class AuxActionHotkeyAction implements HotkeyAction {

	private AuxAction onDown;

	private AuxAction onUp;

	public AuxActionHotkeyAction() {}

	public AuxActionHotkeyAction(AuxAction onDown, AuxAction onUp) {
		this.onDown = onDown;
		this.onUp = onUp;
	}

	public AuxAction getOnDown() {
		return onDown;
	}

	public void setOnDown(AuxAction onDown) {
		this.onDown = onDown;
	}

	public AuxAction getOnUp() {
		return onUp;
	}

	public void setOnUp(AuxAction onUp) {
		this.onUp = onUp;
	}

	@Override
	public void hotkeyDown(Engine engine) {
		if(onDown != null)
			onDown.perform(engine);
	}

	@Override
	public void hotkeyUp(Engine engine) {
		if(onUp != null)
			onUp.perform(engine);
	}

}
