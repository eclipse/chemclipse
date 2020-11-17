/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DataUpdateSupport {

	private static final Logger logger = Logger.getLogger(DataUpdateSupport.class);
	//
	private IEventBroker eventBroker;
	//
	private Map<String, EventHandler> handlerMap = new HashMap<>();
	private Map<String, Set<String>> propertiesMap = new HashMap<>();
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

	public static boolean isVisible(Composite composite) {

		if(composite != null && !composite.isDisposed()) {
			/*
			 * UI Thread
			 */
			if(Thread.currentThread() == Display.getDefault().getThread()) {
				return composite.isVisible();
			}
		}
		return false;
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

	public List<Object> getUpdates(String topic) {

		return objectMap.getOrDefault(topic, Collections.emptyList());
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
			handlerMap.remove(topic);
			propertiesMap.remove(topic);
			objectMap.remove(topic);
			logger.info("Subscription removed on topic '" + topic + "'.");
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
		propertiesMap.clear();
		objectMap.clear();
		//
		logger.info("Subscriptions have been completely removed.");
	}

	private void registerEventHandler(String topic, String[] properties) {

		/*
		 * Register a new handler on the given topic.
		 */
		if(!handlerMap.containsKey(topic)) {
			/*
			 * Probably check additionally, that topic and properties are matching?
			 * Normally, a topic is not registered with different properties.
			 */
			Set<String> propertySet = propertiesMap.get(topic);
			if(propertySet == null) {
				propertySet = new HashSet<>();
				propertySet.addAll(Arrays.asList(properties));
				propertiesMap.put(topic, propertySet);
			} else {
				if(properties.length != propertySet.size()) {
					logger.warn("Subscription properties '" + Arrays.asList(properties) + "' on topic '" + topic + "' differ from existing handler properties '" + propertySet.toArray() + "'.");
				}
			}
			/*
			 * Handler
			 */
			EventHandler eventHandler = new EventHandler() {

				@Override
				public void handleEvent(Event event) {

					update(event, properties);
				}
			};
			/*
			 * Subscribe the new handler.
			 */
			eventBroker.subscribe(topic, eventHandler);
			handlerMap.put(topic, eventHandler);
			//
			logger.info("Subscription added on topic '" + topic + "' and properties '" + Arrays.asList(properties) + "'.");
		}
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
