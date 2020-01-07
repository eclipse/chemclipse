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

public class PeakModel_7_Test extends TestCase {

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
		scanValues.put(8500, 0.0f);
		scanValues.put(9500, 100.0f);
		scanValues.put(10500, 0.0f);
		for(Entry<Integer, Float> entry : scanValues.entrySet()) {
			intensityValues.addIntensityValue(entry.getKey(), entry.getValue());
		}
		/*
		 * 10000 - 9000 50% 9700 - 9300 80% 9500 retention time max by
		 * inflection point equations.
		 */
		// ----------------------IntensityValues
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

		assertEquals("GetBackgroundAbundance", 100.0f, peakModel.getBackgroundAbundance(8500));
	}

	public void testGetBackgroundAbundance_2() {

		assertEquals("GetBackgroundAbundance", 100.0f, peakModel.getBackgroundAbundance(9500));
	}

	public void testGetBackgroundAbundance_3() {

		assertEquals("GetBackgroundAbundance", 100.0f, peakModel.getBackgroundAbundance(10500));
	}

	public void testGetBackgroundAbundance_4() {

		assertEquals("GetBackgroundAbundance", 100.0f, peakModel.getBackgroundAbundance());
	}

	public void testGetPeakAbundance_1() {

		assertEquals("GetPeakAbundance", 523100.00f, peakModel.getPeakAbundance());
	}

	public void testGetPeakAbundanceByInflectionPoints_1() {

		assertEquals("GetPeakAbundanceByInflectionPoints", 523100.0f, peakModel.getPeakAbundanceByInflectionPoints());
	}

	public void testGetWidthBaselineTotal_1() {

		assertEquals("GetWidthBaselineTotal", 2001, peakModel.getWidthBaselineTotal());
	}

	public void testGetWidthBaselineByInflectionPoints_1() {

		assertEquals("GetWidthBaselineByInflectionPoints", 2001, peakModel.getWidthBaselineByInflectionPoints());
	}

	public void testGetWidthByInflectionPoints_1() {

		assertEquals("GetWidthByInflectionPoints", 1001, peakModel.getWidthByInflectionPoints());
	}

	public void testGetWidthByInflectionPoints_2() {

		assertEquals("GetWidthByInflectionPoints 50%", 1001, peakModel.getWidthByInflectionPoints(0.5f));
	}

	public void testGetWidthByInflectionPoints_3() {

		assertEquals("GetWidthByInflectionPoints 80%", 401, peakModel.getWidthByInflectionPoints(0.8f));
	}

	public void testGetWidthByInflectionPoints_4() {

		assertEquals("GetWidthByInflectionPoints -10%", 0, peakModel.getWidthByInflectionPoints(-0.1f));
	}

	public void testGetWidthByInflectionPoints_5() {

		assertEquals("GetWidthByInflectionPoints 110%", 0, peakModel.getWidthByInflectionPoints(1.1f));
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("GetStartRetentionTime", 8500, peakModel.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("GetStopRetentionTime", 10500, peakModel.getStopRetentionTime());
	}

	public void testGetRetentionTimeAtPeakMaximum_1() {

		assertEquals("GetRetentionTimeAtPeakMaximum", 9500, peakModel.getRetentionTimeAtPeakMaximum());
	}

	public void testGetRetentionTimeAtPeakMaximumByInflectionPoints_1() {

		assertEquals("GetRetentionTimeAtPeakMaximumByInflectionPoints", 9500, peakModel.getRetentionTimeAtPeakMaximumByInflectionPoints());
	}

	public void testGradientAngle_1() {

		assertEquals("GradientAngle", 0.0d, peakModel.getGradientAngle());
	}

	public void testGetIncreasingInflectionPointAbundance_1() {

		assertEquals("GetIncreasingInflectionPointAbundance", 0.0f, peakModel.getIncreasingInflectionPointAbundance(8500));
	}

	public void testGetIncreasingInflectionPointAbundance_2() {

		assertEquals("GetIncreasingInflectionPointAbundance", 523100.0f, peakModel.getIncreasingInflectionPointAbundance(9500));
	}

	public void testGetIncreasingInflectionPointAbundance_3() {

		assertEquals("GetIncreasingInflectionPointAbundance", 1046200.0f, peakModel.getIncreasingInflectionPointAbundance(10500));
	}

	public void testGetDecreasingInflectionPointAbundance_1() {

		assertEquals("GetDecreasingInflectionPointAbundance", 1046200.0f, peakModel.getDecreasingInflectionPointAbundance(8500));
	}

	public void testGetDecreasingInflectionPointAbundance_2() {

		assertEquals("GetDecreasingInflectionPointAbundance", 523100.0f, peakModel.getDecreasingInflectionPointAbundance(9500));
	}

	public void testGetDecreasingInflectionPointAbundance_3() {

		assertEquals("GetDecreasingInflectionPointAbundance", 0.0f, peakModel.getDecreasingInflectionPointAbundance(10500));
	}
}
