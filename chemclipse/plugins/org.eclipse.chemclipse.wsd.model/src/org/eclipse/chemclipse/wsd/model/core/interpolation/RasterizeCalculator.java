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
package org.eclipse.chemclipse.wsd.model.core.interpolation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

public class RasterizeCalculator {

	public static final int MIN_STEPS = 1;
	public static final int MAX_STEPS = 10;

	public static Map<Integer, Float> apply(TreeMap<Float, Float> dataOriginal, int steps) {

		Map<Integer, Float> dataAdjusted = null;
		if(steps >= RasterizeCalculator.MIN_STEPS && steps <= RasterizeCalculator.MAX_STEPS) {
			if(dataOriginal.size() >= 4) {
				/*
				 * Range
				 */
				dataAdjusted = new HashMap<>();
				Entry<Float, Float> firstWavelengthEntry = dataOriginal.firstEntry();
				Entry<Float, Float> lastWavelengthEntry = dataOriginal.lastEntry();
				//
				int startWavelengh = Math.round(firstWavelengthEntry.getKey());
				adjust(dataAdjusted, startWavelengh, firstWavelengthEntry, dataOriginal.ceilingEntry((float)(startWavelengh + 1)));
				//
				int stopWavelengh = Math.round(lastWavelengthEntry.getKey());
				adjust(dataAdjusted, stopWavelengh, dataOriginal.floorEntry((float)(stopWavelengh - 1)), lastWavelengthEntry);
				//
				for(int wavelength = (startWavelengh + 1); wavelength <= (stopWavelengh - 1); wavelength++) {
					if(wavelength % steps == 0) {
						Entry<Float, Float> floorEntry = dataOriginal.floorEntry((float)wavelength);
						Entry<Float, Float> ceilingEntry = dataOriginal.ceilingEntry((float)wavelength);
						adjust(dataAdjusted, wavelength, floorEntry, ceilingEntry);
					}
				}
			}
		}
		//
		return dataAdjusted;
	}

	private static void adjust(Map<Integer, Float> dataAdjusted, int wavelength, Entry<Float, Float> floorEntry, Entry<Float, Float> ceilingEntry) {

		Point p1 = new Point(floorEntry.getKey(), floorEntry.getValue());
		Point p2 = new Point(ceilingEntry.getKey(), ceilingEntry.getValue());
		LinearEquation linearEquation = Equations.createLinearEquation(p1, p2);
		dataAdjusted.put(wavelength, (float)linearEquation.calculateY(wavelength));
	}
}
