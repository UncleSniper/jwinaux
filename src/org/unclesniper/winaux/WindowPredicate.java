package org.unclesniper.winaux;

import java.util.function.Consumer;

public interface WindowPredicate {

	void collectListenerTypes(Consumer<Class<?>> sink);

	boolean matches(AuxEngine engine, KnownWindow window);

}
