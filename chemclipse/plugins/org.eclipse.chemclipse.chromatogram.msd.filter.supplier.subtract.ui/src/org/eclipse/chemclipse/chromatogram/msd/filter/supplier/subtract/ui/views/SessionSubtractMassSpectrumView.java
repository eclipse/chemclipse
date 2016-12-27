/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.internal.support.ISubtractFilterEvents;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.SimpleMassSpectrumUI;

public class SessionSubtractMassSpectrumView {

	@Inject
	private Composite parent;
	private SimpleMassSpectrumUI simpleMassSpectrumUI;
	@Inject
	private EPartService partService;
	@Inject
	private MPart part;
	@Inject
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		simpleMassSpectrumUI = new SimpleMassSpectrumUI(parent, SWT.NONE, MassValueDisplayPrecision.NOMINAL);
		subscribe();
		update();
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		simpleMassSpectrumUI.setFocus();
	}

	public void update() {

		IScanMSD massSpectrum = PreferenceSupplier.getSessionSubtractMassSpectrum();
		if(massSpectrum == null) {
			simpleMassSpectrumUI.clear();
		} else {
			simpleMassSpectrumUI.update(massSpectrum, true);
		}
	}

	public boolean doUpdate() {

		if(isPartVisible()) {
			return true;
		}
		return false;
	}

	public boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
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
			 * Receives and handles session subtract mass spectrum updates.
			 */
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					update();
				}
			};
			eventBroker.subscribe(ISubtractFilterEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, eventHandler);
		}
	}

	public void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}
}
