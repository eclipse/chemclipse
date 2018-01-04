/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Derivatives;

public class SecondDerivativeAndNoise implements ISecondDerivativeAndNoise {

	private double[] secondDeriv;
	private double[] noisePositiv;
	private double[] noiseNegativ;

	public SecondDerivativeAndNoise(double[] secondDerivValues, double[] noisePositivValues, double[] noiseNegativValues) {
		secondDeriv = secondDerivValues;
		noisePositiv = noisePositivValues;
		noiseNegativ = noiseNegativValues;
	}

	public double[] getSecondDeriv() {

		return secondDeriv;
	}

	public double[] getNoisePositiv() {

		return noisePositiv;
	}

	public double[] getNoiseNegative() {

		return noiseNegativ;
	}

	public void setSecondDeriv(double[] secondDerivValues) {

		this.secondDeriv = secondDerivValues;
	}

	public void setNoisePositiv(double[] noisePositivValues) {

		this.noisePositiv = noisePositivValues;
	}

	public void setNoiseNegativ(double[] noiseNegativValues) {

		this.noiseNegativ = noiseNegativValues;
	}
}
