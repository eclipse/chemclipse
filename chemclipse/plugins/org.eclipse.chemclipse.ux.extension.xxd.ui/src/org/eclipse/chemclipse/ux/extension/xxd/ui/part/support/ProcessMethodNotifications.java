/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@Creatable
@Singleton
public class ProcessMethodNotifications extends AbstractNotifications<IProcessMethod> {

	private EventHandler eventHandlerCreate = new EventHandler() {

		@Override
		public void handleEvent(Event event) {

			created((IProcessMethod)event.getProperty(IChemClipseEvents.EVENT_BROKER_DATA));
		}
	};
	//
	private EventHandler eventHandlerSelect = new EventHandler() {

		@Override
		public void handleEvent(Event event) {

			select((IProcessMethod)event.getProperty(IChemClipseEvents.EVENT_BROKER_DATA));
		}
	};
	//
	private EventHandler eventHandlerUpdate = new EventHandler() {

		@Override
		public void handleEvent(Event event) {

			updated((IProcessMethod)event.getProperty(IChemClipseEvents.EVENT_BROKER_DATA), (IProcessMethod)event.getProperty(IChemClipseEvents.PROPERTY_METHOD_OLD_OBJECT));
		}
	};

	@PostConstruct
	protected void setupListener(IEventBroker eventBroker) {

		eventBroker.subscribe(IChemClipseEvents.TOPIC_METHOD_CREATED, null, eventHandlerCreate, false);
		eventBroker.subscribe(IChemClipseEvents.TOPIC_METHOD_SELECTED, null, eventHandlerSelect, false);
		eventBroker.subscribe(IChemClipseEvents.TOPIC_METHOD_UPDATE, null, eventHandlerUpdate, false);
	}

	protected void teardownListener(IEventBroker eventBroker) {

		eventBroker.unsubscribe(eventHandlerCreate);
		eventBroker.unsubscribe(eventHandlerSelect);
		eventBroker.unsubscribe(eventHandlerUpdate);
	}
}
