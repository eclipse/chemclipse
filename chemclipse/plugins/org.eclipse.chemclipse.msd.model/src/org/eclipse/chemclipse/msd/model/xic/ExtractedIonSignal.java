/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.comparator.IonValueComparator;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.numeric.statistics.Calculations;

public class ExtractedIonSignal implements IExtractedIonSignal {

	private static final Logger logger = Logger.getLogger(ExtractedIonSignal.class);
	//
	private static final float NORMALIZATION_BASE = 1000.0f;
	//
	private float[] abundanceValues;
	private int startIon;
	private int stopIon;
	private int retentionTime;
	private float retentionIndex;

	/**
	 * The values startIon and stopIon must be positive.<br/>
	 * Negative values will not be regarded.
	 * 
	 * @param startIon
	 * @param stopIon
	 */
	public ExtractedIonSignal(double startIon, double stopIon) {
		int start = AbstractIon.getIon(startIon);
		int stop = AbstractIon.getIon(stopIon);
		/*
		 * It is not allowed to set negative values.<br/> So the value will be
		 * shifted to zero if negative.<br/> How should the array list be
		 * accessed in case of a negative value.<br/>
		 */
		start = (start < 0) ? 0 : start;
		stop = (stop < 0) ? 0 : stop;
		if(startIon > stopIon) {
			this.startIon = stop;
			this.stopIon = start;
		} else {
			this.startIon = start;
			this.stopIon = stop;
		}
		/*
		 * Create an array only if the size is greater or equal 1.
		 */
		int count = this.stopIon - this.startIon + 1;
		if(count > 0) {
			abundanceValues = new float[count];
		}
	}

	public ExtractedIonSignal(List<IIon> ions) {
		ions = new ArrayList<>(ions);
		if(ions != null && ions.size() > 0) {
			Collections.sort(ions, new IonValueComparator());
			this.startIon = AbstractIon.getIon(ions.get(0).getIon());
			this.stopIon = AbstractIon.getIon(ions.get(ions.size() - 1).getIon());
			/*
			 * Create an array only if the size is greater or equal 1.
			 */
			int count = this.stopIon - this.startIon + 1;
			if(count > 0) {
				abundanceValues = new float[count];
			}
			/*
			 * Add the intensities.
			 */
			for(IIon ion : ions) {
				setAbundance(ion);
			}
		}
	}

	@Override
	public void setAbundance(IIon ion, boolean removePreviousAbundance) {

		if(removePreviousAbundance) {
			int ionActual = AbstractIon.getIon(ion.getIon());
			if(isValidIon(ionActual)) {
				int position = ionActual - startIon;
				abundanceValues[position] = ion.getAbundance();
			}
		} else {
			setAbundance(ion);
		}
	}

	@Override
	public void setAbundance(IIon ion) {

		int ionActual = AbstractIon.getIon(ion.getIon());
		if(isValidIon(ionActual)) {
			int position = ionActual - startIon;
			abundanceValues[position] += ion.getAbundance();
		}
	}

	@Override
	public void setAbundance(int ion, float abundance) {

		try {
			Ion defaultIon = new Ion(ion, abundance);
			setAbundance(defaultIon);
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	@Override
	public void setAbundance(int ion, float abundance, boolean removePreviousAbundance) {

		try {
			Ion defaultIon = new Ion(ion, abundance);
			setAbundance(defaultIon, removePreviousAbundance);
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	@Override
	public float getAbundance(int ion) {

		if(isValidIon(ion)) {
			int position = ion - startIon;
			return abundanceValues[position];
		}
		return 0;
	}

	@Override
	public int getNumberOfIonValues() {

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
	public int getIonMaxIntensity() {

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
				return i + startIon;
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

		System.out.println("JUNIT");
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
	public int getStartIon() {

		return startIon;
	}

	@Override
	public int getStopIon() {

		return stopIon;
	}

	@Override
	public IIonRange getIonRange() {

		IIonRange ionRange = new IonRange(startIon, stopIon);
		return ionRange;
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

	private boolean isValidIon(int ion) {

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
		if(ion < startIon || ion > stopIon) {
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
		ExtractedIonSignal extractedIonSignal = (ExtractedIonSignal)otherObject;
		return startIon == extractedIonSignal.startIon && //
				stopIon == extractedIonSignal.stopIon && //
				getNumberOfIonValues() == extractedIonSignal.getNumberOfIonValues() && //
				getTotalSignal() == extractedIonSignal.getTotalSignal();
	}

	@Override
	public int hashCode() {

		return 7 * Integer.valueOf(startIon).hashCode() + //
				9 * Integer.valueOf(stopIon).hashCode() + //
				11 * Integer.valueOf(getNumberOfIonValues()).hashCode() + //
				13 * Float.valueOf(getTotalSignal()).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("startIon=" + startIon);
		builder.append(",");
		builder.append("stopIon=" + stopIon);
		builder.append(",");
		builder.append("numberOfIonValues=" + getNumberOfIonValues());
		builder.append(",");
		builder.append("totalSignal=" + getTotalSignal());
		builder.append("]");
		return builder.toString();
	}
}
