/*******************************************************************************
 * Copyright (c) 2016, 2018 Florian Ernst.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals;

public class IonSignal implements IIonSignal {

	private double signal;

	public IonSignal(double value) {
		signal = value;
	}

	public double getSignal() {

		return signal;
	}

	public void setSignal(int value) {

		this.signal = value;
	}
}
