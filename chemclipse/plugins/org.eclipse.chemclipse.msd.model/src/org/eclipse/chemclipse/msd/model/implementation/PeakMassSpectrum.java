/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.AbstractPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class PeakMassSpectrum extends AbstractPeakMassSpectrum implements IPeakMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -1105097569796396530L;

	public PeakMassSpectrum() {

		super();
	}

	/**
	 * Creates a new instance of a peak mass spectrum shifted to the given
	 * intensity relative to the intensity of peakMassSpectrum. The given
	 * intensity must be intensity >= 0 and intensity <=
	 * IPeakIntensityValues.MAX_INTENSITY.
	 * 
	 * @param peakMassSpectrum
	 * @param intensity
	 */
	public PeakMassSpectrum(IPeakMassSpectrum peakMassSpectrum, float intensity) throws IllegalArgumentException {

		super(peakMassSpectrum, intensity);
	}

	/**
	 * Creates a new instance of a peak mass spectrum shifted to the given
	 * intensity relative to the intensity of peakMassSpectrum.
	 * 
	 * @param massSpectrum
	 */
	public PeakMassSpectrum(IScanMSD massSpectrum) throws IllegalArgumentException {

		super(massSpectrum);
	}

	/**
	 * The value "actualPercentageIntensity" describes the intensity of the
	 * actual mass spectrum relative to the mass spectrum that will be
	 * generated.<br/>
	 * If the actualPercentageIntensity is 50.0f the resulting mass spectrum
	 * will have an intensity of twice the actual intensity.<br/>
	 * Creates a new instance of a peak mass spectrum.<br/>
	 * The mass spectrum will be shifted to an intensity of 100 relative to the
	 * given percentage intensity.<br/>
	 * The intensity needs to be given as: 0.0f (0%), or 56.3f (56.3%) ...<br/>
	 * It is not allowed that the percentage Intensity is less or equal than 0.
	 * If for example the total intensity of massSpectrum is 57000 and a
	 * percentageIntensity of 125.0f will be provided, the total intensity of
	 * the returned peak mass spectrum will be 45600.<br/>
	 * Some examples:<br/>
	 * 97.2% actual: 6514141.6f -> 100% : 6701791.77f<br/>
	 * 100% actual: 6514141.6f -> 100% : 6514141.6f<br/>
	 * 120% actual: 6514141.6f -> 100% : 5428451.333f<br/>
	 * 50% actual: 6514141.6f -> 100% : 13028283.2f<br/>
	 * 
	 * @param massSpectrum
	 * @param actualPercentageIntensity
	 */
	public PeakMassSpectrum(IScanMSD massSpectrum, float actualPercentageIntensity) throws IllegalArgumentException {

		super(massSpectrum, actualPercentageIntensity);
	}

	// -------------------------------IMassSpectrumCloneable
	/**
	 * Keep in mind, it is a covariant return.<br/>
	 * IMassSpectrum is needed. IPeakMassSpectrum is a subtype of IMassSpectrum.
	 */
	@Override
	public IScanMSD makeDeepCopy() throws CloneNotSupportedException {

		IPeakMassSpectrum massSpectrum = (IPeakMassSpectrum)super.clone();
		IPeakIon peakIon;
		/*
		 * The instance variables have been copied by super.clone();.<br/> The
		 * ions in the ion list need not to be removed via
		 * removeAllIons as the method super.clone() has created a new
		 * list.<br/> It is necessary to fill the list again, as the abstract
		 * super class does not know each available type of ion.<br/>
		 * Make a deep copy of all ions.
		 */
		for(IIon ion : getIons()) {
			peakIon = new PeakIon(ion.getIon(), ion.getAbundance());
			massSpectrum.addIon(peakIon);
		}
		return massSpectrum;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		return makeDeepCopy();
	}
	// -------------------------------IMassSpectrumCloneable
}
