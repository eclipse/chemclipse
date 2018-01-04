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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.IAllIonSignals;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals.RetentionTime;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;

public class DeconvHelper implements IDeconvHelper {

	public float[] getFloatArray(double[] doubleArray) {

		int size = doubleArray.length;
		float[] floatArray = new float[size];
		for(int i = 0; i < size; i++) {
			floatArray[i] = (float)doubleArray[i];
		}
		return floatArray;
	}

	public double[] positivToNegativ(double[] noisePositiv) {

		double[] noiseNegativ = new double[noisePositiv.length];
		for(int i = 0; i < noisePositiv.length; i++) {
			noiseNegativ[i] = noisePositiv[i] * (-1);
		}
		return noiseNegativ;
	}

	public double[] factorisingValues(double[] noisePositiv, int Factor) {

		double[] noiseNegativ = new double[noisePositiv.length];
		for(int i = 0; i < noisePositiv.length; i++) {
			noiseNegativ[i] = noisePositiv[i] * Factor;
		}
		return noiseNegativ;
	}

	public double[] getDoubleArray(float[] floatArray) {

		int size = floatArray.length;
		double[] doubleArray = new double[size];
		for(int i = 0; i < size; i++) {
			doubleArray[i] = (double)floatArray[i];
		}
		return doubleArray;
	}

	public double[] getNoisePlusBaselin(double[] baseline, double[] noise) {

		int size = baseline.length;
		double[] doubleArray = new double[size];
		for(int i = 0; i < size; i++) {
			doubleArray[i] = baseline[i] + noise[i];
		}
		return doubleArray;
	}

	public float[] getSignalAsArrayFloatDeconv(ITotalScanSignals totalIonSignals) {

		int size = totalIonSignals.size();
		float[] ticValuesArray = new float[size];
		int counter = 0;
		for(ITotalScanSignal signal : totalIonSignals.getTotalScanSignals()) {
			ticValuesArray[counter++] = signal.getTotalSignal();
		}
		return ticValuesArray;
	}

	public double[] getSignalAsArrayDoubleDeconvCODA(ITotalScanSignals totalIONsignalsExtractedCodaIons, ITotalScanSignals totalIonSignals) {

		int size = totalIonSignals.size();
		int startScan = totalIonSignals.getStartScan();
		int stopScan = totalIonSignals.getStopScan();
		double[] ticValuesArray = new double[size];
		int counter = 0, isValidRangeValue = 0;
		for(ITotalScanSignal signal : totalIONsignalsExtractedCodaIons.getTotalScanSignals()) {
			if((counter >= startScan)) {
				ticValuesArray[isValidRangeValue++] = (double)signal.getTotalSignal();
			}
			if(counter == stopScan) {
				break;
			}
			counter++;
		}
		return ticValuesArray;
	}

	public double[] getSignalAsArrayDoubleDeconv(ITotalScanSignals totalIonSignals) {

		int size = totalIonSignals.size();
		double[] ticValuesArray = new double[size];
		int counter = 0;
		for(ITotalScanSignal signal : totalIonSignals.getTotalScanSignals()) {
			ticValuesArray[counter++] = signal.getTotalSignal();
		}
		return ticValuesArray;
	}

	public double[] setXValueforPrint(ITotalScanSignals signals) {

		int sizeSignal = signals.size();
		int i = 0;
		double retentionTime;
		double[] xScale = new double[sizeSignal];
		for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
			retentionTime = signal.getRetentionTime();
			xScale[i++] = retentionTime;
		}
		return xScale;
	}

	public IAllIonSignals setXValueToAllIonSignals(IAllIonSignals allIonSignals, ITotalScanSignals signals) {

		int sizeSignal = signals.size();
		int i = 0;
		double retentionTime;
		double[] xScale = new double[sizeSignal];
		for(ITotalScanSignal signal : signals.getTotalScanSignals()) {
			retentionTime = signal.getRetentionTime();
			IRetentionTime rTime = new RetentionTime(signal.getRetentionTime());
			allIonSignals.addRetentionTime(rTime);
			xScale[i++] = retentionTime;
		}
		return allIonSignals;
	}
}
