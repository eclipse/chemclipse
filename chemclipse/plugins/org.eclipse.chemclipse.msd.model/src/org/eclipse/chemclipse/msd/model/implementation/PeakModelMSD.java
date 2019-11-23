/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add delegate constructor
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.msd.model.core.AbstractPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;

public class PeakModelMSD extends AbstractPeakModelMSD implements IPeakModelMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -1550042043393366604L;

	public PeakModelMSD(IPeakMassSpectrum peakMaximum, IPeakIntensityValues peakIntensityValues) throws IllegalArgumentException, PeakException {
		this(peakMaximum, peakIntensityValues, 0, 0);
	}

	public PeakModelMSD(IPeakMassSpectrum peakMaximum, IPeakIntensityValues peakIntensityValues, float startBackgroundAbundance, float stopBackgroundAbundance) throws IllegalArgumentException, PeakException {
		super(peakMaximum, peakIntensityValues, startBackgroundAbundance, stopBackgroundAbundance);
	}
}
