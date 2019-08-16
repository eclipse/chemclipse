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

public class ThirdDerivativeAndNoise implements IThirdDerivativeAndNoise {

	private double[] thirdDeriv;
	private double[] noisePositiv;
	private double[] noiseNegativ;

	public ThirdDerivativeAndNoise(double[] thirdDerivValues, double[] noisePositivValues, double[] noiseNegativValues) {
		thirdDeriv = thirdDerivValues;
		noisePositiv = noisePositivValues;
		noiseNegativ = noiseNegativValues;
	}

	public double[] getThirdDeriv() {

		return thirdDeriv;
	}

	public double[] getNoisePositiv() {

		return noisePositiv;
	}

	public double[] getNoiseNegative() {

		return noiseNegativ;
	}

	public void setThirdDeriv(double[] thirdDerivValues) {

		this.thirdDeriv = thirdDerivValues;
	}

	public void setNoisePositiv(double[] noisePositivValues) {

		this.noisePositiv = noisePositivValues;
	}

	public void setNoiseNegativ(double[] noiseNegativValues) {

		this.noiseNegativ = noiseNegativValues;
	}
}
