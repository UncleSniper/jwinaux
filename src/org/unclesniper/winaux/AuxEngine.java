package org.unclesniper.winaux;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.IdentityHashMap;
import org.unclesniper.winwin.Msg;
import org.unclesniper.winwin.HWnd;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.unclesniper.winwin.Hotkey;
import org.unclesniper.winwin.WinAPI;
import org.unclesniper.winwin.WinHook;
import java.util.concurrent.BlockingQueue;
import org.unclesniper.winwin.WinEventProc;
import org.unclesniper.winaux.util.TypeMap;
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

	private final Listeners<TagListener> tagListeners = new Listeners<TagListener>();

	private final TypeMap extensions = new TypeMap();

	private final Map<WindowCreationHook, Long> windowCreationHooks
			= new IdentityHashMap<WindowCreationHook, Long>();

	private volatile Map<WindowCreationHook, Long> windowCreationHookCache;

	private Tag exemptionTag;

	private Worker workerThread;

	public AuxEngine() {}

	public boolean doYaThang(Configuration config, Runnable onError) {
		synchronized(thangLock) {
			boolean hooking = false;
			AuxWinEventProc winEventProc = new AuxWinEventProc();
			try {
				thangThreadId = WinAPI.getCurrentThreadId();
				int hkid = 0;
				Map<Class<?>, Map<TagProvider, Void>> listenerTypes = new HashMap<Class<?>, Map<TagProvider, Void>>();
				for(TagProvider provider : config.getTagProviders()) {
					Consumer<Class<?>> listenerTypeSink = AuxEngine.makeListenerTypeSink(listenerTypes, provider);
					provider.getPredicate().collectListenerTypes(listenerTypeSink);
				}
				Map<Class<?>, TagUpdater> tagUpdaters = config.getTagUpdaters();
				for(Map.Entry<Class<?>, Map<TagProvider, Void>> entry : listenerTypes.entrySet()) {
					Class<?> ltype = entry.getKey();
					TagUpdater updater = tagUpdaters.get(ltype);
					if(updater == null)
						throw new NoSuchTagUpdaterException(ltype);
					updater.registerListener(entry.getValue().keySet(), this);
				}
				for(ShellEventListener listener : config.getShellEventListeners())
					addShellEventListener(listener);
				for(TagListener listener : config.getTagListeners())
					addTagListener(listener);
				for(AuxExtension extension : config.getExtensions())
					extension.registerExtension(this);
				exemptionTag = config.getExemptionTag();
				if(exemptionTag == null)
					exemptionTag = new Tag("exempt");
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
				slate(this::internInitialWindows);
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
				workerThread = null;
				taskQueue.clear();
				shellEventListeners.clear();
				tagListeners.clear();
				extensions.clear();
				exemptionTag = null;
			}
		}
		return true;
	}

	private static Consumer<Class<?>> makeListenerTypeSink(Map<Class<?>, Map<TagProvider, Void>> listenerTypes,
			TagProvider provider) {
		return ltype -> {
			Map<TagProvider, Void> set = listenerTypes.get(ltype);
			if(set == null) {
				set = new IdentityHashMap<TagProvider, Void>();
				listenerTypes.put(ltype, set);
			}
			set.put(provider, null);
		};
	}

	private void internInitialWindows() {
		HWnd.enumWindows(hwnd -> {
			internWindow(hwnd);
			return true;
		});
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
		if(consultWindowCreationHooks(kw))
			fireWindowCreate(kw);
		return kw;
	}

	private void fireWindowCloaked(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowCloaked(event));
	}

	private void fireWindowCreate(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowCreate(event));
	}

	private void fireWindowDestroy(HWnd win) {
		try {
			ShellEvent event = new ShellEvent(this, null);
			shellEventListeners.fire(listener -> listener.windowDestroy(event, win));
		}
		finally {
			KnownWindow known;
			synchronized(knownWindows) {
				known = knownWindows.remove(win);
			}
			if(known != null) {
				List<Tag> lost = new LinkedList<Tag>();
				known.removeAllTagsNoGlobalNotify(lost::add);
				for(Tag ltag : lost)
					fireTagLost(known, ltag, true);
			}
		}
	}

	private void fireWindowFocus(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowFocus(event));
	}

	private void fireWindowNameChange(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowNameChange(event));
	}

	private void fireWindowReorder(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowReorder(event));
	}

	private void fireWindowShow(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowShow(event));
	}

	private void fireWindowUncloaked(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowUncloaked(event));
	}

	private void fireDesktopSwitch() {
		ShellEvent event = new ShellEvent(this, null);
		shellEventListeners.fire(listener -> listener.desktopSwitch(event));
	}

	private void fireForeground(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.foreground(event));
	}

	private void fireWindowMinimizeEnd(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowMinimizeEnd(event));
	}

	private void fireWindowMinimizeStart(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowMinimizeStart(event));
	}

	private void fireWindowMoveSizeEnd(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
		shellEventListeners.fire(listener -> listener.windowMoveSizeEnd(event));
	}

	private void fireWindowMoveSizeStart(KnownWindow win) {
		ShellEvent event = new ShellEvent(this, win);
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

	private void fireTagGained(KnownWindow window, Tag tag) {
		TagEvent event = new TagEvent(this, window, tag, false);
		tagListeners.fire(listener -> listener.tagGained(event));
	}

	private void fireTagLost(KnownWindow window, Tag tag, boolean windowDestroyed) {
		TagEvent event = new TagEvent(this, window, tag, windowDestroyed);
		tagListeners.fire(listener -> listener.tagLost(event));
	}

	public void addTagListener(TagListener listener) {
		tagListeners.add(listener);
	}

	public boolean removeTagListener(TagListener listener) {
		return tagListeners.remove(listener);
	}

	public TagGrant grantTag(KnownWindow window, Tag tag) {
		if(window == null)
			throw new IllegalArgumentException("Window cannot be null");
		if(tag == null)
			throw new IllegalArgumentException("Tag cannot be null");
		TagGrant grant = new TagGrant(this, tag, window);
		boolean gained = window.addTagNoGlobalNotify(grant);
		tag.addWindowNoGlobalNotify(grant);
		if(gained)
			fireTagGained(window, tag);
		return grant;
	}

	void revokeTagGrant(TagGrant grant) {
		if(grant == null)
			throw new IllegalArgumentException("Tag grant cannot be null");
		KnownWindow window = grant.getWindow();
		boolean lost = window.removeTagNoGlobalNotify(grant);
		Tag tag = grant.getTag();
		tag.removeWindowNoGlobalNotify(grant);
		if(lost)
			fireTagLost(window, tag, false);
	}

	public <T> T setExtension(Class<T> key, T value) {
		synchronized(extensions) {
			return extensions.put(key, value);
		}
	}

	public <T> T removeExtension(Class<T> key) {
		synchronized(extensions) {
			return extensions.remove(key);
		}
	}

	public <T> T getExtension(Class<T> key) {
		synchronized(extensions) {
			return extensions.get(key);
		}
	}

	public <T> T getExtension(Class<T> key, Supplier<T> constructor) {
		synchronized(extensions) {
			T t = extensions.get(key);
			if(t != null || constructor == null)
				return t;
			t = constructor.get();
			extensions.put(key, t);
			return t;
		}
	}

	public Tag getExemptionTag() {
		return exemptionTag;
	}

	public boolean isExempt(KnownWindow window) {
		return window.hasTag(exemptionTag);
	}

	public boolean isWorkerThread() {
		return workerThread != null && workerThread.getId() == Thread.currentThread().getId();
	}

	public void addWindowCreationHook(WindowCreationHook hook) {
		if(hook == null)
			return;
		synchronized(windowCreationHooks) {
			windowCreationHookCache = null;
			Long oldCount = windowCreationHooks.get(hook);
			long newCount = (oldCount == null ? 0l : oldCount.longValue()) + 1l;
			windowCreationHooks.put(hook, newCount);
		}
	}

	public boolean removeWindowCreationHook(WindowCreationHook hook) {
		if(hook == null)
			return false;
		synchronized(windowCreationHooks) {
			windowCreationHookCache = null;
			Long oldCount = windowCreationHooks.get(hook);
			if(oldCount == null)
				return false;
			long newCount = oldCount.longValue() - 1l;
			if(newCount < 0l)
				return false;
			if(newCount == 0l)
				windowCreationHooks.remove(hook);
			else
				windowCreationHooks.put(hook, newCount);
		}
		return true;
	}

	private boolean consultWindowCreationHooks(KnownWindow window) {
		Map<WindowCreationHook, Long> c = windowCreationHookCache;
		if(c == null) {
			c = new IdentityHashMap<WindowCreationHook, Long>();
			synchronized(windowCreationHooks) {
				for(Map.Entry<WindowCreationHook, Long> entry : windowCreationHooks.entrySet())
					c.put(entry.getKey(), entry.getValue());
				windowCreationHookCache = c;
			}
		}
		Iterator<WindowCreationHook> it = c.keySet().iterator();
		while(it.hasNext()) {
			WindowCreationHook hook = it.next();
			int flags = hook.onWindowCreated(this, window);
			if((flags & WindowCreationHook.FL_REMOVE) != 0)
				it.remove();
			if((flags & WindowCreationHook.FL_SWALLOW) != 0)
				return false;
		}
		return true;
	}

}
