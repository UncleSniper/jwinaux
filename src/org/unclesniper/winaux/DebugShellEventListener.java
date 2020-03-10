package org.unclesniper.winaux;

import org.unclesniper.winwin.HWnd;
import org.unclesniper.winwin.WindowsException;

public class DebugShellEventListener implements ShellEventListener {

	public DebugShellEventListener() {}

	@Override
	public void windowCloaked(ShellEvent event) {
		DebugShellEventListener.doEvent("windowCloaked", event, false);
	}

	@Override
	public void windowCreate(ShellEvent event) {
		DebugShellEventListener.doEvent("windowCreate", event, false);
	}

	@Override
	public void windowDestroy(ShellEvent event, HWnd hwnd) {
		DebugShellEventListener.doEvent("windowDestroy", hwnd, true);
	}

	@Override
	public void windowFocus(ShellEvent event) {
		DebugShellEventListener.doEvent("windowFocus", event, false);
	}

	@Override
	public void windowNameChange(ShellEvent event) {
		DebugShellEventListener.doEvent("windowNameChange", event, false);
	}

	@Override
	public void windowReorder(ShellEvent event) {
		DebugShellEventListener.doEvent("windowReorder", event, false);
	}

	@Override
	public void windowShow(ShellEvent event) {
		DebugShellEventListener.doEvent("windowShow", event, false);
	}

	@Override
	public void windowUncloaked(ShellEvent event) {
		DebugShellEventListener.doEvent("windowUncloaked", event, false);
	}

	@Override
	public void desktopSwitch(ShellEvent event) {
		DebugShellEventListener.doEvent("desktopSwitch", event, false);
	}

	@Override
	public void foreground(ShellEvent event) {
		DebugShellEventListener.doEvent("foreground", event, false);
	}

	@Override
	public void windowMinimizeEnd(ShellEvent event) {
		DebugShellEventListener.doEvent("windowMinimizeEnd", event, false);
	}

	@Override
	public void windowMinimizeStart(ShellEvent event) {
		DebugShellEventListener.doEvent("windowMinimizeStart", event, false);
	}

	@Override
	public void windowMoveSizeEnd(ShellEvent event) {
		DebugShellEventListener.doEvent("windowMoveSizeEnd", event, false);
	}

	@Override
	public void windowMoveSizeStart(ShellEvent event) {
		DebugShellEventListener.doEvent("windowMoveSizeStart", event, false);
	}

	private static void doEvent(String what, ShellEvent event, boolean windowDestroyed) {
		KnownWindow window = event.getWindow();
		DebugShellEventListener.doEvent(what, window == null ? null : window.getHWnd(), windowDestroyed);
	}

	private static void doEvent(String what, HWnd hwnd, boolean windowDestroyed) {
		StringBuilder builder = new StringBuilder(what);
		builder.append(": hwnd = ");
		builder.append(hwnd == null ? "<null>" : hwnd.toString());
		if(!windowDestroyed) {
			try {
				String clazz = hwnd.getClassName(), title = hwnd.getWindowText();
				builder.append(" (class = '");
				builder.append(clazz);
				builder.append("', title = '");
				builder.append(title);
				builder.append("')");
			}
			catch(WindowsException we) {}
		}
		System.err.println(builder.toString());
	}

}
