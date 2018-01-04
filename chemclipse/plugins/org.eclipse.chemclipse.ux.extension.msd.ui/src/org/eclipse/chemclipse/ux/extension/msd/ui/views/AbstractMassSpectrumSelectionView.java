/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.ui.definitions.ScanType;
import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractSelectionView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractMassSpectrumSelectionView extends AbstractSelectionView implements IMassSpectrumSelectionView {

	private IScanMSD massSpectrum;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	public AbstractMassSpectrumSelectionView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService);
		this.eventBroker = eventBroker;
		subscribe();
	}

	@Override
	public IScanMSD getMassSpectrum() {

		if(massSpectrum == null) {
			massSpectrum = ScanType.getSelectedScanMSD();
		}
		return massSpectrum;
	}

	@Override
	public void setMassSpectrum(IScanMSD massSpectrum) {

		this.massSpectrum = massSpectrum;
	}

	@Override
	public boolean doUpdate(IScanMSD massSpectrum) {

		if(isPartVisible() && massSpectrum != null) {
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

					massSpectrum = (IScanMSD)event.getProperty(IChemClipseEvents.PROPERTY_MASSPECTRUM);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					update(massSpectrum, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_MASSSPECTRUM, eventHandler);
		}
	}

	@Override
	public void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}
}
