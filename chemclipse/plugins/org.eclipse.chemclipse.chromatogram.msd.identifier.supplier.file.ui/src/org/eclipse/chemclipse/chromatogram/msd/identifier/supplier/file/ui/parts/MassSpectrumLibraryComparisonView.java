/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.LibraryMassSpectrumComparisonUI;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class MassSpectrumLibraryComparisonView {

	@Inject
	private Composite parent;
	@Inject
	private EPartService partService;
	@Inject
	private MPart part;
	private IEventBroker eventBroker;
	//
	private EventHandler eventHandlerName;
	private EventHandler eventHandlerMassSpectrum;
	//
	private LibraryMassSpectrumComparisonUI libraryMassSpectrumComparisonUI;
	//
	private String name = "";
	private String casNumber = "";
	private IScanMSD massSpectrum;

	@Inject
	public MassSpectrumLibraryComparisonView(IEventBroker eventBroker) {
		this.eventBroker = eventBroker;
		subscribe();
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		libraryMassSpectrumComparisonUI = new LibraryMassSpectrumComparisonUI(parent, SWT.NONE, MassValueDisplayPrecision.NOMINAL);
	}

	@Focus
	public void setFocus() {

		libraryMassSpectrumComparisonUI.setFocus();
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	private boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
			return true;
		}
		return false;
	}

	private void unsubscribe() {

		if(eventBroker != null) {
			if(eventHandlerName != null) {
				eventBroker.unsubscribe(eventHandlerName);
			}
		}
	}

	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Name
			 */
			eventHandlerName = new EventHandler() {

				public void handleEvent(Event event) {

					/*
					 * Receive name and formula.
					 */
					name = (String)event.getProperty(IChemClipseEvents.PROPERTY_IDENTIFICATION_ENTRY_NAME);
					casNumber = (String)event.getProperty(IChemClipseEvents.PROPERTY_IDENTIFICATION_ENTRY_CAS_NUMBER);
					update();
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_IDENTIFICATION_ENTRY_UPDATE_CDK, eventHandlerName);
			/*
			 * Mass Spectrum
			 */
			eventHandlerMassSpectrum = new EventHandler() {

				@Override
				public void handleEvent(Event event) {

					massSpectrum = (IScanMSD)event.getProperty(IChemClipseEvents.PROPERTY_MASSPECTRUM);
					name = "";
					casNumber = "";
					update();
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_MASSSPECTRUM, eventHandlerMassSpectrum);
		}
	}

	private void update() {

		if(isPartVisible()) {
			libraryMassSpectrumComparisonUI.update(massSpectrum, massSpectrum, true);
		}
	}
}