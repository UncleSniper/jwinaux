package org.unclesniper.winaux;

import java.util.Map;
import java.util.HashMap;
import org.unclesniper.winwin.Msg;
import org.unclesniper.winwin.HWnd;
import org.unclesniper.winwin.Hotkey;
import org.unclesniper.winwin.WinAPI;
import org.unclesniper.winwin.WinHook;
import java.util.concurrent.BlockingQueue;
import org.unclesniper.winwin.WinEventProc;
import org.unclesniper.winwin.HWinEventHook;
import java.util.concurrent.LinkedBlockingQueue;

public final class AuxEngine {

	private final class AuxWinEventProc implements WinEventProc {

		public AuxWinEventProc() {}

		@Override
		public void windowCloaked(HWnd hwnd, long idChild) {
			slate(() -> fireWindowCloaked(internWindow(hwnd)));
		}

		@Override
		public void windowCreate(HWnd hwnd, long idChild) {
			slate(() -> internWindow(hwnd));
		}

		@Override
		public void windowDestroy(HWnd hwnd, long idChild) {
			slate(() -> fireWindowDestroy(hwnd));
		}

		@Override
		public void windowFocus(HWnd hwnd, long idChild) {
			slate(() -> fireWindowFocus(internWindow(hwnd)));
		}

		@Override
		public void windowNameChange(HWnd hwnd, long idChild) {
			slate(() -> fireWindowNameChange(internWindow(hwnd)));
		}

		@Override
		public void windowReorder(HWnd hwnd, long idChild) {
			slate(() -> fireWindowReorder(internWindow(hwnd)));
		}

		@Override
		public void windowShow(HWnd hwnd, long idChild) {
			slate(() -> fireWindowShow(internWindow(hwnd)));
		}

		@Override
		public void windowUncloaked(HWnd hwnd, long idChild) {
			slate(() -> fireWindowUncloaked(internWindow(hwnd)));
		}

		@Override
		public void desktopSwitch(long idChild) {
			slate(() -> fireDesktopSwitch());
		}

		@Override
		public void foreground(HWnd hwnd) {
			slate(() -> fireForeground(internWindow(hwnd)));
		}

		@Override
		public void windowMinimizeEnd(HWnd hwnd) {
			slate(() -> fireWindowMinimizeEnd(internWindow(hwnd)));
		}

		@Override
		public void windowMinimizeStart(HWnd hwnd) {
			slate(() -> fireWindowMinimizeStart(internWindow(hwnd)));
		}

		@Override
		public void windowMoveSizeEnd(HWnd hwnd) {
			slate(() -> fireWindowMoveSizeEnd(internWindow(hwnd)));
		}

		@Override
		public void windowMoveSizeStart(HWnd hwnd) {
			slate(() -> fireWindowMoveSizeStart(internWindow(hwnd)));
		}

	}

	private final class Worker extends Thread {

		private volatile boolean shouldStop;

		public Worker() {
			start();
		}

		public void pleaseStop() {
			shouldStop = true;
		}

		@Override
		public void run() {
			while(!shouldStop) {
				try {
					Runnable task;
					try {
						task = taskQueue.take();
					}
					catch(InterruptedException ie) {
						continue;
					}
					task.run();
				}
				catch(RuntimeException e) {
					feedError(e);
				}
				catch(Error e) {
					feedError(e);
				}
			}
		}

	}

	private final Object thangLock = new Object();

	private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();

	private final Map<HWnd, KnownWindow> knownWindows = new HashMap<HWnd, KnownWindow>();

	private final Listeners<ShellEventListener> shellEventListeners = new Listeners<ShellEventListener>();

	private long thangThreadId;

	public AuxEngine() {}

