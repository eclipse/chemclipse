/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.core.IPeakIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;

import junit.framework.TestCase;

public class PeakModel_11_Test extends TestCase {

	private IPeakModelMSD peakModel;
	private IPeakMassSpectrum peakMaximum;
	private IPeakIon ion;
	private TreeMap<Float, Float> fragmentValues;
	private IPeakIntensityValues intensityValues;
	private TreeMap<Integer, Float> scanValues;
	private float startBackgroundAbundance = 100.0f;
	private float stopBackgroundAbundance = 100.0f;

	protected void setUp() throws Exception {

		super.setUp();
		// ----------------------PeakMaximum
		peakMaximum = new PeakMassSpectrum();
		fragmentValues = new TreeMap<Float, Float>();
		fragmentValues.put(104.0f, 230000.0f);
		fragmentValues.put(103.0f, 58000.0f);
		fragmentValues.put(51.0f, 26000.0f);
		fragmentValues.put(50.0f, 48000.0f);
		fragmentValues.put(78.0f, 23600.0f);
		fragmentValues.put(77.0f, 2500.0f);
		fragmentValues.put(74.0f, 38000.0f);
		fragmentValues.put(105.0f, 97000.0f);
		for(Entry<Float, Float> entry : fragmentValues.entrySet()) {
			ion = new PeakIon(entry.getKey(), entry.getValue());
			peakMaximum.addIon(ion);
		}
		// ----------------------PeakMaximum
		// ----------------------IntensityValues
		intensityValues = new PeakIntensityValues();
		scanValues = new TreeMap<Integer, Float>();
		scanValues.put(4500, 0.0f);
		scanValues.put(5500, 20.0f);
		scanValues.put(6500, 100.0f);
		scanValues.put(7500, 80.0f);
		scanValues.put(8500, 60.0f);
		scanValues.put(9500, 40.0f);
		scanValues.put(10500, 20.0f);
		scanValues.put(11500, 0.0f);
		for(Entry<Integer, Float> entry : scanValues.entrySet()) {
			intensityValues.addIntensityValue(entry.getKey(), entry.getValue());
		}
		// ----------------------IntensityValues
		peakModel = new PeakModelMSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		peakModel.setStrictModel(true);
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

	public void testTailing_1() {

		assertEquals("GetTailing", 4.0f, peakModel.getTailing());
	}
}
