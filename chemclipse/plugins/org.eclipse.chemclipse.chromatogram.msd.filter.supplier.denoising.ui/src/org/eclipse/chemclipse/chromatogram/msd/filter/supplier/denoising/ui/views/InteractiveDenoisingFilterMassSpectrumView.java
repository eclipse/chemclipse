/*******************************************************************************
 * Copyright (c) 2010, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.views;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.internal.preferences.IDenoisingEvents;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.swt.NoiseMassSpectraUI;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.ux.extension.msd.ui.views.AbstractChromatogramSelectionMSDView;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class InteractiveDenoisingFilterMassSpectrumView extends AbstractChromatogramSelectionMSDView {

	private static final Logger logger = Logger.getLogger(InteractiveDenoisingFilterMassSpectrumView.class);
	@Inject
	private Composite parent;
	private NoiseMassSpectraUI noiseMassSpectraUI;

	@Inject
	public InteractiveDenoisingFilterMassSpectrumView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		subscribeToMassSpectraUpdates(eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		noiseMassSpectraUI = new NoiseMassSpectraUI(parent, SWT.NONE);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		noiseMassSpectraUI.setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			//
		}
	}

	public void update(List<ICombinedMassSpectrum> massSpectra, boolean forceReload) {

		if(isPartVisible() && massSpectra != null) {
			noiseMassSpectraUI.update(massSpectra, forceReload);
		}
	}

	private void subscribeToMassSpectraUpdates(IEventBroker eventBroker) {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram overview updates.
			 */
			EventHandler eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					Object object = event.getProperty(IDenoisingEvents.PROPERTY_NOISE_MASS_SPECTRA);
					if(object instanceof List) {
						try {
							@SuppressWarnings("unchecked")
							List<ICombinedMassSpectrum> elements = (List<ICombinedMassSpectrum>)object;
							update(elements, true);
						} catch(Exception e) {
							logger.warn(e);
						}
					}
				}
			};
			eventBroker.subscribe(IDenoisingEvents.TOPIC_NOISE_MASS_SPECTRA_UPDATE, eventHandler);
		}
	}
}
