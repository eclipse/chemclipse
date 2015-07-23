/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumListUI;
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

public class MassSpectraIdentifiedList extends AbstractChromatogramSelectionMSDView {

	@Inject
	private Composite parent;
	private MassSpectrumListUI massSpectrumListUI;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	@Inject
	public MassSpectraIdentifiedList(MPart part, EPartService partService, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
		/*
		 * Receives and handles chromatogram selection updates.
		 */
		this.eventBroker = eventBroker;
		if(eventBroker != null) {
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					update(getChromatogramSelection(), true);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_MASSSPECTRUM, eventHandler);
		}
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		massSpectrumListUI = new MassSpectrumListUI(parent, SWT.NONE);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		massSpectrumListUI.setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void unsubscribe() {

		super.unsubscribe();
		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			IMassSpectra massSpectra = new MassSpectra();
			List<IScan> scans = chromatogramSelection.getChromatogram().getScans();
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			//
			for(IScan scan : scans) {
				if(scan instanceof IVendorMassSpectrum) {
					IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)scan;
					if(massSpectrum.getTargets().size() > 0) {
						int retentionTime = massSpectrum.getRetentionTime();
						if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
							/*
							 * Enforce to load the scan proxy if it is used.
							 */
							massSpectrum.enforceLoadScanProxy();
							massSpectra.addMassSpectrum(massSpectrum);
						}
					}
				}
			}
			//
			massSpectrumListUI.update(massSpectra, forceReload);
		}
	}
}
