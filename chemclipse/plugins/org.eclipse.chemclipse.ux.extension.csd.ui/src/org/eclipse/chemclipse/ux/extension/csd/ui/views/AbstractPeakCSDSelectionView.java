/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.definitions.PeakType;
import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractSelectionView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractPeakCSDSelectionView extends AbstractSelectionView implements IPeakCSDSelectionView {

	private IPeakCSD peak;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	public AbstractPeakCSDSelectionView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService);
		this.eventBroker = eventBroker;
		subscribe();
	}

	@Override
	public IPeakCSD getPeak() {

		if(peak == null) {
			peak = PeakType.getSelectedPeakCSD();
		}
		return peak;
	}

	@Override
	public void setPeak(IPeakCSD peak) {

		this.peak = peak;
	}

	@Override
	public boolean doUpdate(IPeakCSD peak) {

		if(isPartVisible() && peak != null) {
			return true;
		}
		return false;
	}

	/**
	 * Subscribes the selection update events.
	 */
	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram selection updates.
			 */
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					peak = (IPeakCSD)event.getProperty(IChemClipseEvents.PROPERTY_PEAK_CSD);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					update(peak, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_PEAK, eventHandler);
		}
	}

	@Override
	public void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}
}
