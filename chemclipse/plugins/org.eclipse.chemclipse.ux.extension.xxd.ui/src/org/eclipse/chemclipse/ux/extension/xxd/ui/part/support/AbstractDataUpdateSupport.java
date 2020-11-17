/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - changes to partsupport
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractDataUpdateSupport extends AbstractUpdateSupport implements IDataUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractDataUpdateSupport.class);
	//
	private final List<Object> objects = new ArrayList<Object>();
	private String topic = "";
	//
	private final IEventBroker eventBroker = Activator.getDefault().getEventBroker();
	private final List<EventHandler> registeredEventHandler = new ArrayList<EventHandler>();

	public AbstractDataUpdateSupport(MPart part) {

		super(part);
		/*
		 * Additional events.
		 */
		registerEvents();
	}

	public void registerEvent(String topic, String property) {

		registerEvent(topic, new String[]{property});
	}

	public void registerEvent(String topic, String[] properties) {

		if(eventBroker != null) {
			registeredEventHandler.add(registerEventHandler(eventBroker, topic, properties));
		}
	}

	@Override
	public List<Object> getObjects() {

		return objects;
	}

	@Override
	public String getTopic() {

		return topic;
	}

	@PreDestroy
	protected void preDestroy() {

		if(eventBroker != null) {
			for(EventHandler eventHandler : registeredEventHandler) {
				eventBroker.unsubscribe(eventHandler);
			}
			eventBroker.send(IChemClipseEvents.TOPIC_PART_CLOSED, getClass().getSimpleName());
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String topic, String[] properties) {

		EventHandler eventHandler = new EventHandler() {

			@Override
			public void handleEvent(Event event) {

				try {
					objects.clear();
					AbstractDataUpdateSupport.this.topic = topic;
					for(String property : properties) {
						Object object = event.getProperty(property);
						objects.add(object);
					}
					update(topic);
				} catch(Exception e) {
					logger.warn(e + "\t" + event);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private void update(String topic) {

		/*
		 * Do an update only if the part is visible.
		 */
		if(doUpdate()) {
			updateObjects(objects, topic);
		}
	}
}
