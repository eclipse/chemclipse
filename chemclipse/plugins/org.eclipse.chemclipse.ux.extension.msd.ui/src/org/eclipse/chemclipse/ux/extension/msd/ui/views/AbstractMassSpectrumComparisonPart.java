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
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractMassSpectrumComparisonPart {

	private EPartService partService;
	private MPart part;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;
	//
	private IScanMSD referenceMassSpectrum;
	private IScanMSD comparisonMassSpectrum;

	public AbstractMassSpectrumComparisonPart(EPartService partService, MPart part, IEventBroker eventBroker, EventHandler eventHandler) {
		this.partService = partService;
		this.part = part;
		this.eventBroker = eventBroker;
		this.eventHandler = eventHandler;
	}

	public IScanMSD getReferenceMassSpectrum() {

		return referenceMassSpectrum;
	}

	public IScanMSD getComparisonMassSpectrum() {

		return comparisonMassSpectrum;
	}

	/**
	 * Subscribes the selection update events.
	 */
	protected void subscribe() {

		if(eventBroker != null) {
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					referenceMassSpectrum = (IScanMSD)event.getProperty(IChemClipseEvents.PROPERTY_REFERENCE_MS);
					comparisonMassSpectrum = (IScanMSD)event.getProperty(IChemClipseEvents.PROPERTY_COMPARISON_MS);
					update();
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_SCAN_MSD_UPDATE_COMPARISON, eventHandler);
		}
	}

	protected void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	protected boolean doUpdate() {

		if(isPartVisible()) {
			return true;
		}
		return false;
	}

	private boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
			return true;
		}
		return false;
	}

	public void update() {

		// Override
	}
}
