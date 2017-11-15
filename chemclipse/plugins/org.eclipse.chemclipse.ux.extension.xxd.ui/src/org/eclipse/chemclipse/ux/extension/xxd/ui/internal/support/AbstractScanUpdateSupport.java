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

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractScanUpdateSupport extends AbstractUpdateSupport implements IScanUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractScanUpdateSupport.class);
	//
	private static IScan scan;
	//
	private IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
	private List<EventHandler> registeredEventHandler;

	public AbstractScanUpdateSupport(MPart part) {
		super(part);
		registeredEventHandler = new ArrayList<EventHandler>();
		registerEventBroker(eventBroker);
	}

	@Override
	public IScan getScan() {

		return scan;
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
			registeredEventHandler.add(registerScanHandler(eventBroker, IChemClipseEvents.PROPERTY_SELECTED_SCAN, IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION));
			registeredEventHandler.add(registerScanHandler(eventBroker, IChemClipseEvents.PROPERTY_SELECTED_SCAN, IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION));
			registeredEventHandler.add(registerEventHandlerFile(eventBroker, IChemClipseEvents.PROPERTY_SELECTED_PEAK, IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION));
			registeredEventHandler.add(registerEventHandlerFile(eventBroker, IChemClipseEvents.PROPERTY_SELECTED_PEAK, IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION));
		}
	}

	private EventHandler registerScanHandler(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			public void handleEvent(Event event) {

				try {
					if(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION.equals(topic)) {
						setScan(null);
					} else {
						Object object = event.getProperty(property);
						if(object instanceof IScan) {
							setScan((IScan)object);
						} else {
							setScan(null);
						}
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private EventHandler registerEventHandlerFile(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			public void handleEvent(Event event) {

				try {
					if(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION.equals(topic)) {
						setScan(null);
					} else {
						Object object = event.getProperty(property);
						IScan scan = null;
						if(object instanceof IPeakMSD) {
							IPeakMSD peakMSD = (IPeakMSD)object;
							scan = peakMSD.getPeakModel().getPeakMaximum();
						} else if(object instanceof IPeakCSD) {
							IPeakCSD peakCSD = (IPeakCSD)object;
							scan = peakCSD.getPeakModel().getPeakMaximum();
						} else if(object instanceof IPeakWSD) {
							IPeakWSD peakWSD = (IPeakWSD)object;
							scan = peakWSD.getPeakModel().getPeakMaximum();
						}
						//
						setScan(scan);
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	public void setScan(IScan selectedScan) {

		scan = selectedScan;
		updateScan(selectedScan);
	}
}
