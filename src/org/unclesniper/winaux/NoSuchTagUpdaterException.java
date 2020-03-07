package org.unclesniper.winaux;

public class NoSuchTagUpdaterException extends RuntimeException {

	private final Class<?> keyClass;

	public NoSuchTagUpdaterException(Class<?> keyClass) {
		super("No TagUpdater registered for listener type " + keyClass.getName());
		this.keyClass = keyClass;
	}

	public Class<?> getKeyClass() {
		return keyClass;
	}

}
