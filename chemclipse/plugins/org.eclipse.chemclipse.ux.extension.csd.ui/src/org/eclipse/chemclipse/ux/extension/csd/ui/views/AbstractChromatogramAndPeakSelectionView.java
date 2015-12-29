/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractSelectionView;

public abstract class AbstractChromatogramAndPeakSelectionView extends AbstractSelectionView implements IChromatogramAndPeakSelectionView {

	private IChromatogramSelectionCSD chromatogramSelection;
	private IChromatogramPeakCSD chromatogramPeak;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	public AbstractChromatogramAndPeakSelectionView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService);
		this.eventBroker = eventBroker;
		subscribe();
	}

	@Override
	public IChromatogramSelectionCSD getChromatogramSelection() {

		return chromatogramSelection;
	}

	@Override
	public void setChromatogramSelection(IChromatogramSelectionCSD chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public IChromatogramPeakCSD getChromatogramPeak() {

		return chromatogramPeak;
	}

	@Override
	public void setChromatogramPeak(IChromatogramPeakCSD chromatogramPeak) {

		this.chromatogramPeak = chromatogramPeak;
	}

	@Override
	public boolean doUpdate(IChromatogramSelectionCSD chromatogramSelection, IChromatogramPeakCSD chromatogramPeak) {

		if(isPartVisible() && chromatogramSelection != null && chromatogramPeak != null) {
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

					chromatogramSelection = (IChromatogramSelectionCSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
					chromatogramPeak = (IChromatogramPeakCSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_PEAK_CSD);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					update(chromatogramSelection, chromatogramPeak, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_AND_PEAK_SELECTION, eventHandler);
		}
	}

	@Override
	public void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}
}
