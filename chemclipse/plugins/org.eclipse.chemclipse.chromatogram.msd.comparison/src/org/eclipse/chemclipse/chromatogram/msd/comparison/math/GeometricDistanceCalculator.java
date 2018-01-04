/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.math;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IIonRange;

public class GeometricDistanceCalculator implements IMatchCalculator {

	@Override
	public float calculate(IScanMSD unknown, IScanMSD reference) {

		List<Integer> ionList = getIonList(unknown);
		double sqrtSquaredIntensitiesU = calculateSqrtSumSquaredIntensities(unknown, ionList);
		double sqrtSquaredIntensitiesR = calculateSqrtSumSquaredIntensities(reference, ionList);
		/*
		 * If at least one mass spectrum has no match, return 0.
		 */
		if(sqrtSquaredIntensitiesU == 0 || sqrtSquaredIntensitiesR == 0) {
			return 0;
		} else {
			/*
			 * Do a direct forward match.
			 */
			IExtractedIonSignal extractedIonSignalU = unknown.getExtractedIonSignal();
			IExtractedIonSignal extractedIonSignalR = reference.getExtractedIonSignal();
			return calculate(sqrtSquaredIntensitiesU, sqrtSquaredIntensitiesR, extractedIonSignalU, extractedIonSignalR, ionList);
		}
	}

	@Override
	public float calculate(IScanMSD unknown, IScanMSD reference, IIonRange ionRange) {

		List<Integer> ionList = getIonList(ionRange);
		double sqrtSquaredIntensitiesU = calculateSqrtSumSquaredIntensities(unknown, ionList);
		double sqrtSquaredIntensitiesR = calculateSqrtSumSquaredIntensities(reference, ionList);
		/*
		 * If at least one mass spectrum has no match, return 0.
		 */
		if(sqrtSquaredIntensitiesU == 0 || sqrtSquaredIntensitiesR == 0) {
			return 0;
		} else {
			/*
			 * Match the complete range.
			 */
			int startIon = ionRange.getStartIon();
			int stopIon = ionRange.getStopIon();
			IExtractedIonSignal extractedIonSignalU = unknown.getExtractedIonSignal(startIon, stopIon);
			IExtractedIonSignal extractedIonSignalR = reference.getExtractedIonSignal(startIon, stopIon);
			return calculate(sqrtSquaredIntensitiesU, sqrtSquaredIntensitiesR, extractedIonSignalU, extractedIonSignalR, ionList);
		}
	}

	private float calculate(double sqrtSquaredIntensitiesU, double sqrtSquaredIntensitiesR, IExtractedIonSignal extractedIonSignalU, IExtractedIonSignal extractedIonSignalR, List<Integer> ionList) {

		/*
		 * Calculate the distance.
		 */
		double sumDistance = 0.0d;
		for(int ion : ionList) {
			double uValue = (extractedIonSignalU.getAbundance(ion) / sqrtSquaredIntensitiesU);
			double rValue = (extractedIonSignalR.getAbundance(ion) / sqrtSquaredIntensitiesR);
			sumDistance += Math.pow(uValue - rValue, 2);
		}
		sumDistance += 1;
		return (float)Math.pow(sumDistance, -1);
	}

	/**
	 * SUM ui^2 or SUM si^2
	 * 
	 * @param massSpectrum
	 * @param ionRange
	 * @return double
	 */
	private double calculateSumSquaredIntensities(IScanMSD massSpectrum, List<Integer> ions) {

		double normalizedLength = 0.0d;
		IExtractedIonSignal signal = massSpectrum.getExtractedIonSignal();
		for(int ion : ions) {
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
	private double calculateSqrtSumSquaredIntensities(IScanMSD massSpectrum, List<Integer> ions) {

		return Math.sqrt(calculateSumSquaredIntensities(massSpectrum, ions));
	}

	private List<Integer> getIonList(IIonRange ionRange) {

		List<Integer> ionList = new ArrayList<Integer>();
		//
		int startIon = ionRange.getStartIon();
		int stopIon = ionRange.getStopIon();
		for(int ion = startIon; ion <= stopIon; ion++) {
			ionList.add(ion);
		}
		//
		return ionList;
	}

	private List<Integer> getIonList(IScanMSD massSpectrum) {

		List<Integer> ionList = new ArrayList<Integer>();
		//
		IExtractedIonSignal signal = massSpectrum.getExtractedIonSignal();
		int startIon = signal.getStartIon();
		int stopIon = signal.getStopIon();
		for(int ion = startIon; ion <= stopIon; ion++) {
			if(signal.getAbundance(ion) > 0.0f) {
				ionList.add(ion);
			}
		}
		//
		return ionList;
	}
}
