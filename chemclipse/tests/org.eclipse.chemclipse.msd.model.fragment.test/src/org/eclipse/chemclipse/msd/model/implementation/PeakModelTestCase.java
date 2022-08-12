/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.core.IPeakIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class PeakModelTestCase extends TestCase {

	protected IPeakModelMSD peakModel;
	protected IPeakMassSpectrum peakMaximum;
	protected IPeakIon ion;
	protected TreeMap<Float, Float> fragmentValues;
	protected IPeakIntensityValues intensityValues;
	protected TreeMap<Integer, Float> scanValues;
	protected float startBackgroundAbundance = 0.0f;
	protected float stopBackgroundAbundance = 0.0f;

	protected void setUp(float startBackgroundAbundance, float stopBackgroundAbundance, IPeakMassSpectrum peakMaximum) throws Exception {

		super.setUp();
		this.peakMaximum = peakMaximum;
		// ----------------------IntensityValues
		intensityValues = new PeakIntensityValues();
		scanValues = new TreeMap<Integer, Float>();
		scanValues.put(1500, 0.0f);
		scanValues.put(2500, 5.0f);
		scanValues.put(3500, 10.0f);
		scanValues.put(4500, 15.0f);
		scanValues.put(5500, 20.0f);
		scanValues.put(6500, 30.0f);
		scanValues.put(7500, 46.0f);
		scanValues.put(8500, 82.0f);
		scanValues.put(9500, 100.0f);
		scanValues.put(10500, 86.0f);
		scanValues.put(11500, 64.0f);
		scanValues.put(12500, 43.0f);
		scanValues.put(13500, 30.0f);
		scanValues.put(14500, 15.0f);
		scanValues.put(15500, 4.0f);
		for(Entry<Integer, Float> entry : scanValues.entrySet()) {
			intensityValues.addIntensityValue(entry.getKey(), entry.getValue());
		}
		// ----------------------IntensityValues
		// ----------------------BackgroundValues
		this.startBackgroundAbundance = startBackgroundAbundance;
		this.stopBackgroundAbundance = stopBackgroundAbundance;
		// ----------------------BackgroundValues
		peakModel = new PeakModelMSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
	}

	@Override
	protected void tearDown() throws Exception {

		peakModel = null;
		peakMaximum = null;
		ion = null;
		fragmentValues = null;
		intensityValues = null;
		scanValues = null;
		super.tearDown();
	}
}
