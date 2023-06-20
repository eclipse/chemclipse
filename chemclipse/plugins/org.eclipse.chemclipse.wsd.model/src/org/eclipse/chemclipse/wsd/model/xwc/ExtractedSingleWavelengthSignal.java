/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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

import java.util.Objects;

import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;

public class ExtractedSingleWavelengthSignal implements IExtractedSingleWavelengthSignal {

	private float abundanceValue;
	private float wavelength;
	private int retentionTime;
	private float retentionIndex;

	public ExtractedSingleWavelengthSignal(float wavelength, float abundanceValue, int retentionTime, float retentionIndex) {

		this.wavelength = wavelength;
		this.abundanceValue = abundanceValue;
		this.retentionTime = retentionTime;
		this.retentionIndex = retentionIndex;
	}

	private ExtractedSingleWavelengthSignal() {

	}

	public ExtractedSingleWavelengthSignal(IScanSignalWSD scanSignalWSD, int retentionTime, float retentionIndex) {

		this(scanSignalWSD.getWavelength(), scanSignalWSD.getAbundance(), retentionTime, retentionIndex);
	}

	@Override
	public void setTotalSignal(float abundance) {

		abundanceValue = abundance;
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
		ExtractedSingleWavelengthSignal extractedWavelengthSignal = (ExtractedSingleWavelengthSignal)otherObject;
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

		if(validatePositive && totalSignal < 0.0f) {
			return;
		}
		setTotalSignal(totalSignal);
	}

	@Override
	public IExtractedSingleWavelengthSignal makeDeepCopy() {

		ExtractedSingleWavelengthSignal extractedSingleWavelengthSignal = new ExtractedSingleWavelengthSignal();
		extractedSingleWavelengthSignal.abundanceValue = abundanceValue;
		extractedSingleWavelengthSignal.wavelength = wavelength;
		extractedSingleWavelengthSignal.retentionTime = retentionTime;
		extractedSingleWavelengthSignal.retentionIndex = retentionIndex;
		return extractedSingleWavelengthSignal;
	}
}
