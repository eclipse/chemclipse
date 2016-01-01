/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.csd.ui.internal.provider.PeakTargetsContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.views.AbstractTargetsView;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class PeakTargetsView extends AbstractTargetsView {

	@Inject
	private Composite parent;
	@Inject
	private EPartService partService;
	@Inject
	private MPart part;
	private IEventBroker eventBroker;
	private EventHandler eventHandlerChromatogramSelection;
	private EventHandler eventHandlerPeak;
	//
	private IPeakCSD peak;

	@Inject
	public PeakTargetsView(IEventBroker eventBroker) {
		super(new PeakTargetsContentProvider(), eventBroker);
		this.eventBroker = eventBroker;
	}

	@PostConstruct
	private void createControl() {

		super.createPartControl(parent);
		subscribe();
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		super.setFocus();
		updatePeakTargets(peak, false);
	}

	/**
	 * Subscribes the selection update events.
	 */
	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram selection updates.
			 */
			eventHandlerChromatogramSelection = new EventHandler() {

				public void handleEvent(Event event) {

					IChromatogramSelectionCSD chromatogramSelection = (IChromatogramSelectionCSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					if(chromatogramSelection != null) {
						peak = chromatogramSelection.getSelectedPeak();
						updatePeakTargets(peak, forceReload);
					}
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandlerChromatogramSelection);
			/*
			 * Receives and handles peak updates.
			 */
			eventHandlerPeak = new EventHandler() {

				@Override
				public void handleEvent(Event event) {

					peak = (IPeakCSD)event.getProperty(IChemClipseEvents.PROPERTY_PEAK_CSD);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					updatePeakTargets(peak, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_PEAK, eventHandlerPeak);
		}
	}

	/**
	 * Unsubscribes the selection update events.
	 */
	private void unsubscribe() {

		if(eventBroker != null) {
			if(eventHandlerChromatogramSelection != null) {
				eventBroker.unsubscribe(eventHandlerChromatogramSelection);
			}
			if(eventHandlerPeak != null) {
				eventBroker.unsubscribe(eventHandlerPeak);
			}
		}
	}

	private boolean doUpdate(IPeakCSD peak) {

		if(isPartVisible() && peak != null) {
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

	private void updatePeakTargets(IPeakCSD peak, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(peak)) {
			super.update(peak, forceReload);
		}
	}
}
