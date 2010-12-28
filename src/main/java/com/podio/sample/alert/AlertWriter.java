package com.podio.sample.alert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.podio.APIFactory;
import com.podio.app.Application;
import com.podio.app.ApplicationField;
import com.podio.item.FieldValuesUpdate;
import com.podio.item.ItemAPI;
import com.podio.item.ItemBadge;
import com.podio.item.ItemCreate;

/**
 * Writes alerts into a Podio app
 */
public class AlertWriter {

	private final int appId;

	private final APIFactory apiFactory;

	/**
	 * Creates a new writer that will write to the given app using the API
	 * 
	 * @param appId
	 *            The id of the app
	 * @param apiFactory
	 *            The API class to use
	 */
	public AlertWriter(int appId, APIFactory apiFactory) {
		super();
		this.appId = appId;
		this.apiFactory = apiFactory;
	}

	/**
	 * Creates the app mapping for the app id from the configuration
	 * 
	 * @return The app mapping created
	 */
	private AppMapping getAppMapping() {
		Application app = apiFactory.getAppAPI().getApp(appId);

		return AppMapping.get(app);
	}

	/**
	 * Saves the alerts as items in Podio
	 * 
	 * @param alerts
	 *            The alerts to save
	 */
	public void write(List<Alert> alerts) {
		AppMapping mapping = getAppMapping();
		ItemAPI itemAPI = apiFactory.getItemAPI();

		for (Alert alert : alerts) {
			// Check that the bookmark has not already been added
			List<ItemBadge> items = itemAPI.getItemsByExternalId(appId,
					alert.getId()).getItems();
			if (items.size() == 0) {
				// No items exists, so add the item
				itemAPI.addItem(appId, mapping.map(alert), false);
			}
		}
	}

	/**
	 * Maintans a mapping for the individual fields in the app
	 */
	private static final class AppMapping {

		private final int title;

		private final int content;

		private final int link;

		private AppMapping(int title, int content, int link) {
			super();
			this.title = title;
			this.content = content;
			this.link = link;
		}

		/**
		 * Returns the create object to be used when creating the object in
		 * Podio
		 * 
		 * @param alert
		 *            The alert to map
		 * @return The mapped object
		 */
		public ItemCreate map(Alert alert) {
			List<FieldValuesUpdate> fields = new ArrayList<FieldValuesUpdate>();
			fields.add(new FieldValuesUpdate(title, "value", alert.getTitle()));
			fields.add(new FieldValuesUpdate(content, "value", alert
					.getContent()));
			fields.add(new FieldValuesUpdate(link, "value", alert.getLink()));

			return new ItemCreate(alert.getId(), fields,
					Collections.<Integer> emptyList(),
					Collections.<String> emptyList());
		}

		/**
		 * Creates a mapping configuration based on the app
		 * 
		 * @param app
		 *            The app to create a mapping for
		 * @return The mapping created
		 */
		public static AppMapping get(Application app) {
			List<ApplicationField> fields = app.getFields();

			return new AppMapping(getField(fields, "Title"), getField(fields,
					"Content"), getField(fields, "Link"));
		}

		/**
		 * Finds a field in the list of fields with the given name
		 * 
		 * @param fields
		 *            The fields to search through
		 * @param label
		 *            The label to search for
		 * @return The id of the matching field
		 */
		private static int getField(List<ApplicationField> fields, String label) {
			for (ApplicationField field : fields) {
				if (field.getConfiguration().getLabel().equals(label)) {
					return field.getId();
				}
			}

			throw new IllegalArgumentException("No field found with the label "
					+ label);
		}
	}
}
