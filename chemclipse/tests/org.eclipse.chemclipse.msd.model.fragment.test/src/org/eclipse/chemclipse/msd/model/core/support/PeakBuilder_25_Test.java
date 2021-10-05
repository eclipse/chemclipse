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

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.BackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IBackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

import junit.framework.TestCase;

/**
 * Test the peak exceptions.
 * 
 * @author Philip Wenig
 */
public class PeakBuilder_25_Test extends TestCase {

	private ITotalScanSignals totalIonSignals;
	private ITotalScanSignal totalIonSignal;
	private IScanRange scanRange;
	private IBackgroundAbundanceRange backgroundAbundanceRange;
	private LinearEquation linearEquation;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		List<Float> intensities = new ArrayList<Float>();
		intensities.add(1000.00f);
		intensities.add(5578.14f);
		intensities.add(7596.27f);
		intensities.add(9386.37f);
		intensities.add(5000.0f);
		intensities.add(2709.21f);
		intensities.add(1440.9f);
		intensities.add(810.72f);
		intensities.add(538.22f);
		intensities.add(400.00f);
		scanRange = new ScanRange(1, 10);
		totalIonSignals = new TotalScanSignals(1, 10);
		float abundance = 0.0f;
		for(int scan = 1; scan <= 10; scan++) {
			abundance = intensities.get(scan - 1);
			totalIonSignal = new TotalScanSignal(scan * 10, 0.0f, abundance);
			totalIonSignals.add(totalIonSignal);
		}
		backgroundAbundanceRange = new BackgroundAbundanceRange(1000.0f, 400.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetBackgroundEquation_1() {

		try {
			linearEquation = PeakBuilderMSD.getBackgroundEquation(totalIonSignals, scanRange, backgroundAbundanceRange);
			assertEquals("A", -6.666666666666667, linearEquation.getA());
			assertEquals("B", 1066.6666666666667, linearEquation.getB());
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testGetBackgroundEquation_2() {

		try {
			linearEquation = PeakBuilderMSD.getBackgroundEquation(null, scanRange, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetBackgroundEquation_3() {

		try {
			linearEquation = PeakBuilderMSD.getBackgroundEquation(totalIonSignals, null, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetBackgroundEquation_4() {

		try {
			linearEquation = PeakBuilderMSD.getBackgroundEquation(totalIonSignals, scanRange, null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
