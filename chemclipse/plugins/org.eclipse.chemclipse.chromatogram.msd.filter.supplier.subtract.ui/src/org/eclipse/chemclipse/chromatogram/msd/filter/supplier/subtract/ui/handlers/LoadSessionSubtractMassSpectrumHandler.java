/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.support.ISubtractFilterEvents;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class LoadSessionSubtractMassSpectrumHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part, IEventBroker eventBroker) {

		/*
		 * Loads stored mass spectrum.
		 */
		IScanMSD normalizedMassSpectrum = PreferenceSupplier.getSubtractMassSpectrum();
		PreferenceSupplier.setSessionSubtractMassSpectrum(normalizedMassSpectrum);
		/*
		 * Update all listeners
		 */
		eventBroker.send(ISubtractFilterEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
	}
}
