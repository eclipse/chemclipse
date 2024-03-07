/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.wsd.model.comparator.WavelengthValueComparator;
import org.eclipse.chemclipse.wsd.model.core.AbstractScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;

public class ExtractedWavelengthSignal implements IExtractedWavelengthSignal {

	private static final Logger logger = Logger.getLogger(ExtractedWavelengthSignal.class);
	//
	private static final float NORMALIZATION_BASE = 1000.0f;
	//
	private float[] abundanceValues;
	private int startWavelength;
	private int stopWavelength;
	private int retentionTime;
	private float retentionIndex;

	public ExtractedWavelengthSignal(double startWavelength, double stopWavelength) {

		int start = AbstractScanSignalWSD.getWavelength(startWavelength);
		int stop = AbstractScanSignalWSD.getWavelength(stopWavelength);
		/*
		 * It is not allowed to set negative values.<br/> So the value will be
		 * shifted to zero if negative.<br/> How should the array list be
		 * accessed in case of a negative value.<br/>
		 */
		start = (start < 0) ? 0 : start;
		stop = (stop < 0) ? 0 : stop;
		if(startWavelength > stopWavelength) {
			this.startWavelength = stop;
			this.stopWavelength = start;
		} else {
			this.startWavelength = start;
			this.stopWavelength = stop;
		}
		/*
		 * Create an array only if the size is greater or equal 1.
		 */
		int count = this.stopWavelength - this.startWavelength + 1;
		if(count > 0) {
			abundanceValues = new float[count];
		}
	}

	public ExtractedWavelengthSignal(List<IScanSignalWSD> wavelengths) {

		wavelengths = new ArrayList<>(wavelengths);
		if(!wavelengths.isEmpty()) {
			Collections.sort(wavelengths, new WavelengthValueComparator());
			this.startWavelength = AbstractScanSignalWSD.getWavelength(wavelengths.get(0).getWavelength());
			this.stopWavelength = AbstractScanSignalWSD.getWavelength(wavelengths.get(wavelengths.size() - 1).getWavelength());
			/*
			 * Create an array only if the size is greater or equal 1.
			 */
			int count = this.stopWavelength - this.startWavelength + 1;
			if(count > 0) {
				abundanceValues = new float[count];
			}
			/*
			 * Add the intensities.
			 */
			for(IScanSignalWSD scanSignalWSD : wavelengths) {
				setAbundance(scanSignalWSD);
			}
		}
	}

	@Override
	public void setAbundance(IScanSignalWSD scanSignal, boolean removePreviousAbundance) {

		if(removePreviousAbundance) {
			int wavelengthActual = AbstractScanSignalWSD.getWavelength(scanSignal.getWavelength());
			if(isValidWavelength(wavelengthActual)) {
				int position = wavelengthActual - startWavelength;
				abundanceValues[position] = scanSignal.getAbsorbance();
			}
		} else {
			setAbundance(scanSignal);
		}
	}

	@Override
	public void setAbundance(IScanSignalWSD scanSignal) {

		int wavelengthActual = AbstractScanSignalWSD.getWavelength(scanSignal.getWavelength());
		if(isValidWavelength(wavelengthActual)) {
			int position = wavelengthActual - startWavelength;
			abundanceValues[position] += scanSignal.getAbsorbance();
		}
	}

