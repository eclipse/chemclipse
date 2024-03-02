/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.chemclipse.xir.model.core.IScanISD;
import org.eclipse.chemclipse.xir.model.core.ISignalVS;
import org.eclipse.chemclipse.xir.model.core.SignalType;
import org.eclipse.chemclipse.xir.model.implementation.ScanISD;
import org.eclipse.chemclipse.xir.model.implementation.SignalInfrared;
import org.eclipse.chemclipse.xir.model.implementation.SignalRaman;

public class CombinedScanCalculator {

	private Map<Double, List<Double>> combinedScan = new HashMap<>();

	public static int getWavenumber(double wavenumber) {

		return (int)Math.round(wavenumber);
	}

	public int size() {

		return combinedScan.size();
	}

	public void addSignals(IScanISD scanISD, boolean nominalizeWavenumber) {

		if(scanISD != null) {
			for(ISignalVS signal : scanISD.getProcessedSignals()) {
				double wavenumber = nominalizeWavenumber ? getWavenumber(signal.getWavenumber()) : signal.getWavenumber();
				List<Double> intensities = combinedScan.get(wavenumber);
				if(intensities == null) {
					intensities = new ArrayList<>();
					combinedScan.put(wavenumber, intensities);
				}
				intensities.add(signal.getIntensity());
			}
		}
	}

	public IScanISD createScan(CalculationType calculationType, SignalType signalType) {

		IScanISD scanISD = new ScanISD();
		for(Double wavenumber : combinedScan.keySet()) {
			ISignalVS signal;
			double intensity = getIntensity(wavenumber, calculationType);
			switch(signalType) {
				case FTIR:
					signal = new SignalInfrared(wavenumber, intensity);
					break;
				default:
					signal = new SignalRaman(wavenumber, intensity);
					break;
			}
			scanISD.getProcessedSignals().add(signal);
		}
		//
		return scanISD;
	}

	private double getIntensity(double wavenumber, CalculationType calculationType) {

		return calculateSumIntensity(combinedScan.get(wavenumber), calculationType);
	}

	private double calculateSumIntensity(List<Double> intensities, CalculationType calculationType) {

		double sumIntensity = 0.0d;
		if(intensities != null) {
			/*
			 * Add an option here to calculate the sum,
			 * mean or median signal.
			 */
			double[] values = intensities.stream().mapToDouble(Double::doubleValue).toArray();
			switch(calculationType) {
				case SUM:
					sumIntensity = Calculations.getSum(values);
					break;
				case MEAN:
					sumIntensity = Calculations.getMean(values);
					break;
				case MEDIAN:
					sumIntensity = Calculations.getMedian(values);
					break;
			}
		}
		//
		return sumIntensity;
	}
}
