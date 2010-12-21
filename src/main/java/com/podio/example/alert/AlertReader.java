package com.podio.example.alert;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

/**
 * Reads a Google Alerts feed
 */
public class AlertReader {

	private final String feed;

	/**
	 * Creates a new reader for the given feed
	 * 
	 * @param feed
	 *            The feed to read
	 */
	public AlertReader(String feed) {
		super();
		this.feed = feed;
	}

	/**
	 * Retrieves the feed from delicious as an RSS feed
	 * 
	 * @return The given feed
	 * @throws Exception
	 *             If any error occurs during communication with google
	 */
	private SyndFeed getFeed() throws Exception {
		FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
		FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedInfoCache);
		return feedFetcher.retrieveFeed(new URL(feed));
	}

	public List<Alert> read() throws Exception {
		SyndFeed syndFeed = getFeed();

		List<Alert> alerts = new ArrayList<Alert>();
		@SuppressWarnings("unchecked")
		List<SyndEntry> entries = syndFeed.getEntries();
		for (SyndEntry entry : entries) {
			if (entry.getTitle().equals("Feeds for Google Alerts")) {
				continue;
			}

			String id = entry.getUri().substring(
					entry.getUri().lastIndexOf('/') + 1);

			SyndContent content = (SyndContent) entry.getContents().get(0);

			alerts.add(new Alert(id, entry.getTitle(), content.getValue(),
					entry.getLink()));
		}

		return alerts;
	}
}
