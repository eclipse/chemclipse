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

import java.util.List;
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
public class PeakIntensityValues_6_Test extends TestCase {

	private PeakIntensityValues intensityValues;
	private TreeMap<Integer, Float> scanValues;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
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
	}

	@Override
	protected void tearDown() throws Exception {

		intensityValues = null;
		scanValues = null;
		super.tearDown();
	}

	public void testGetRetentionTimes_1() {

		int rt;
		List<Integer> retentionTimes = intensityValues.getRetentionTimes();
		rt = retentionTimes.get(0);
		assertEquals("retentionTime", 1500, rt);
		rt = retentionTimes.get(4);
		assertEquals("retentionTime", 5500, rt);
		rt = retentionTimes.get(7);
		assertEquals("retentionTime", 8500, rt);
		rt = retentionTimes.get(10);
		assertEquals("retentionTime", 11500, rt);
		rt = retentionTimes.get(14);
		assertEquals("retentionTime", 15500, rt);
	}
}