	public boolean doYaThang(Configuration config, Runnable onError) {
		synchronized(thangLock) {
			boolean hooking = false;
			Worker workerThread = null;
			AuxWinEventProc winEventProc = new AuxWinEventProc();
			try {
				thangThreadId = WinAPI.getCurrentThreadId();
				int hkid = 0;
				for(AuxHotkey hk : config.getHotkeys()) {
					if(hk.isLowLevel())
						WinHook.registerLowLevelHotKey(null, hkid++, hk.getModifiers(), hk.getKey(),
								new AuxHotkeyHandler(this, hk.getAction()));
					else
						Hotkey.registerHotKey(null, hkid++, hk.getModifiers(), hk.getKey(),
								new AuxHotkeyHandler(this, hk.getAction()));
				}
				HWinEventHook weHookLo = HWinEventHook.setWinEventHook(HWinEventHook.EVENT_SYSTEM_FOREGROUND,
						HWinEventHook.EVENT_SYSTEM_DESKTOPSWITCH, HWinEventHook.WINEVENT_SKIPOWNPROCESS, winEventProc);
				HWinEventHook weHookHi = HWinEventHook.setWinEventHook(HWinEventHook.EVENT_OBJECT_CREATE,
						HWinEventHook.EVENT_OBJECT_UNCLOAKED, HWinEventHook.WINEVENT_SKIPOWNPROCESS, winEventProc);
				WinHook.startHooks(WinHook.WH_KEYBOARD_LL);
				hooking = true;
				workerThread = new Worker();
				Msg.pumpAll();
				workerThread.pleaseStop();
				taskQueue.add(() -> {});
				weHookLo.unhookWinEvent();
				weHookHi.unhookWinEvent();
			}
			catch(RuntimeException e) {
				ExceptionWindow.showException(e, onError == null ? null : exwin -> onError.run());
				return false;
			}
			catch(Error e) {
				ExceptionWindow.showException(e, onError == null ? null : exwin -> onError.run());
				return false;
			}
			finally {
				thangThreadId = 0l;
				if(hooking)
					WinHook.stopHooks();
				if(workerThread != null) {
					workerThread.pleaseStop();
					taskQueue.add(() -> {});
					for(;;) {
						try {
							workerThread.join();
							break;
						}
						catch(InterruptedException ie) {}
					}
				}
				taskQueue.clear();
				shellEventListeners.clear();
			}
		}
		return true;
	}

	public void feedError(Throwable exception) {
		ExceptionWindow.showException(exception, null);
	}

	public void slate(Runnable task) {
		if(task != null)
			taskQueue.add(task);
	}

	public KnownWindow internWindow(HWnd hwnd) {
		if(hwnd == null)
			return null;
		KnownWindow kw;
		synchronized(knownWindows) {
			kw = knownWindows.get(hwnd);
			if(kw != null)
				return kw;
			kw = new KnownWindow(hwnd);
			knownWindows.put(hwnd, kw);
		}
		fireWindowCreate(kw);
		return kw;
	}

	private void fireWindowCloaked(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowCloaked(event));
	}

	private void fireWindowCreate(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowCreate(event));
	}

	private void fireWindowDestroy(HWnd win) {
		try {
			ShellEvent event = new ShellEvent(null);
			shellEventListeners.fire(listener -> listener.windowDestroy(event, win));
		}
		finally {
			synchronized(knownWindows) {
				knownWindows.remove(win);
			}
		}
	}

	private void fireWindowFocus(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowFocus(event));
	}

	private void fireWindowNameChange(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowNameChange(event));
	}

	private void fireWindowReorder(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowReorder(event));
	}

	private void fireWindowShow(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowShow(event));
	}

	private void fireWindowUncloaked(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowUncloaked(event));
	}

	private void fireDesktopSwitch() {
		ShellEvent event = new ShellEvent(null);
		shellEventListeners.fire(listener -> listener.desktopSwitch(event));
	}

	private void fireForeground(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.foreground(event));
	}

	private void fireWindowMinimizeEnd(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowMinimizeEnd(event));
	}

	private void fireWindowMinimizeStart(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowMinimizeStart(event));
	}

	private void fireWindowMoveSizeEnd(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowMoveSizeEnd(event));
	}

	private void fireWindowMoveSizeStart(KnownWindow win) {
		ShellEvent event = new ShellEvent(win);
		shellEventListeners.fire(listener -> listener.windowMoveSizeStart(event));
	}

	public void postQuitMessage() {
		Msg.postQuitMessageToThread(thangThreadId, 0);
	}

	public void addShellEventListener(ShellEventListener listener) {
		shellEventListeners.add(listener);
	}

	public boolean removeShellEventListener(ShellEventListener listener) {
		return shellEventListeners.remove(listener);
	}

}
