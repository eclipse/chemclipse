/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.ux.extension.ui.support.PartListenerAdapter;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractDataUpdateSupport extends AbstractUpdateSupport implements IDataUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractDataUpdateSupport.class);
	//
	private List<Object> objects = new ArrayList<Object>();
	private String topic = "";
	//
	private IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
	private List<EventHandler> registeredEventHandler;

	public AbstractDataUpdateSupport(MPart part) {
		super(part);
		handlePartCloseEvent(part);
		registeredEventHandler = new ArrayList<EventHandler>();
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
	private void preDestroy() {

		if(eventBroker != null) {
			for(EventHandler eventHandler : registeredEventHandler) {
				eventBroker.unsubscribe(eventHandler);
			}
		}
	}

	private void handlePartCloseEvent(MPart myPart) {

		if(myPart != null) {
			EPartService ePartService = myPart.getContext().get(EPartService.class);
			ePartService.addPartListener(new PartListenerAdapter() {

				@Override
				public void partHidden(MPart part) {

					if(part.getElementId().equals(myPart.getElementId())) {
						if(part.isCloseable()) {
							PartSupport.setPartVisibility(part, false);
						}
					}
				}
			});
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String topic, String[] properties) {

		EventHandler eventHandler = new EventHandler() {

			public void handleEvent(Event event) {

				try {
					objects.clear();
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
