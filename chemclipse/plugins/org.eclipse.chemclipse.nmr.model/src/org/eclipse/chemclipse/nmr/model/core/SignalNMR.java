/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.model.core.AbstractSignal;

public class SignalNMR extends AbstractSignal implements ISignalNMR, Comparable<ISignalNMR> {

	private static final long serialVersionUID = 6450313862258177287L;
	//
	private double chemicalShift = 0.0d;
	private double intensity = 0.0d;
	private Complex fourierTransformedData;
	private Complex phaseCorrection;
	private Complex baselineCorrection;

	public SignalNMR(double chemicalShift, double intensity) {

		this.chemicalShift = chemicalShift;
		this.intensity = intensity;
		this.phaseCorrection = new Complex(1.0);
		this.baselineCorrection = new Complex(0.0);
		this.fourierTransformedData = new Complex(intensity);
	}

	public SignalNMR(double chemicalShift, Complex fourierTransformedData) {

		this.chemicalShift = chemicalShift;
		this.intensity = fourierTransformedData.getReal();
		this.fourierTransformedData = fourierTransformedData;
		this.phaseCorrection = new Complex(1.0);
		this.baselineCorrection = new Complex(0.0);
	}

	@Override
	public Complex getFourierTransformedData() {

		return fourierTransformedData;
	}

	@Override
	public void setFourierTransformedData(Complex fourierTransformedData) {

		this.fourierTransformedData = fourierTransformedData;
	}

	@Override
	public Complex getPhaseCorrectedData() {

		return getFourierTransformedData().multiply(phaseCorrection);
	}

	@Override
	public Complex getBaselineCorrectedData() {

		return getPhaseCorrectedData().subtract(baselineCorrection);
	}

	@Override
	public Complex getPhaseCorrection() {

		return phaseCorrection;
	}

	@Override
	public void setPhaseCorrection(Complex phaseCorrection) {

		this.phaseCorrection = phaseCorrection;
	}

	@Override
	public Complex getBaselineCorrection() {

		return baselineCorrection;
	}

	@Override
	public void setBaselineCorrection(Complex baselineCorrection) {

		this.baselineCorrection = baselineCorrection;
	}

	@Override
	public double getX() {

		return chemicalShift;
	}

	@Override
	public double getY() {

		return intensity;
	}

	@Override
	public double getChemicalShift() {

		return chemicalShift;
	}

	@Override
	public void setChemicalShift(double chemicalShift) {

		this.chemicalShift = chemicalShift;
	}

	@Override
	public double getIntensity() {

		return intensity;
	}

	@Override
	public void setIntensity(double intensity) {

		this.intensity = intensity;
	}

	@Override
	public void resetIntesity() {

		this.intensity = getBaselineCorrectedData().getReal();
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(chemicalShift);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		SignalNMR other = (SignalNMR)obj;
		if(Double.doubleToLongBits(chemicalShift) != Double.doubleToLongBits(other.chemicalShift))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "SignalNMR [chemicalShift=" + chemicalShift + ", intensity=" + intensity + "]";
	}

	@Override
	public int compareTo(ISignalNMR signalNMR) {

		if(signalNMR != null) {
			return Double.compare(chemicalShift, signalNMR.getChemicalShift());
		} else {
			return 0;
		}
	}
}
