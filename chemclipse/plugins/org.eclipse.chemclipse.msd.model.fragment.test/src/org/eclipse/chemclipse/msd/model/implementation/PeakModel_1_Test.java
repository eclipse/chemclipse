/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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

import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.core.IPeakIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;

import junit.framework.TestCase;

public class PeakModel_1_Test extends TestCase {

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

	public void testGetBackgroundAbundance_1() {

		assertEquals("BackgroundAbundance", 394.57144f, peakModel.getBackgroundAbundance());
	}

	public void testGetBackgroundAbundance_2() {

		assertEquals("BackgroundAbundance", 374.05762f, peakModel.getBackgroundAbundance(4753));
	}

	public void testGetBackgroundAbundance_3() {

		assertEquals("BackgroundAbundance", 407.89438f, peakModel.getBackgroundAbundance(12583));
	}

	public void testGetBackgroundAbundance_4() {

		assertEquals("BackgroundAbundance", 420.28394f, peakModel.getBackgroundAbundance(15450));
	}

	public void testGetBackgroundAbundance_5() {

		assertEquals("BackgroundAbundance", 360.0f, peakModel.getBackgroundAbundance(1500));
	}

	public void testGetBackgroundAbundance_6() {

		assertEquals("BackgroundAbundance", 420.5f, peakModel.getBackgroundAbundance(15500));
	}

	public void testGetBackgroundAbundance_7() {

		assertEquals("BackgroundAbundance", 365.61786f, peakModel.getBackgroundAbundance(2800));
	}

	public void testGetPeakAbundance_1() {

		assertEquals("PeakAbundance", 5231.0f, peakModel.getPeakAbundance());
	}

	public void testGetPeakAbundance_2() {

		assertEquals("PeakAbundance", 5231.0f, peakModel.getPeakAbundance(9500));
	}

	public void testGetPeakAbundance_3() {

		assertEquals("PeakAbundance", 3347.84f, peakModel.getPeakAbundance(11500));
	}

	public void testGetPeakAbundance_4() {

		assertEquals("PeakAbundance", 209.24f, peakModel.getPeakAbundance(15500));
	}

	public void testGetPeakAbundance_5() {

		assertEquals("PeakAbundance", 0.0f, peakModel.getPeakAbundance(1500));
	}

	public void testGetPeakAbundance_6() {

		assertEquals("PeakAbundance", 2249.33f, peakModel.getPeakAbundance(12500));
	}

	public void testGetPeakAbundance_7() {

		assertEquals("PeakAbundance", 5231.0f, peakModel.getPeakAbundance(10000));
	}

	public void testGetPeakAbundance_8() {

		assertEquals("PeakAbundance", 261.55f, peakModel.getPeakAbundance(3000));
	}

	public void testGetPeakAbundance_9() {

		assertEquals("PeakAbundance", 1569.3f, peakModel.getPeakAbundance(14000));
	}

	public void testGetPeakAbundance_10() {

		assertEquals("PeakAbundance", 0.0f, peakModel.getPeakAbundance(15501));
	}

	public void testGetPeakAbundance_11() {

		assertEquals("PeakAbundance", 0.0f, peakModel.getPeakAbundance(1499));
	}

	public void testGetPeakMassSpectrum_1() {

		assertNotNull("PeakMassSpectrum must not be null", peakModel.getPeakMassSpectrum());
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("StartRetentionTime", 1500, peakModel.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("StopRetentionTime", 15500, peakModel.getStopRetentionTime());
	}

	public void testGetWidthBaselineTotal_1() {

		assertEquals("WidthInMilliseconds", 14001, peakModel.getWidthBaselineTotal());
	}

	public void testGetRetentionTimeAtPeakMaximum_1() {

		assertEquals("RetentionTimeAtPeakMaximum", 9500, peakModel.getRetentionTimeAtPeakMaximum());
	}

	public void testGetPeakMassSpectrum_2() {

		IPeakMassSpectrum massSpectrum = peakModel.getPeakMassSpectrum();
		assertEquals("RetentionTimeAtPeakMaximum", 9500, massSpectrum.getRetentionTime());
	}
}
