/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.interpolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.interpolation.RasterizeCalculator;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.core.IScanISD;
import org.eclipse.chemclipse.xir.model.core.ISignalXIR;
import org.eclipse.chemclipse.xir.model.implementation.SignalInfrared;
import org.eclipse.chemclipse.xir.model.implementation.SignalRaman;

public class ScanRasterizer {

	public static void normalize(IChromatogramISD chromatogramISD, int steps) {

		if(chromatogramISD != null) {
			for(IScan scan : chromatogramISD.getScans()) {
				if(scan instanceof IScanISD scanISD) {
					normalize(scanISD, steps);
				}
			}
		}
	}

	public static void normalize(IScanISD scanISD, int steps) {

		if(scanISD != null) {
			if(steps >= RasterizeCalculator.MIN_STEPS && steps <= RasterizeCalculator.MAX_STEPS) {
				/*
				 * Collect the data
				 */
				boolean isInfrared = true;
				TreeMap<Float, Float> wavenumberOriginal = new TreeMap<>();
				for(ISignalXIR signalXIR : scanISD.getProcessedSignals()) {
					isInfrared = signalXIR instanceof SignalInfrared;
					wavenumberOriginal.put((float)signalXIR.getWavenumber(), (float)signalXIR.getIntensity());
				}
				//
				Map<Integer, Float> wavenumbersAdjusted = RasterizeCalculator.apply(wavenumberOriginal, steps);
				if(wavenumbersAdjusted != null) {
					/*
					 * Rasterized Data
					 */
					List<Integer> wavenumbers = new ArrayList<>(wavenumbersAdjusted.keySet());
					Collections.sort(wavenumbers);
					//
					scanISD.getProcessedSignals().clear();
					for(int wavenumber : wavenumbers) {
						scanISD.getProcessedSignals().add(getSignalXIR(isInfrared, wavenumber, wavenumbersAdjusted.get(wavenumber)));
					}
				}
			}
		}
	}

	private static ISignalXIR getSignalXIR(boolean isInfrared, double wavenumber, double signal) {

		if(isInfrared) {
			return new SignalInfrared(wavenumber, signal);
		} else {
			return new SignalRaman(wavenumber, signal);
		}
	}
}