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

import junit.framework.TestCase;

/**
 * Test the peak intensity values.<br/>
 * Make sure that the limit IPeakIntensityValues.MAX_INTENSITY is implemented
 * correctly.
 * 
 * @author eselmeister
 */
public class PeakIntensityValues_5_Test extends TestCase {

	private PeakIntensityValues intensityValues;
	private TreeMap<Integer, Float> scanValues;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		intensityValues = new PeakIntensityValues();
		scanValues = new TreeMap<Integer, Float>();
		scanValues.put(10500, IPeakIntensityValues.MAX_INTENSITY);
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
		assertNotNull("Entry<Integer, Float> must not be null.", entry);
		int retentionTime = entry.getKey();
		float intensity = entry.getValue();
		assertEquals("retentionTime", 10500, retentionTime);
		assertEquals("intensity", IPeakIntensityValues.MAX_INTENSITY, intensity);
	}

	public void testGetIntensityValue_1() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(8500);
		assertNull("Entry<Integer, Float> must be null.", entry);
	}

	public void testGetIntensityValue_2() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(2600);
		assertNull("Entry<Integer, Float> must be null.", entry);
	}

	public void testGetIntensityValue_3() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(1200);
		assertNull("Entry<Integer, Float> must be null.", entry);
	}

	public void testGetIntensityValue_4() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(11700);
		assertNull("Entry<Integer, Float> must be null.", entry);
	}

	public void testGetIntensityValue_5() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(14500);
		assertNull("Entry<Integer, Float> must be null.", entry);
	}

	public void testGetIntensityValue_6() {

		Entry<Integer, Float> entry = intensityValues.getIntensityValue(15600);
		assertNull("Entry<Integer, Float> must be null.", entry);
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("StartRetentionTime", 10500, intensityValues.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("StopRetentionTime", 10500, intensityValues.getStopRetentionTime());
	}
}
