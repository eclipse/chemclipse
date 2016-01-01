/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.math;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IIonRange;

public class GeometricDistanceCalculator implements IMatchCalculator {

	/**
	 * Returns the geometric distance match quality.<br/>
	 * 0 : no match
	 * 1 : best match <br/>
	 * A normalization of the unknown and reference mass spectrum is not
	 * required.<br/>
	 * See
	 * "Alfassi, Z. B., Vector analysis of multi-measurements identification", 2004
	 * Equation (3).
	 */
	@Override
	public float calculate(IScanMSD unknown, IScanMSD reference, IIonRange ionRange) {

		int startIon = ionRange.getStartIon();
		int stopIon = ionRange.getStopIon();
		double sqrtSquaredIntensitiesU = calculateSqrtSumSquaredIntensities(unknown, ionRange);
		double sqrtSquaredIntensitiesR = calculateSqrtSumSquaredIntensities(reference, ionRange);
		/*
		 * If at least one mass spectrum has no match, return 0.
		 */
		if(sqrtSquaredIntensitiesU == 0 || sqrtSquaredIntensitiesR == 0) {
			return 0;
		} else {
			IExtractedIonSignal u;
			u = unknown.getExtractedIonSignal(startIon, stopIon);
			IExtractedIonSignal r;
			r = reference.getExtractedIonSignal(startIon, stopIon);
			//
			double sumDistance = 0.0d;
			//
			for(int ion = startIon; ion <= stopIon; ion++) {
				double uValue = (u.getAbundance(ion) / sqrtSquaredIntensitiesU);
				double rValue = (r.getAbundance(ion) / sqrtSquaredIntensitiesR);
				sumDistance += Math.pow(uValue - rValue, 2);
			}
			sumDistance += 1;
			return (float)Math.pow(sumDistance, -1);
		}
	}

	/**
	 * SUM ui^2 or SUM si^2
	 * 
	 * @param massSpectrum
	 * @param ionRange
	 * @return double
	 */
	private double calculateSumSquaredIntensities(IScanMSD massSpectrum, IIonRange ionRange) {

		double normalizedLength = 0.0d;
		int startIon = ionRange.getStartIon();
		int stopIon = ionRange.getStopIon();
		IExtractedIonSignal signal = massSpectrum.getExtractedIonSignal(startIon, stopIon);
		for(int ion = startIon; ion <= stopIon; ion++) {
			normalizedLength += Math.pow(signal.getAbundance(ion), 2);
		}
		return normalizedLength;
	}

	/**
	 * SQRT SUM ui^2 or SQRT Sum si^2
	 * 
	 * @param massSpectrum
	 * @param ionRange
	 * @return
	 */
	private double calculateSqrtSumSquaredIntensities(IScanMSD massSpectrum, IIonRange ionRange) {

		return Math.sqrt(calculateSumSquaredIntensities(massSpectrum, ionRange));
	}
}
