/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractDataUpdateSupport extends AbstractUpdateSupport implements IDataUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractDataUpdateSupport.class);
	//
	private static Object object = null;
	private static String topic = "";
	//
	private IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
	private List<EventHandler> registeredEventHandler;

	public AbstractDataUpdateSupport(MPart part) {
		super(part);
		registeredEventHandler = new ArrayList<EventHandler>();
		registerEvents();
	}

	public void registerEvent(String topic, String property) {

		if(eventBroker != null) {
			registeredEventHandler.add(registerEventHandler(eventBroker, topic, property));
		}
	}

	@Override
	public Object getObject() {

		return object;
	}

	@Override
	public String getTopic() {

		return topic;
	}

	@PreDestroy
	private void preDestroy() {

		if(eventBroker != null) {
			for(EventHandler eventHandler : registeredEventHandler) {
				eventBroker.unsubscribe(eventHandler);
			}
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String topic, String property) {

		EventHandler eventHandler = new EventHandler() {

			public void handleEvent(Event event) {

				try {
					Object object = event.getProperty(property);
					setObject(object, topic);
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	public void setObject(Object myObject, String myTopic) {

		/*
		 * Remember the selection.
		 */
		object = myObject;
		topic = myTopic;
		/*
		 * Do an update only if the part is visible.
		 */
		if(doUpdate()) {
			updateObject(object, topic);
		}
	}
}
