/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
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

	public static Map<Integer, Float> apply(TreeMap<Double, Float> dataOriginal, int steps) {

		Map<Integer, Float> dataAdjusted = null;
		if(steps >= RasterizeCalculator.MIN_STEPS && steps <= RasterizeCalculator.MAX_STEPS) {
			if(dataOriginal.size() >= 4) {
				/*
				 * Range
				 */
				dataAdjusted = new HashMap<>();
				Entry<Double, Float> firstWavelengthEntry = dataOriginal.firstEntry();
				Entry<Double, Float> lastWavelengthEntry = dataOriginal.lastEntry();
				//
				int startWavelengh = (int)Math.round(firstWavelengthEntry.getKey());
				adjust(dataAdjusted, startWavelengh, firstWavelengthEntry, dataOriginal.ceilingEntry((double)(startWavelengh + 1)));
				//
				int stopWavelengh = (int)Math.round(lastWavelengthEntry.getKey());
				adjust(dataAdjusted, stopWavelengh, dataOriginal.floorEntry((double)(stopWavelengh - 1)), lastWavelengthEntry);
				//
				for(int wavelength = (startWavelengh + 1); wavelength <= (stopWavelengh - 1); wavelength++) {
					if(wavelength % steps == 0) {
						Entry<Double, Float> floorEntry = dataOriginal.floorEntry((double)wavelength);
						Entry<Double, Float> ceilingEntry = dataOriginal.ceilingEntry((double)wavelength);
						adjust(dataAdjusted, wavelength, floorEntry, ceilingEntry);
					}
				}
			}
		}
		//
		return dataAdjusted;
	}

	private static void adjust(Map<Integer, Float> dataAdjusted, int wavelength, Entry<Double, Float> floorEntry, Entry<Double, Float> ceilingEntry) {

		Point p1 = new Point(floorEntry.getKey(), floorEntry.getValue());
		Point p2 = new Point(ceilingEntry.getKey(), ceilingEntry.getValue());
		LinearEquation linearEquation = Equations.createLinearEquation(p1, p2);
		dataAdjusted.put(wavelength, (float)linearEquation.calculateY(wavelength));
	}
}