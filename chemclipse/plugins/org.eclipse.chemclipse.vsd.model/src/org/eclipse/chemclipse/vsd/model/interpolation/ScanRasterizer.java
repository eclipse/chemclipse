/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.vsd.model.interpolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.interpolation.RasterizeCalculator;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.chemclipse.vsd.model.core.IScanVSD;
import org.eclipse.chemclipse.vsd.model.core.ISignalVSD;
import org.eclipse.chemclipse.vsd.model.implementation.SignalInfrared;
import org.eclipse.chemclipse.vsd.model.implementation.SignalRaman;

public class ScanRasterizer {

	public static void normalize(IChromatogramVSD chromatogramISD, int steps) {

		if(chromatogramISD != null) {
			for(IScan scan : chromatogramISD.getScans()) {
				if(scan instanceof IScanVSD scanISD) {
					normalize(scanISD, steps);
				}
			}
		}
	}

	public static void normalize(IScanVSD scanVSD, int steps) {

		if(scanVSD != null) {
			if(steps >= RasterizeCalculator.MIN_STEPS && steps <= RasterizeCalculator.MAX_STEPS) {
				/*
				 * Collect the data
				 */
				boolean isInfrared = true;
				TreeMap<Float, Float> wavenumberOriginal = new TreeMap<>();
				for(ISignalVSD signal : scanVSD.getProcessedSignals()) {
					isInfrared = signal instanceof SignalInfrared;
					wavenumberOriginal.put((float)signal.getWavenumber(), (float)signal.getIntensity());
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
					scanVSD.getProcessedSignals().clear();
					for(int wavenumber : wavenumbers) {
						scanVSD.getProcessedSignals().add(getSignalVSD(isInfrared, wavenumber, wavenumbersAdjusted.get(wavenumber)));
					}
				}
			}
		}
	}

	private static ISignalVSD getSignalVSD(boolean isInfrared, double wavenumber, double signal) {

		if(isInfrared) {
			return new SignalInfrared(wavenumber, signal);
		} else {
			return new SignalRaman(wavenumber, signal);
		}
	}
}
