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
package org.eclipse.chemclipse.wsd.model.core.interpolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.interpolation.RasterizeCalculator;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;

public class ScanRasterizer {

	public static void normalize(IChromatogramWSD chromatogramWSD, int steps) {

		if(chromatogramWSD != null) {
			for(IScan scan : chromatogramWSD.getScans()) {
				if(scan instanceof IScanWSD scanWSD) {
					normalize(scanWSD, steps);
				}
			}
		}
	}

	public static void normalize(IScanWSD scanWSD, int steps) {

		if(scanWSD != null) {
			if(steps >= RasterizeCalculator.MIN_STEPS && steps <= RasterizeCalculator.MAX_STEPS) {
				/*
				 * Collect the data
				 */
				TreeMap<Float, Float> wavelengthOriginal = new TreeMap<>();
				for(IScanSignalWSD scanSignalWSD : scanWSD.getScanSignals()) {
					wavelengthOriginal.put(scanSignalWSD.getWavelength(), scanSignalWSD.getAbsorbance());
				}
				//
				Map<Integer, Float> wavelengthsAdjusted = RasterizeCalculator.apply(wavelengthOriginal, steps);
				if(wavelengthsAdjusted != null) {
					/*
					 * Rasterized Data
					 */
					List<Integer> wavelengths = new ArrayList<>(wavelengthsAdjusted.keySet());
					Collections.sort(wavelengths);
					//
					scanWSD.deleteScanSignals();
					for(int wavelength : wavelengths) {
						scanWSD.addScanSignal(new ScanSignalWSD(wavelength, wavelengthsAdjusted.get(wavelength)));
					}
				}
			}
		}
	}
}
