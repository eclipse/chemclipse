/*******************************************************************************
 * Copyright (c) 2012, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.definitions.ChromatogramType;
import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractSelectionView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractChromatogramSelectionMSDView extends AbstractSelectionView implements IChromatogramSelectionMSDView {

	private IChromatogramSelectionMSD chromatogramSelection;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	public AbstractChromatogramSelectionMSDView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService);
		this.eventBroker = eventBroker;
		subscribe();
	}

	@Override
	public IChromatogramSelectionMSD getChromatogramSelection() {

		if(chromatogramSelection == null) {
			chromatogramSelection = ChromatogramType.getChromatogramSelectionMSD();
		}
		return chromatogramSelection;
	}

	@Override
	public void setChromatogramSelection(IChromatogramSelectionMSD chromatogramSelection) {

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

					chromatogramSelection = (IChromatogramSelectionMSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					update(chromatogramSelection, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandler);
		}
	}

	@Override
	public void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	@Override
	public boolean doUpdate(IChromatogramSelection chromatogramSelection) {

		return super.doUpdate(chromatogramSelection);
	}
}
