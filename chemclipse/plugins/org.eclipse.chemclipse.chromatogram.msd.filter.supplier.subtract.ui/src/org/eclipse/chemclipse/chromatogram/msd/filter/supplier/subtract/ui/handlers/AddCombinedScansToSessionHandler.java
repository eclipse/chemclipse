/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.handlers;

import javax.inject.Named;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.support.FilterSupport;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class AddCombinedScansToSessionHandler implements EventHandler {

	private static IChromatogramSelectionMSD chromatogramSelection;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part, IEventBroker eventBroker) {

		/*
		 * Run the handler.
		 */
		if(chromatogramSelection != null) {
			boolean useNormalize = org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan();
			IScanMSD massSpectrum1 = PreferenceSupplier.getSessionSubtractMassSpectrum();
			IScanMSD massSpectrum2 = FilterSupport.getCombinedMassSpectrum(chromatogramSelection, null, useNormalize);
			IScanMSD normalizedMassSpectrum = FilterSupport.getCombinedMassSpectrum(massSpectrum1, massSpectrum2, null, useNormalize);
			PreferenceSupplier.setSessionSubtractMassSpectrum(normalizedMassSpectrum);
			/*
			 * Update all listeners
			 */
			eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
		}
	}

	@Override
	public void handleEvent(Event event) {

		if(event.getTopic().equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionMSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		}
	}
}
