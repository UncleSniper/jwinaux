package org.unclesniper.winaux;

public class TagProvider {

	private Tag tag;

	private WindowPredicate predicate;

	public TagProvider() {}

	public TagProvider(Tag tag, WindowPredicate predicate) {
		this.tag = tag;
		this.predicate = predicate;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public WindowPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(WindowPredicate predicate) {
		this.predicate = predicate;
	}

	public void apply(KnownWindow window) {
		//TODO
	}

}
