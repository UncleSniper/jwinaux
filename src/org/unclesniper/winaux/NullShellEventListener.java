package org.unclesniper.winaux;

import org.unclesniper.winwin.HWnd;

public class NullShellEventListener implements ShellEventListener {

	public NullShellEventListener() {}

	@Override
	public void windowCloaked(ShellEvent event) {}

	@Override
	public void windowCreate(ShellEvent event) {}

	@Override
	public void windowDestroy(ShellEvent event, HWnd hwnd) {}

	@Override
	public void windowFocus(ShellEvent event) {}

	@Override
	public void windowNameChange(ShellEvent event) {}

	@Override
	public void windowReorder(ShellEvent event) {}

	@Override
	public void windowShow(ShellEvent event) {}

	@Override
	public void windowUncloaked(ShellEvent event) {}

	@Override
	public void desktopSwitch(ShellEvent event) {}

	@Override
	public void foreground(ShellEvent event) {}

	@Override
	public void windowMinimizeEnd(ShellEvent event) {}

	@Override
	public void windowMinimizeStart(ShellEvent event) {}

	@Override
	public void windowMoveSizeEnd(ShellEvent event) {}

	@Override
	public void windowMoveSizeStart(ShellEvent event) {}

	@Override
	public void windowShowHideMessage(ShowHideShellEvent event) {}

}
