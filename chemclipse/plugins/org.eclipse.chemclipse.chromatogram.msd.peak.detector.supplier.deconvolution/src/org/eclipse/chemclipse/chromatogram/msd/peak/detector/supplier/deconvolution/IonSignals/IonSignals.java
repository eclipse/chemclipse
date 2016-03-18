/*******************************************************************************
 * Copyright (c) 2016 Florian Ernst
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

import java.util.ArrayList;
import java.util.List;

public class IonSignals implements IIonSignals {

	private int Ion;
	private double[] ionSignals;
	private List<IIonSignal> ionSegmentValue;
	private double meanValue;
	private int steinYes;
	private int steinNo;

	public IonSignals(int ion, double[] IonSignals, double MeanValue) {
		Ion = ion;
		ionSignals = IonSignals;
		ionSegmentValue = new ArrayList<IIonSignal>(0);
		meanValue = MeanValue;
		steinYes = 0;
		steinNo = 0;
	}

	public int getIon() {

		return Ion;
	}

	public double getMean() {

		return meanValue;
	}

	public double[] getIonSignals() {

		return ionSignals;
	}

	public void setIonSignals(double[] IonSignals) {

		this.ionSignals = IonSignals;
	}

	public int size() {

		return ionSignals.length;
	}

	public IIonSignal getSegmentValue(int value) {

		return ionSegmentValue.get(value);
	}

	public List<IIonSignal> getSegmentSignals() {

		return ionSegmentValue;
	}

	public int sizeSegmentSum() {

		return ionSegmentValue.size();
	}

	public void addSegmentValue(IIonSignal value) {

		ionSegmentValue.add(value);
	}

	public void setSteinYes(int value) {

		this.steinYes = value;
	}

	public int getSteinYes() {

		return steinYes;
	}

	public void setSteinNo(int value) {

		this.steinNo = value;
	}

	public int getSteinNo() {

		return steinNo;
	}
}
