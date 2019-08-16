/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity;

import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.ComparisonException;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IIonRange;

public abstract class AbstractMassSpectrumPurityResult implements IMassSpectrumPurityResult {

	private float fitValue = 0;
	private float reverseFitValue = 0;

	public AbstractMassSpectrumPurityResult(IScanMSD unknown, IScanMSD reference) throws ComparisonException {
		if(unknown == null) {
			throw new ComparisonException("The unknown must not be null.");
		}
		if(reference == null) {
			throw new ComparisonException("The reference must not be null.");
		}
		this.fitValue = calculateFitValue(unknown, reference);
		this.reverseFitValue = calculateReverseFitValue(unknown, reference);
	}

	@Override
	public float getFitValue() {

		return fitValue * 100; // internally it's normalized to 1, but a percentage value is used normally
	}

	@Override
	public float getReverseFitValue() {

		return reverseFitValue * 100; // internally it's normalized to 1, but a percentage value is used normally
	}

	// ----------------------------------------private methods
	/**
	 * Calculates how many ions from the unknown mass spectrum fit to
	 * the reference mass spectrum.
	 * 
	 * @param unknown
	 * @param reference
	 * @param ionRange
	 * @return float
	 */
	private float calculateFitValue(IScanMSD unknown, IScanMSD reference) {

		IIonRange ionRange = unknown.getExtractedIonSignal().getIonRange();
		IExtractedIonSignal u = unknown.getExtractedIonSignal(ionRange.getStartIon(), ionRange.getStopIon());
		IExtractedIonSignal r = reference.getExtractedIonSignal(ionRange.getStartIon(), ionRange.getStopIon());
		return calculateMatch(u, r);
	}

	/**
	 * Calculates how many ions from the reference mass spectrum fit
	 * to the unknown mass spectrum.
	 * 
	 * @param unknown
	 * @param reference
	 * @param ionRange
	 * @return float
	 */
	private float calculateReverseFitValue(IScanMSD unknown, IScanMSD reference) {

		IIonRange ionRange = reference.getExtractedIonSignal().getIonRange();
		IExtractedIonSignal u = unknown.getExtractedIonSignal(ionRange.getStartIon(), ionRange.getStopIon());
		IExtractedIonSignal r = reference.getExtractedIonSignal(ionRange.getStartIon(), ionRange.getStopIon());
		return calculateMatch(r, u);
	}

	/**
	 * Calculates the percentage amount of ions from massSpectrum1
	 * that fit in massSpectrum2.
	 * 
	 * @param massSpectrum1
	 * @param massSpectrum2
	 * @return float
	 */
	private float calculateMatch(IExtractedIonSignal massSpectrum1, IExtractedIonSignal massSpectrum2) {

		int startIon = massSpectrum1.getStartIon();
		int stopIon = massSpectrum1.getStopIon();
		float abundance = 0.0f;
		int count = 0;
		int match = 0;
		for(int ion = startIon; ion <= stopIon; ion++) {
			abundance = massSpectrum1.getAbundance(ion);
			if(abundance > 0) {
				count++;
				if(massSpectrum2.getAbundance(ion) > 0) {
					match++;
				}
			}
		}
		/*
		 * If all counts will be matched, a value of 1 (100% match factor) will
		 * be returned.<br/> If no ion will be matched, a value of 0 will be
		 * returned (0% match factor).
		 */
		if(count == 0) {
			return 0.0f;
		} else {
			return (1.0f / count) * match;
		}
	}
	// ----------------------------------------private methods
}
