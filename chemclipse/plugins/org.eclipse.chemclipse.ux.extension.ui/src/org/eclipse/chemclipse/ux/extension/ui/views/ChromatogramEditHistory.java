/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.ui.parts.EditHistoryPart;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ChromatogramEditHistory extends EditHistoryPart {

	private EPartService partService;
	private MPart part;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	@Inject
	public ChromatogramEditHistory(Composite parent, EPartService partService, MPart part, IEventBroker eventBroker, EventHandler eventHandler) {
		super(parent, partService, part, eventBroker);
		this.partService = partService;
		this.part = part;
		this.eventBroker = eventBroker;
		this.eventHandler = eventHandler;
		subscribe();
	}

	@PreDestroy
	public void preDestroy() {

		unsubscribe();
	}

	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Receives and handles MSD/CSD/WSD chromatogram selection updates.
			 */
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					IChromatogramSelection<?, ?> chromatogramSelection = (IChromatogramSelection<?, ?>)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					doUpdate(chromatogramSelection, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandler);
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandler);
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandler);
		}
	}

	private void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	private void doUpdate(IChromatogramSelection<?, ?> chromatogramSelection, boolean forceReload) {

		if(isPartVisible() && chromatogramSelection != null && chromatogramSelection.getChromatogram() != null) {
			IEditHistory editHistory = chromatogramSelection.getChromatogram().getEditHistory();
			setInput(editHistory);
		}
	}

	private boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
			return true;
		}
		return false;
	}
}
