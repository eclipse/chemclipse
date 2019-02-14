/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/*
 * TEST!
 * Ask Philip when trying to modify it.
 */
public class SelectionUpdateSupport {

	private IEventBroker eventBroker;
	private List<EventHandler> registeredEventHandler = new ArrayList<>();
	private Map<String, List<Object>> topicMap = new HashMap<>();

	public SelectionUpdateSupport(IEventBroker eventBroker) {
		this.eventBroker = eventBroker;
		subscribeEvents();
	}

	@Override
	protected void finalize() throws Throwable {

		unsubscribeEvents();
		super.finalize();
	}

	@SuppressWarnings("unchecked")
	public List<Object> getLatestSelection(String topic) {

		return topicMap.getOrDefault(topic, Collections.EMPTY_LIST);
	}

	private void subscribeEvents() {

		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_LOAD_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION_XXD);
		registerEvent(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_SCAN);
		registerEvent(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_PEAK);
	}

	private void unsubscribeEvents() {

		topicMap.clear();
		//
		if(eventBroker != null) {
			for(EventHandler eventHandler : registeredEventHandler) {
				eventBroker.unsubscribe(eventHandler);
			}
		}
	}

	private void registerEvent(String topic, String property) {

		registerEvent(topic, new String[]{property});
	}

	private void registerEvent(String topic, String[] properties) {

		if(eventBroker != null) {
			registeredEventHandler.add(registerEventHandler(eventBroker, topic, properties));
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String topic, String[] properties) {

		EventHandler eventHandler = new EventHandler() {

			@Override
			public void handleEvent(Event event) {

				updateTopicMap(event, properties);
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private void updateTopicMap(Event event, String[] properties) {

		String topic = event.getTopic();
		/*
		 * Create a new topic entry.
		 */
		if(!topicMap.containsKey(topic)) {
			topicMap.put(topic, new ArrayList<Object>());
		}
		/*
		 * Get the list and re-evaluate.
		 */
		List<Object> objects = topicMap.get(topic);
		objects.clear();
		//
		// String[] properties = event.getPropertyNames();
		if(properties != null) {
			for(String property : properties) {
				Object object = event.getProperty(property);
				objects.add(object);
			}
		}
		/*
		 * Update the map.
		 */
		topicMap.put(topic, objects);
	}
}
