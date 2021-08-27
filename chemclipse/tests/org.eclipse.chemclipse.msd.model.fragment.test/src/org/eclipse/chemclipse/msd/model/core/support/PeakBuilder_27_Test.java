/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;

import junit.framework.TestCase;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_27_Test extends TestCase {

	private ITotalScanSignals totalIonSignals;
	private ITotalScanSignal totalIonSignal;
	private IPeakIntensityValues peakIntensityValues;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		List<Float> intensities = new ArrayList<Float>();
		intensities.add(0.0f);
		intensities.add(54.1f);
		intensities.add(78.38f);
		intensities.add(100.0f);
		intensities.add(49.69f);
		intensities.add(23.79f);
		intensities.add(9.8f);
		intensities.add(3.23f);
		intensities.add(0.84f);
		intensities.add(0.0f);
		totalIonSignals = new TotalScanSignals(1, 10);
		float abundance = 0.0f;
		for(int scan = 1; scan <= 10; scan++) {
			abundance = intensities.get(scan - 1);
			totalIonSignal = new TotalScanSignal(scan * 10, 0.0f, abundance);
			totalIonSignals.add(totalIonSignal);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		totalIonSignal = null;
		totalIonSignals = null;
		super.tearDown();
	}

	public void testGetPeakIntensityValues_1() {

		Map.Entry<Integer, Float> value;
		try {
			peakIntensityValues = PeakBuilderMSD.getPeakIntensityValues(totalIonSignals);
			assertNotNull(peakIntensityValues);
			value = peakIntensityValues.getIntensityValue(10);
			assertEquals("Intensity", 0.0f, value.getValue());
			value = peakIntensityValues.getIntensityValue(40);
			assertEquals("Intensity", IPeakIntensityValues.MAX_INTENSITY, value.getValue());
			value = peakIntensityValues.getIntensityValue(100);
			assertEquals("Intensity", 0.0f, value.getValue());
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testGetPeakIntensityValues_2() {

		try {
			peakIntensityValues = PeakBuilderMSD.getPeakIntensityValues(null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
