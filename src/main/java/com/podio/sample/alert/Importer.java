package com.podio.sample.alert;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import com.podio.APIFactory;
import com.podio.ResourceFactory;
import com.podio.oauth.OAuthClientCredentials;
import com.podio.oauth.OAuthUsernameCredentials;

/**
 * Imports alerts from Google Alerts to a Podio app. The feed, app id and
 * authentication configuration must be given in a configuration file.
 * 
 * The app in Podio must have 3 fields with the labels Title, Content and Link.
 */
public final class Importer {

	private Importer() {
	}

	/**
	 * Start the importer with the given configuration file. This will read the
	 * alerts from Google and write them to an app in Podio.
	 * 
	 * @param args
	 *            The first parameter must be the path to the configuration file
	 * @throws Exception
	 *             If any error occurs during execution
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new IllegalArgumentException(
					"Expected exactly one argument which should be the path of the configuration file");
		}

		Properties config = new Properties();
		config.load(new FileInputStream(args[0]));

		String endpoint = config.getProperty("podio.endpoint", "podio.com");

		ResourceFactory podioAPI = new ResourceFactory("api." + endpoint,
				"upload." + endpoint, 443, true, false,
				new OAuthClientCredentials(config
						.getProperty("podio.client.mail"), config
						.getProperty("podio.client.secret")),
				new OAuthUsernameCredentials(config
						.getProperty("podio.user.mail"), config
						.getProperty("podio.user.password")));
		APIFactory apiFactory = new APIFactory(podioAPI);

		String feed = config.getProperty("google.feed");
		int appId = Integer.parseInt(config.getProperty("podio.app"));

		List<Alert> alerts = new AlertReader(feed).read();
		new AlertWriter(appId, apiFactory).write(alerts);
	}
}
