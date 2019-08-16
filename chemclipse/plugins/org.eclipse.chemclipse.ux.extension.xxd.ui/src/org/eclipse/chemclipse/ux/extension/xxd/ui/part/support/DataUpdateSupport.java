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

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DataUpdateSupport {

	private IEventBroker eventBroker;
	//
	private Map<String, EventHandler> handlerMap = new HashMap<>();
	private Map<String, List<Object>> objectMap = new HashMap<>();
	//
	private List<IDataUpdateListener> updateListeners = new ArrayList<>();

	public DataUpdateSupport(IEventBroker eventBroker) throws IllegalArgumentException {
		if(eventBroker == null) {
			throw new IllegalArgumentException("The event broker must be not null.");
		} else {
			this.eventBroker = eventBroker;
		}
	}

	public void add(IDataUpdateListener updateListener) {

		if(updateListener != null) {
			updateListeners.add(updateListener);
		}
	}

	public void remove(IDataUpdateListener updateListener) {

		if(updateListener != null) {
			updateListeners.remove(updateListener);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object> getUpdates(String topic) {

		return objectMap.getOrDefault(topic, Collections.EMPTY_LIST);
	}

	public void subscribe(String topic, String property) {

		subscribe(topic, new String[]{property});
	}

	public void subscribe(String topic, String[] properties) {

		if(topic != null && !"".equals(topic) && properties != null) {
			registerEventHandler(topic, properties);
		}
	}

	public void unsubscribe(String topic) {

		EventHandler eventHandler = handlerMap.get(topic);
		if(eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	@Override
	protected void finalize() throws Throwable {

		unsubscribeEvents();
		super.finalize();
	}

	private void unsubscribeEvents() {

		if(eventBroker != null) {
			for(EventHandler eventHandler : handlerMap.values()) {
				eventBroker.unsubscribe(eventHandler);
			}
		}
		//
		handlerMap.clear();
		objectMap.clear();
	}

	private void registerEventHandler(String topic, String[] properties) {

		EventHandler eventHandler = new EventHandler() {

			@Override
			public void handleEvent(Event event) {

				update(event, properties);
			}
		};
		/*
		 * Remove an existing handler with
		 * the same topic.
		 */
		if(handlerMap.containsKey(topic)) {
			unsubscribe(topic);
		}
		/*
		 * Subscribe the new handler.
		 */
		eventBroker.subscribe(topic, eventHandler);
		handlerMap.put(topic, eventHandler);
	}

	private void update(Event event, String[] properties) {

		String topic = event.getTopic();
		/*
		 * Create a new topic entry.
		 */
		if(!objectMap.containsKey(topic)) {
			objectMap.put(topic, new ArrayList<Object>());
		}
		/*
		 * Get the list and re-evaluate.
		 */
		List<Object> objects = objectMap.get(topic);
		objects.clear();
		//
		if(properties != null) {
			for(String property : properties) {
				Object object = event.getProperty(property);
				if(object != null) {
					objects.add(object);
				}
			}
		}
		/*
		 * Update the map.
		 */
		objectMap.put(topic, objects);
		fireUpdate(topic, objects);
	}

	private void fireUpdate(String topic, List<Object> objects) {

		for(IDataUpdateListener updateListener : updateListeners) {
			updateListener.update(topic, objects);
		}
	}
}
