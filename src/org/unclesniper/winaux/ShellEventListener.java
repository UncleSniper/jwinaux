package org.unclesniper.winaux;

import org.unclesniper.winwin.HWnd;

public interface ShellEventListener {

	void windowCloaked(ShellEvent event);

	void windowCreate(ShellEvent event);

	void windowDestroy(ShellEvent event, HWnd hwnd);

	void windowFocus(ShellEvent event);

	void windowNameChange(ShellEvent event);

	void windowReorder(ShellEvent event);

	void windowShow(ShellEvent event);

	void windowUncloaked(ShellEvent event);

	void desktopSwitch(ShellEvent event);

	void foreground(ShellEvent event);

	void windowMinimizeEnd(ShellEvent event);

	void windowMinimizeStart(ShellEvent event);

	void windowMoveSizeEnd(ShellEvent event);

	void windowMoveSizeStart(ShellEvent event);

	void windowShowHideMessage(ShowHideShellEvent event);

}
