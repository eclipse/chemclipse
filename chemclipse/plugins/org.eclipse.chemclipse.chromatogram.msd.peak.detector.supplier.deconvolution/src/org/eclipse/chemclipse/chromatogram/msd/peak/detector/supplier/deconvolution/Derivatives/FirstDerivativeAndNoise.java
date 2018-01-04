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

public class FirstDerivativeAndNoise implements IFirstDerivativeAndNoise {

	private double[] firstDeriv;
	private double[] noisePositiv;
	private double[] noiseNegativ;

	public FirstDerivativeAndNoise(double[] firstDerivValues, double[] noisePositivValues, double[] noiseNegativValues) {
		firstDeriv = firstDerivValues;
		noisePositiv = noisePositivValues;
		noiseNegativ = noiseNegativValues;
	}

	public double[] getFirstDeriv() {

		return firstDeriv;
	}

	public double[] getNoisePositiv() {

		return noisePositiv;
	}

	public double[] getNoiseNegative() {

		return noiseNegativ;
	}

	public void setFirstDeriv(double[] firstDerivValues) {

		this.firstDeriv = firstDerivValues;
	}

	public void setNoisePositiv(double[] noisePositivValues) {

		this.noisePositiv = noisePositivValues;
	}

	public void setNoiseNegativ(double[] noiseNegativValues) {

		this.noiseNegativ = noiseNegativValues;
	}
}
