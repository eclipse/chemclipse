/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

/**
 * More informations about the class structure of mass spectra are stored in {@link IScanMSD}.
 */
public interface IPeakMassSpectrum extends IRegularMassSpectrum {

	/**
	 * Adds an {@link IPeakIon} instance to the peak mass spectrum.<br/>
	 * See description addIon in {@link IScanMSD}.
	 * 
	 * @param peakIon
	 * @param checked
	 */
	void addIon(IPeakIon peakIon, boolean checked);

	/**
	 * Adds an {@link IPeakIon} instance to the peak mass spectrum.<br/>
	 * See description addIon in {@link IScanMSD}.
	 * 
	 * @param peakIon
	 */
	void addIon(IPeakIon peakIon);
}