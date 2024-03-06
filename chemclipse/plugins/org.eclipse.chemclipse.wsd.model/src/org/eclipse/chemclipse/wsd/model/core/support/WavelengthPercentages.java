/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public class WavelengthPercentages {

	public static final int MAX_PERCENTAGE = 100;
	//
	private IScanWSD scanWSD;
	private SortedMap<Integer, Float> wavelengthDistribution;

	public WavelengthPercentages(IScanWSD scanWSD) {

		this.scanWSD = scanWSD;
		wavelengthDistribution = new TreeMap<Integer, Float>();
		calculateDistribution();
	}

	public IScanWSD getScanWSD() {

		return scanWSD;
	}

	public float getPercentage(int wavelength) {

		float result = 0.0f;
		if(scanIsNull()) {
			result = 0.0f;
		} else {
			if(wavelengthDistribution.containsKey(wavelength)) {
				result = wavelengthDistribution.get(wavelength);
			}
		}
		return result;
	}

	public float getPercentage(List<Integer> wavelengths) {

		float result = 0.0f;
		if(wavelengths != null) {
			for(Integer wavelength : wavelengths) {
				result += getPercentage(wavelength);
			}
		}
		return result;
	}

	/**
	 * Calculates a ion percentage distribution.
	 */
	private void calculateDistribution() {

		if(!scanIsNull()) {
			float percentage;
			int wavelength;
			float totalIonSignal = scanWSD.getTotalSignal();
			if(totalIonSignal == 0) {
				return;
			}
			float factor = MAX_PERCENTAGE / totalIonSignal;
			/*
			 * Calculate each percentage distribution and set it to the list.
			 */
			for(IScanSignalWSD scanSignal : scanWSD.getScanSignals()) {
				if(scanSignal != null) {
					percentage = factor * scanSignal.getAbsorbance();
					wavelength = (int)Math.round(scanSignal.getWavelength());
					wavelengthDistribution.put(wavelength, percentage);
				}
			}
		}
	}

	private boolean scanIsNull() {

		return scanWSD == null;
	}
}
