package org.unclesniper.winaux;

import org.unclesniper.winwin.HWnd;
import org.unclesniper.winwin.WindowsException;

public class DebugTagListener implements TagListener {

	public DebugTagListener() {}

	@Override
	public void tagGained(TagEvent event) {
		System.err.println(DebugTagListener.format(event, "gained"));
	}

	@Override
	public void tagLost(TagEvent event) {
		System.err.println(DebugTagListener.format(event, "lost"));
	}

	private static String format(TagEvent event, String what) {
		StringBuilder builder = new StringBuilder("Window ");
		HWnd hwnd = event.getWindow().getHWnd();
		builder.append(hwnd.toString());
		if(!event.isWindowDestroyed()) {
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
		builder.append(' ');
		builder.append(what);
		builder.append(" tag ");
		Tag tag = event.getTag();
		builder.append(String.valueOf(tag.getID()));
		String name = tag.getName();
		if(name != null) {
			builder.append(" (");
			builder.append(name);
			builder.append(')');
		}
		return builder.toString();
	}

}
