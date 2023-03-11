/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.PeakIon;

/**
 * @author eselmeister
 */
public abstract class AbstractPeakMassSpectrum extends AbstractRegularMassSpectrum implements IPeakMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -7779014133066855210L;
	private static final Logger logger = Logger.getLogger(AbstractPeakMassSpectrum.class);

	protected AbstractPeakMassSpectrum() {

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
	protected AbstractPeakMassSpectrum(IPeakMassSpectrum peakMassSpectrum, float intensity) throws IllegalArgumentException {

		if(peakMassSpectrum == null) {
			throw new IllegalArgumentException("The peakMassSpectrum must not be null");
		}
		if(intensity < 0 || intensity > IPeakIntensityValues.MAX_INTENSITY) {
			throw new IllegalArgumentException("The intensity must be >= 0 and <= IPeakIntensityValues.MAX_INTENSITY");
		}
		/*
		 * Add the ions.
		 */
		PeakIon peakIon;
		float abundance;
		/*
		 * Cast to double to get a higher precision.
		 */
		double intensityFactor = (double)intensity / IPeakIntensityValues.MAX_INTENSITY;
		List<IIon> ions = peakMassSpectrum.getIons();
		for(IIon ion : ions) {
			try {
				abundance = (float)(ion.getAbundance() * intensityFactor);
				peakIon = new PeakIon(ion.getIon(), abundance);
				addIon(peakIon);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * The value "actualPercentageIntensity" describes the intensity of the
	 * actual mass spectrum relative to the mass spectrum that will be
	 * generated.<br/>
	 * If the actualpercentageIntensity is 50.0f the resulting mass spectrum
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
	protected AbstractPeakMassSpectrum(IScanMSD massSpectrum, float actualPercentageIntensity) throws IllegalArgumentException {

		if(massSpectrum == null) {
			throw new IllegalArgumentException("The massSpectrum must not be null");
		}
		if(actualPercentageIntensity <= 0.0f) {
			throw new IllegalArgumentException("The percentageIntensity must not be > 0.");
		}
		/*
		 * Add the ions.
		 */
		PeakIon peakIon;
		float abundance;
		List<IIon> ions = massSpectrum.getIons();
		for(IIon ion : ions) {
			try {
				abundance = (ion.getAbundance() / actualPercentageIntensity) * 100.0f;
				peakIon = new PeakIon(ion.getIon(), abundance);
				addIon(peakIon);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
	}

	protected AbstractPeakMassSpectrum(IScanMSD massSpectrum) throws IllegalArgumentException {

		if(massSpectrum == null) {
			throw new IllegalArgumentException("The massSpectrum must not be null");
		}
		/*
		 * Add the ions.
		 */
		PeakIon peakIon;
		List<IIon> ions = massSpectrum.getIons();
		for(IIon ion : ions) {
			try {
				peakIon = new PeakIon(ion);
				addIon(peakIon);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		}
	}

	// -------------------------------------------------------
	@Override
	public void addIon(IPeakIon peakIon, boolean checked) {

		super.addIon(peakIon, checked);
	}

	@Override
	public void addIon(IPeakIon peakIon) {

		super.addIon(peakIon);
	}
	// -------------------------------------------------------
}
