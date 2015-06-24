/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

/**
 * More informations about the class structure of mass spectra are stored in {@link IScanMSD}.
 * 
 * @author eselmeister
 */
public interface IRegularMassSpectrum extends IScanMSD {

	/**
	 * Returns the mass spectrometer number that recorded the mass spectrum.
	 * 1 = MS1
	 * 2 = MS2
	 * 3 = ...
	 * 
	 * @return short
	 */
	short getMassSpectrometer();

	/**
	 * Sets the mass spectrometer (MS1, MS2, ...).
	 * 
	 * @param massSpectrometer
	 */
	void setMassSpectrometer(short massSpectrometer);

	/**
	 * Returns the mass spectrum type.
	 * 0 = centroid
	 * 1 = profile
	 * 
	 * @return {@link MassSpectrumType}
	 */
	short getMassSpectrumType();

	/**
	 * Returns a descriptions of the stored type.
	 * 
	 * @return String
	 */
	String getMassSpectrumTypeDescription();

	/**
	 * Sets the mass spectrum type (centroid = 0, profile = 1).
	 * 
	 * @param short
	 */
	void setMassSpectrumType(short massSpectrumType);

	/**
	 * Returns the precursor ion.
	 * If none has been selected, 0 will be returned.
	 * 
	 * @return double
	 */
	double getPrecursorIon();

	/**
	 * Sets the precursor ion.
	 * 
	 * @param precursorIon
	 */
	void setPrecursorIon(double precursorIon);
}
