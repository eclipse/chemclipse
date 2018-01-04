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

import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;

import junit.framework.TestCase;

/**
 * Test the peak intensity values.<br/>
 * Make sure that the limit IPeakIntensityValues.MAX_INTENSITY is implemented
 * correctly.
 * 
 * @author eselmeister
 */
public class PeakIntensityValues_2_Test extends TestCase {

	private PeakIntensityValues intensityValues;
	private TreeMap<Integer, Float> scanValues;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		intensityValues = new PeakIntensityValues();
		scanValues = new TreeMap<Integer, Float>();
		scanValues.put(1500, -6.0f);
		scanValues.put(2500, 5.0f);
		scanValues.put(3500, 10.0f);
		scanValues.put(4500, 15.0f);
		scanValues.put(5500, 20.0f);
		scanValues.put(6500, 30.0f);
		scanValues.put(7500, 46.0f);
		scanValues.put(8500, 82.0f);
		scanValues.put(9500, 140.0f);
		scanValues.put(10500, 150.0f);
		scanValues.put(11500, 101.0f);
		scanValues.put(12500, 43.0f);
		scanValues.put(13500, 30.0f);
		scanValues.put(14500, 15.0f);
		scanValues.put(15500, -1.0f);
		for(Entry<Integer, Float> entry : scanValues.entrySet()) {
			intensityValues.addIntensityValue(entry.getKey(), entry.getValue());
		}
	}

	@Override
	protected void tearDown() throws Exception {

		intensityValues = null;
		scanValues = null;
		super.tearDown();
	}

	public void testGetHighestIntensityValue_1() {

		Entry<Integer, Float> entry = intensityValues.getHighestIntensityValue();
		assertNull("Entry<Integer, Float> must be null.", entry);
	}

	public void testGetIntensityValue_1() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(8500);
		assertNotNull("Entry<Integer, Float> must not be null.", entry);
		int retentionTime = entry.getKey();
		float intensity = entry.getValue();
		assertEquals("retentionTime", 8500, retentionTime);
		assertEquals("intensity", 82.0f, intensity);
	}

	public void testGetIntensityValue_2() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(2600);
		assertNotNull("Entry<Integer, Float> must not be null.", entry);
		int retentionTime = entry.getKey();
		float intensity = entry.getValue();
		assertEquals("retentionTime", 2500, retentionTime);
		assertEquals("intensity", 5.0f, intensity);
	}

	public void testGetIntensityValue_3() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(1200);
		assertNull("Entry<Integer, Float> must be null.", entry);
	}

	public void testGetIntensityValue_4() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(11700);
		assertNotNull("Entry<Integer, Float> must not be null.", entry);
		int retentionTime = entry.getKey();
		float intensity = entry.getValue();
		assertEquals("retentionTime", 8500, retentionTime);
		assertEquals("intensity", 82.0f, intensity);
	}

	public void testGetIntensityValue_5() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(14500);
		assertNotNull("Entry<Integer, Float> must not be null.", entry);
		int retentionTime = entry.getKey();
		float intensity = entry.getValue();
		assertEquals("retentionTime", 14500, retentionTime);
		assertEquals("intensity", 15.0f, intensity);
	}

	public void testGetIntensityValue_6() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(15600);
		assertNull("Entry<Integer, Float> must be null.", entry);
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("StartRetentionTime", 2500, intensityValues.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("StopRetentionTime", 14500, intensityValues.getStopRetentionTime());
	}
}
