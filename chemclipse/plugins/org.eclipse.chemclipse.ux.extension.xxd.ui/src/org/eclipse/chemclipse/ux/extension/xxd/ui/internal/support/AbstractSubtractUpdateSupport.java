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
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractSubtractUpdateSupport extends AbstractUpdateSupport implements ISubtractUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractSubtractUpdateSupport.class);
	//
	private static IScanMSD scanMSD;
	private static IChromatogramSelectionMSD chromatogramSelectionMSD;
	//
	private IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
	private List<EventHandler> registeredEventHandler;

	public AbstractSubtractUpdateSupport(MPart part) {
		super(part);
		registeredEventHandler = new ArrayList<EventHandler>();
		registerEventBroker(eventBroker);
	}

	@Override
	public IScanMSD getScanMSD() {

		return scanMSD;
	}

	@Override
	public IChromatogramSelectionMSD getChromatogramSelectionMSD() {

		return chromatogramSelectionMSD;
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
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.PROPERTY_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM));
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION, IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION));
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			public void handleEvent(Event event) {

				try {
					if(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM.equals(topic)) {
						IScanMSD scanMSD = PreferenceSupplier.getSessionSubtractMassSpectrum();
						setScanMSD(scanMSD);
					} else if(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION.equals(topic)) {
						Object object = event.getProperty(property);
						setChromatogramSelectionMSD((IChromatogramSelectionMSD)object);
					} else {
						setChromatogramSelectionMSD(null);
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private void setScanMSD(IScanMSD myScanMSD) {

		/*
		 * Remember the selection.
		 */
		scanMSD = myScanMSD;
		/*
		 * Do an update only if the part is visible.
		 */
		if(doUpdate()) {
			updateScanMSD(scanMSD);
		}
	}

	private void setChromatogramSelectionMSD(IChromatogramSelectionMSD myChromatogramSelectionMSD) {

		/*
		 * Remember the selection.
		 */
		chromatogramSelectionMSD = myChromatogramSelectionMSD;
		/*
		 * Do an update only if the part is visible.
		 */
		if(doUpdate()) {
			updateChromatogramSelectionMSD(myChromatogramSelectionMSD);
		}
	}
}
