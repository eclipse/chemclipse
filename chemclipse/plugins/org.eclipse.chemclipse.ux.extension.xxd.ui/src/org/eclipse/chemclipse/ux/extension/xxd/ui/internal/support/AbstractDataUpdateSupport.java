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
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractDataUpdateSupport extends AbstractUpdateSupport implements IDataUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractDataUpdateSupport.class);
	//
	private static Object object;
	//
	private IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
	private List<EventHandler> registeredEventHandler;

	public AbstractDataUpdateSupport(MPart part) {
		super(part);
		registeredEventHandler = new ArrayList<EventHandler>();
		registerEventBroker(eventBroker);
	}

	@Override
	public Object getObject() {

		return object;
	}

	@PreDestroy
	private void preDestroy() {

		if(eventBroker != null) {
			for(EventHandler eventHandler : registeredEventHandler) {
				eventBroker.unsubscribe(eventHandler);
			}
		}
	}

	private void registerEventBroker(IEventBroker eventBroker) {

		if(eventBroker != null) {
			/*
			 * TODO optimize to TOPIC_CHROMATOGRAM_XXD_LOAD_CHROMATOGRAM_SELECTION
			 */
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION_XXD, IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_LOAD_CHROMATOGRAM_SELECTION));
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION_XXD, IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_CHROMATOGRAM_SELECTION));
			//
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.PROPERTY_SELECTED_SCAN, IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION));
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.PROPERTY_SELECTED_SCAN, IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION));
			//
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.PROPERTY_SELECTED_PEAK, IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION));
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.PROPERTY_SELECTED_PEAK, IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION));
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			public void handleEvent(Event event) {

				try {
					if(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION.equals(topic) || IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION.equals(topic) || IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_CHROMATOGRAM_SELECTION.equals(topic)) {
						setObject(null);
					} else {
						Object object = event.getProperty(property);
						if(object instanceof IChromatogramSelection) {
							/*
							 * TargetsPart etc. handle the chromatogram instead of the selection.
							 */
							IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
							object = chromatogramSelection.getChromatogram();
						}
						setObject(object);
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	public void setObject(Object myObject) {

		object = myObject;
		updateObject(object);
	}
}
