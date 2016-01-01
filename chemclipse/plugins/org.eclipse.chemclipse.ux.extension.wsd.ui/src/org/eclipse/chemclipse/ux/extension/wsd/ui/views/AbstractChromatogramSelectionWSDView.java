/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.views;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractSelectionView;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

public abstract class AbstractChromatogramSelectionWSDView extends AbstractSelectionView implements IChromatogramSelectionWSDView {

	private IChromatogramSelectionWSD chromatogramSelection;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	public AbstractChromatogramSelectionWSDView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService);
		this.eventBroker = eventBroker;
		subscribe();
	}

	@Override
	public IChromatogramSelectionWSD getChromatogramSelection() {

		return chromatogramSelection;
	}

	@Override
	public void setChromatogramSelection(IChromatogramSelectionWSD chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
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

					chromatogramSelection = (IChromatogramSelectionWSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					update(chromatogramSelection, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandler);
		}
	}

	@Override
	public void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}
}
