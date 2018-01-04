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
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;

public interface IDeconvHelper {

	/*
	 * Strings
	 */
	// EventBroker
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAKDECONDETEC = "chromatogram/msd/update/peakdecondetec";
	// ArraysViewDeconv
	String PROPERTY_DECONVIEW_ORIGINALCHROMA = "OriginalChromatogram";
	String PROPERTY_DECONVIEW_BASELINE = "BaselineDeconv";
	String PROPTERY_DECONVIEW_NOISE = "NoiseDeconv";
	String PROPTERY_DECONVIEW_SMOOTHED = "SmoothedValues";
	String PROPTERY_DECONVIEW_FIRSTDERIV = "FirstDerivative";
	String PROPTERY_DECONVIEW_SECONDDERIV = "SecondDerivative";
	String PROPTERY_DECONVIEW_THIRDDERIV = "ThirdDerivative";
	String PROPERTY_DECONVIEW_PEAKRANGES = "PeakRangesStartPoint";
	String PROPERTY_DECONVIEW_PEAKRANGES_ENDPOINTS = "PeakRangesEndPoint";

	//
	/*
	 * 
	 */
	float[] getFloatArray(double[] doubleArray);

	double[] getDoubleArray(float[] floatArray);

	double[] getNoisePlusBaselin(double[] baseline, double[] noise);

	float[] getSignalAsArrayFloatDeconv(ITotalScanSignals totalIonSignals);

	double[] getSignalAsArrayDoubleDeconvCODA(ITotalScanSignals totalIONsignalsExtractedCodaIons, ITotalScanSignals totalIONsignals);

	double[] getSignalAsArrayDoubleDeconv(ITotalScanSignals totalIonSignals);

	double[] setXValueforPrint(ITotalScanSignals signals);

	double[] positivToNegativ(double[] noisePositiv);

	double[] factorisingValues(double[] noisePositiv, int Factor);

	IAllIonSignals setXValueToAllIonSignals(IAllIonSignals allIonSignals, ITotalScanSignals signals);
}
