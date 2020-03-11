package org.unclesniper.winaux;

public interface WindowCreationHook {

	public static final int FL_SWALLOW = 001;
	public static final int FL_REMOVE  = 002;

	int onWindowCreated(AuxEngine engine, KnownWindow window);

}
