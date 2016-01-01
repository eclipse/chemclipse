/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.notifier.IMassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.MassSpectrumTargetsContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.views.AbstractTargetsView;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class MassSpectrumTargetsView extends AbstractTargetsView implements IMassSpectrumSelectionUpdateNotifier {

	@Inject
	private Composite parent;
	@Inject
	private EPartService partService;
	@Inject
	private MPart part;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;
	//
	private IScanMSD massSpectrum;

	@Inject
	public MassSpectrumTargetsView(IEventBroker eventBroker) {
		super(new MassSpectrumTargetsContentProvider(), eventBroker);
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
		update(massSpectrum, false);
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

	/**
	 * Unsubscribes the selection update events.
	 */
	private void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	private boolean doUpdate(IScanMSD massSpectrum) {

		if(isPartVisible() && massSpectrum != null) {
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

	@Override
	public void update(IScanMSD massSpectrum, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(massSpectrum)) {
			super.update(massSpectrum, forceReload);
		}
	}
}
