package org.unclesniper.winaux;

import org.unclesniper.winwin.HWnd;

public class TagUpdatingShellEventListener extends AbstractTagUpdatingListener implements ShellEventListener {

	public TagUpdatingShellEventListener(AuxEngine engine, Iterable<TagProvider> providers) {
		super(engine, providers);
	}

	protected void updateTagGrants(ShellEvent event) {
		updateTagGrants(event.getWindow());
	}

	@Override
	public void windowCloaked(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void windowCreate(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void windowDestroy(ShellEvent event, HWnd hwnd) {}

	@Override
	public void windowFocus(ShellEvent event) {}

	@Override
	public void windowNameChange(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void windowReorder(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void windowShow(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void windowUncloaked(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void desktopSwitch(ShellEvent event) {}

	@Override
	public void foreground(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void windowMinimizeEnd(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void windowMinimizeStart(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void windowMoveSizeEnd(ShellEvent event) {
		updateTagGrants(event);
	}

	@Override
	public void windowMoveSizeStart(ShellEvent event) {}

	@Override
	public void windowShowHideMessage(ShowHideShellEvent event) {
		updateTagGrants(event);
	}

}
