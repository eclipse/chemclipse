/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import static org.junit.Assert.assertThrows;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.core.IPeakIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;

import junit.framework.TestCase;

public class PeakModel_2_Test extends TestCase {

	private IPeakModelMSD peakModel;
	private IPeakMassSpectrum peakMaximum;
	private IPeakIon ion;
	private TreeMap<Float, Float> fragmentValues;
	private IPeakIntensityValues intensityValues;
	private TreeMap<Integer, Float> scanValues;
	private float startBackgroundAbundance = 0.0f;
	private float stopBackgroundAbundance = 0.0f;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		// ----------------------PeakMaximum
		peakMaximum = new PeakMassSpectrum();
		fragmentValues = new TreeMap<Float, Float>();
		fragmentValues.put(104.0f, 2300.0f);
		fragmentValues.put(103.0f, 580.0f);
		fragmentValues.put(51.0f, 260.0f);
		fragmentValues.put(50.0f, 480.0f);
		fragmentValues.put(78.0f, 236.0f);
		fragmentValues.put(77.0f, 25.0f);
		fragmentValues.put(74.0f, 380.0f);
		fragmentValues.put(105.0f, 970.0f);
		for(Entry<Float, Float> entry : fragmentValues.entrySet()) {
			ion = new PeakIon(entry.getKey(), entry.getValue());
			peakMaximum.addIon(ion);
		}
		// ----------------------PeakMaximum
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
		startBackgroundAbundance = 360.0f;
		stopBackgroundAbundance = 420.5f;
		// ----------------------BackgroundValues
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

	public void testConstruct_1() {

		peakModel = new PeakModelMSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		assertNotNull("The construction was fine.", peakModel);
	}

	public void testConstruct_2() {

		assertThrows(IllegalArgumentException.class, () -> {
			peakModel = new PeakModelMSD(null, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		});
	}

	public void testConstruct_3() {

		assertThrows(IllegalArgumentException.class, () -> {
			peakModel = new PeakModelMSD(peakMaximum, null, startBackgroundAbundance, stopBackgroundAbundance);
		});
	}

	public void testConstruct_4() {

		assertThrows(IllegalArgumentException.class, () -> {
			peakModel = new PeakModelMSD(null, null, startBackgroundAbundance, stopBackgroundAbundance);
		});
	}

	public void testConstruct_5() {

		peakModel = new PeakModelMSD(peakMaximum, intensityValues, 0, stopBackgroundAbundance);
		assertNotNull("The construction was fine.", peakModel);
	}

	public void testConstruct_6() {

		peakModel = new PeakModelMSD(peakMaximum, intensityValues, startBackgroundAbundance, 0);
		assertNotNull("The construction was fine.", peakModel);
	}

	public void testConstruct_7() {

		peakModel = new PeakModelMSD(peakMaximum, intensityValues, 0, 0);
		assertNotNull("The construction was fine.", peakModel);
	}
}
