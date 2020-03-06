package org.unclesniper.winaux;

public class PostQuitMessageAction implements AuxAction {

	private int status;

	public PostQuitMessageAction() {}

	public PostQuitMessageAction(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public void perform(Engine engine) {
		engine.postQuitMessage();
	}

}
