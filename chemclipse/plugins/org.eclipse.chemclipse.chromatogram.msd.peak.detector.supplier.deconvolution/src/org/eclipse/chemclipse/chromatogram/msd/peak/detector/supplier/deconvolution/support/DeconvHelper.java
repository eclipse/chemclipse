/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
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

import org.eclipse.chemclipse.chromatogram.peak.detector.support.IDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
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

	public double[] getDoubleArray(float[] floatArray) {

		int size = floatArray.length;
		double[] doubleArray = new double[size];
		for(int i = 0; i < size; i++) {
			doubleArray[i] = (double)floatArray[i];
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

	public void setSlopetoTotalScanSignals(ITotalScanSignals totalScanSignals, IFirstDerivativeDetectorSlopes slopes) {

		int column = 0;
		int scanOffset = slopes.getStartScan();
		int slopesmax = slopes.getStopScan() - scanOffset + 1;
		double newScan = 0.0f;
		for(ITotalScanSignal signal : totalScanSignals.getTotalScanSignals()) {
			if(column < slopesmax) {
				IDetectorSlope slope = slopes.getDetectorSlope(column + scanOffset);
				newScan = slope.getSlope();
				signal.setTotalSignal((float)newScan);
				column++;
			} else {
				break;
			}
		}
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
}
