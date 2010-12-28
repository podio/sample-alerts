package com.podio.sample.alert;

public final class Alert {

	private final String id;

	private final String title;

	private final String content;

	private final String link;

	public Alert(String id, String title, String content, String link) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.link = link;
	}

	@Override
	public String toString() {
		return "Alert [id=" + id + ", title=" + title + ", content=" + content
				+ ", link=" + link + "]";
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getLink() {
		return link;
	}
}