	@Override
	public void setAbundance(int wavelength, float abundance) {

		try {
			IScanSignalWSD defaultWavelength = new ScanSignalWSD(wavelength, abundance);
			setAbundance(defaultWavelength);
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	@Override
	public void setAbundance(int ion, float abundance, boolean removePreviousAbundance) {

		try {
			ScanSignalWSD defaultWavelength = new ScanSignalWSD(ion, abundance);
			setAbundance(defaultWavelength, removePreviousAbundance);
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	@Override
	public float getAbundance(int wavelength) {

		if(isValidWavelength(wavelength)) {
			int position = wavelength - startWavelength;
			return abundanceValues[position];
		}
		return 0;
	}

	@Override
	public int getNumberOfWavelengthValues() {

		if(abundanceValues == null) {
			return 0;
		} else {
			return abundanceValues.length;
		}
	}

	@Override
	public float getTotalSignal() {

		float totalSignal = 0.0f;
		if(abundanceValues != null && abundanceValues.length > 0) {
			totalSignal = Calculations.getSum(abundanceValues);
		}
		return totalSignal;
	}

	@Override
	public int getWavelengthMaxIntensity() {

		/*
		 * No intensities available, then 0.
		 */
		if(abundanceValues.length == 0) {
			return 0;
		}
		/*
		 * Max intensity 0, then 0.
		 */
		float maxIntensity = getMaxIntensity();
		if(maxIntensity == 0) {
			return 0;
		}
		//
		for(int i = 0; i < abundanceValues.length; i++) {
			if(abundanceValues[i] == maxIntensity) {
				return i + startWavelength;
			}
		}
		return 0;
	}

	@Override
	public float getMaxIntensity() {

		return Calculations.getMax(abundanceValues);
	}

	@Override
	public float getNthHighestIntensity(int n) {

		if(n <= 0 || n > abundanceValues.length) {
			return 0;
		} else {
			float[] values = Arrays.copyOf(abundanceValues, abundanceValues.length);
			Arrays.sort(values);
			return values[values.length - n];
		}
	}

	@Override
	public float getMinIntensity() {

		return Calculations.getMin(abundanceValues);
	}

	@Override
	public float getMeanIntensity() {

		return Calculations.getMean(abundanceValues);
	}

	@Override
	public float getMedianIntensity() {

		return Calculations.getMedian(abundanceValues);
	}

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		if(retentionTime >= 0) {
			this.retentionTime = retentionTime;
		}
	}

	@Override
	public float getRetentionIndex() {

		return retentionIndex;
	}

	@Override
	public void setRetentionIndex(float retentionIndex) {

		if(retentionIndex >= 0) {
			this.retentionIndex = retentionIndex;
		}
	}

	@Override
	public int getStartWavelength() {

		return startWavelength;
	}

	@Override
	public int getStopWavelength() {

		return stopWavelength;
	}

	@Override
	public IWavelengthRange getWavelengthRange() {

		IWavelengthRange wavelengthRange = new WavelengthRange(startWavelength, stopWavelength);
		return wavelengthRange;
	}

	@Override
	public void normalize() {

		normalize(NORMALIZATION_BASE);
	}

	@Override
	public void normalize(float normalizationBase) {

		if(normalizationBase > 0) {
			float maxIntensity = Calculations.getMax(abundanceValues);
			if(maxIntensity > 0) {
				float factor = normalizationBase / maxIntensity;
				for(int i = 0; i < abundanceValues.length; i++) {
					abundanceValues[i] = factor * abundanceValues[i];
				}
			}
		}
	}

	private boolean isValidWavelength(int wavelength) {

		/*
		 * If the array has not been created return false.
		 */
		if(abundanceValues == null) {
			return false;
		}
		/*
		 * Check if the value is out of the valid range.<br/> If yes, return
		 * false.
		 */
		if(wavelength < startWavelength || wavelength > stopWavelength) {
			return false;
		}
		/*
		 * If all negative checks has been passed, the result must be true.
		 */
		return true;
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(this.getClass() != otherObject.getClass()) {
			return false;
		}
		ExtractedWavelengthSignal extractedWavelengthSignal = (ExtractedWavelengthSignal)otherObject;
		return startWavelength == extractedWavelengthSignal.startWavelength && //
				stopWavelength == extractedWavelengthSignal.stopWavelength && //
				getNumberOfWavelengthValues() == extractedWavelengthSignal.getNumberOfWavelengthValues() && //
				getTotalSignal() == extractedWavelengthSignal.getTotalSignal();
	}

	@Override
	public int hashCode() {

		return 7 * Integer.valueOf(startWavelength).hashCode() + //
				9 * Integer.valueOf(stopWavelength).hashCode() + //
				11 * Integer.valueOf(getNumberOfWavelengthValues()).hashCode() + //
				13 * Float.valueOf(getTotalSignal()).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("startWavelength=" + startWavelength);
		builder.append(",");
		builder.append("stopWavelength=" + stopWavelength);
		builder.append(",");
		builder.append("numberOfWavelengthValues=" + getNumberOfWavelengthValues());
		builder.append(",");
		builder.append("totalSignal=" + getTotalSignal());
		builder.append("]");
		return builder.toString();
	}
}
