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

public class DerivativesAndNoise implements IDerivativesAndNoise {

	private IFirstDerivativeAndNoise firstDerivativeAndNoise;
	private ISecondDerivativeAndNoise secondDerivativeAndNoise;
	private IThirdDerivativeAndNoise thirdDerivativeAndNoise;

	public DerivativesAndNoise(IFirstDerivativeAndNoise firstDerivative, ISecondDerivativeAndNoise secondDerivative, IThirdDerivativeAndNoise thirdDerivative) {
		firstDerivativeAndNoise = firstDerivative;
		secondDerivativeAndNoise = secondDerivative;
		thirdDerivativeAndNoise = thirdDerivative;
	}

	public IFirstDerivativeAndNoise getFirstDerivativeAndNoise() {

		return firstDerivativeAndNoise;
	}

	public ISecondDerivativeAndNoise getSecondDerivativeAndNoise() {

		return secondDerivativeAndNoise;
	}

	public IThirdDerivativeAndNoise getThirdDerivativeAndNoise() {

		return thirdDerivativeAndNoise;
	}

	public void setFirstDerivativeAndNoise(IFirstDerivativeAndNoise firstDerivAndNoise) {

		this.firstDerivativeAndNoise = firstDerivAndNoise;
	}

	public void setSecondDerivativeAndNoise(ISecondDerivativeAndNoise secondDerivAndNoise) {

		this.secondDerivativeAndNoise = secondDerivAndNoise;
	}

	public void setThirdDerivativeAndNoise(IThirdDerivativeAndNoise thirdDerivAndNoise) {

		this.thirdDerivativeAndNoise = thirdDerivAndNoise;
	}
}
