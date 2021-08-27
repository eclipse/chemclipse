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

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

import junit.framework.TestCase;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_26_Test extends TestCase {

	private ITotalScanSignals totalIonSignals;
	private ITotalScanSignals totalIonSignalsModified;
	private ITotalScanSignal totalIonSignal;
	private LinearEquation backgroundEquation;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		List<Float> intensities = new ArrayList<Float>();
		intensities.add(1000.0f);
		intensities.add(5578.14f);
		intensities.add(7596.27f);
		intensities.add(9386.37f);
		intensities.add(5000.0f);
		intensities.add(2709.21f);
		intensities.add(1440.9f);
		intensities.add(810.72f);
		intensities.add(538.22f);
		intensities.add(400.00f);
		totalIonSignals = new TotalScanSignals(1, 10);
		float abundance = 0.0f;
		for(int scan = 1; scan <= 10; scan++) {
			abundance = intensities.get(scan - 1);
			totalIonSignal = new TotalScanSignal(scan * 10, 0.0f, abundance);
			totalIonSignals.add(totalIonSignal);
		}
		IPoint p1 = new Point(10, 1000);
		IPoint p2 = new Point(100, 400);
		backgroundEquation = Equations.createLinearEquation(p1, p2);
	}

	@Override
	protected void tearDown() throws Exception {

		totalIonSignal = null;
		totalIonSignals = null;
		totalIonSignalsModified = null;
		backgroundEquation = null;
		super.tearDown();
	}

	public void testAdjustTotalIonSignals_1() {

		try {
			totalIonSignalsModified = PeakBuilderMSD.adjustTotalIonSignals(totalIonSignals, backgroundEquation);
			assertNotSame(totalIonSignalsModified, totalIonSignals);
			totalIonSignal = totalIonSignalsModified.getTotalScanSignal(1);
			assertEquals("TotalSignal", 0.0f, totalIonSignal.getTotalSignal());
			totalIonSignal = totalIonSignalsModified.getTotalScanSignal(4);
			assertEquals("TotalSignal", IPeakIntensityValues.MAX_INTENSITY, totalIonSignal.getTotalSignal());
			totalIonSignal = totalIonSignalsModified.getTotalScanSignal(10);
			assertEquals("TotalSignal", 0.0f, totalIonSignal.getTotalSignal());
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testAdjustTotalIonSignals_2() {

		try {
			totalIonSignalsModified = PeakBuilderMSD.adjustTotalIonSignals(null, backgroundEquation);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testAdjustTotalIonSignals_3() {

		try {
			totalIonSignalsModified = PeakBuilderMSD.adjustTotalIonSignals(totalIonSignals, null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
