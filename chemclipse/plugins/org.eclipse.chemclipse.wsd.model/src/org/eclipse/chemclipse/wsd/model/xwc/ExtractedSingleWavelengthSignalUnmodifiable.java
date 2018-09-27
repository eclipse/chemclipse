/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import java.util.Objects;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;

public class ExtractedSingleWavelengthSignalUnmodifiable implements IExtractedSingleWavelengthSignal {

	private static final Logger logger = Logger.getLogger(ExtractedSingleWavelengthSignalUnmodifiable.class);
	//
	private float abundanceValue;
	private double wavelength;
	private int retentionTime;
	private float retentionIndex;

	/**
	 * 
	 * @param wavelength
	 * @param abundanceValue
	 * @param retentionTime
	 * @param retentionIndex
	 */
	public ExtractedSingleWavelengthSignalUnmodifiable(double wavelength, float abundanceValue, int retentionTime, float retentionIndex) {

		this.wavelength = wavelength;
		this.abundanceValue = abundanceValue;
		this.retentionTime = retentionTime;
		this.retentionIndex = retentionIndex;
	}

	private ExtractedSingleWavelengthSignalUnmodifiable() {

	}

	/**
	 * 
	 * @param scanSignalWSD
	 * @param retentionTime
	 * @param retentionIndex
	 */
	public ExtractedSingleWavelengthSignalUnmodifiable(IScanSignalWSD scanSignalWSD, int retentionTime, float retentionIndex) {

		this(scanSignalWSD.getWavelength(), scanSignalWSD.getAbundance(), retentionTime, retentionIndex);
	}

	@Override
	public void setTotalSignal(float abundance) {

		throw new UnsupportedOperationException("Object is unmodifiable");
	}

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		new UnsupportedOperationException("Object is unmodifiable");
	}

	@Override
	public float getRetentionIndex() {

		return retentionIndex;
	}

	@Override
	public void setRetentionIndex(float retentionIndex) {

		new UnsupportedOperationException("Object is unmodifiable");
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
		ExtractedSingleWavelengthSignalUnmodifiable extractedWavelengthSignal = (ExtractedSingleWavelengthSignalUnmodifiable)otherObject;
		return wavelength == extractedWavelengthSignal.wavelength && //
				abundanceValue == extractedWavelengthSignal.abundanceValue && retentionTime == extractedWavelengthSignal.retentionTime && retentionIndex == extractedWavelengthSignal.retentionIndex;
	}

	@Override
	public int hashCode() {

		return Objects.hash(wavelength, abundanceValue, retentionTime, retentionIndex);
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("wavelength=" + wavelength);
		builder.append(",");
		builder.append("abundanceValue=" + abundanceValue);
		builder.append(",");
		builder.append("retentionTime=" + retentionTime);
		builder.append(",");
		builder.append("retentionIndex=" + retentionIndex);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public float getTotalSignal() {

		return abundanceValue;
	}

	@Override
	public double getWavelength() {

		return wavelength;
	}

	@Override
	public void setTotalSignal(float totalSignal, boolean validatePositive) {

		new UnsupportedOperationException("Object is unmodifiable");
	}

	@Override
	public IExtractedSingleWavelengthSignal makeDeepCopy() {

		ExtractedSingleWavelengthSignalUnmodifiable extractedSingleWavelengthSignal = new ExtractedSingleWavelengthSignalUnmodifiable();
		extractedSingleWavelengthSignal.abundanceValue = abundanceValue;
		extractedSingleWavelengthSignal.wavelength = wavelength;
		extractedSingleWavelengthSignal.retentionTime = retentionTime;
		extractedSingleWavelengthSignal.retentionIndex = retentionIndex;
		return extractedSingleWavelengthSignal;
	}
}
