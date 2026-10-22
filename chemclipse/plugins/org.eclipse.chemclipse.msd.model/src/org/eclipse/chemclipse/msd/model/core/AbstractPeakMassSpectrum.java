/*******************************************************************************
 * Copyright (c) 2008, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.List;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.msd.model.implementation.PeakIon;
import org.eclipse.chemclipse.msd.model.implementation.PeakIonMSn;

public abstract class AbstractPeakMassSpectrum extends AbstractRegularMassSpectrum implements IPeakMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -7779014133066855210L;

	protected AbstractPeakMassSpectrum() {

	}

	/**
	 * Creates a new instance of a peak mass spectrum shifted to the given
	 * intensity relative to the intensity of peakMassSpectrum. The given
	 * intensity must be intensity &gt;= 0 and intensity &lt;=
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
		IPeakIon peakIon;
		float abundance;
		/*
		 * Cast to double to get a higher precision.
		 */
		double intensityFactor = (double)intensity / IPeakIntensityValues.MAX_INTENSITY;
		List<IIon> ions = peakMassSpectrum.getIons();
		for(IIon ion : ions) {
			abundance = (float)(ion.getAbundance() * intensityFactor);
			peakIon = createPeakIon(ion, abundance);
			addIon(peakIon);
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
	 * 97.2% actual: 6514141.6f → 100% : 6701791.77f<br/>
	 * 100% actual: 6514141.6f → 100% : 6514141.6f<br/>
	 * 120% actual: 6514141.6f → 100% : 5428451.333f<br/>
	 * 50% actual: 6514141.6f → 100% : 13028283.2f<br/>
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
		IPeakIon peakIon;
		float abundance;
		List<IIon> ions = massSpectrum.getIons();
		for(IIon ion : ions) {
			abundance = (ion.getAbundance() / actualPercentageIntensity) * 100.0f;
			peakIon = createPeakIon(ion, abundance);
			addIon(peakIon, false);
		}
	}

	protected AbstractPeakMassSpectrum(IScanMSD massSpectrum) throws IllegalArgumentException {

		if(massSpectrum == null) {
			throw new IllegalArgumentException("The massSpectrum must not be null.");
		}
		/*
		 * Add the ions.
		 */
		IPeakIon peakIon;
		List<IIon> ions = massSpectrum.getIons();
		for(IIon ion : ions) {
			peakIon = createPeakIon(ion, ion.getAbundance());
			addIon(peakIon);
		}
	}

	/**
	 * Creates a peak ion. The ion transition is kept in case of tandem MS data.
	 *
	 * @param ion
	 * @param abundance
	 * @return {@link IPeakIon}
	 */
	protected static IPeakIon createPeakIon(IIon ion, float abundance) {

		if(ion instanceof IIonMSn ionMSn && ionMSn.getIonTransition() != null) {
			return new PeakIonMSn(ion.getIon(), abundance, ionMSn.getIonTransition());
		} else {
			return new PeakIon(ion.getIon(), abundance);
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
