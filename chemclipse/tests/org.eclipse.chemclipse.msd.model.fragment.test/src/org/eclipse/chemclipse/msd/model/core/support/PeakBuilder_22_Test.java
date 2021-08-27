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

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.BackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IBackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;

import junit.framework.TestCase;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_22_Test extends TestCase {

	private ITotalScanSignals totalIonSignals;
	private ITotalScanSignal totalIonSignal;
	private IScanRange scanRange;
	private IBackgroundAbundanceRange backgroundAbundanceRange;
	private IBackgroundAbundanceRange correctedBackground;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		totalIonSignals = new TotalScanSignals(20, 30);
		scanRange = new ScanRange(20, 30);
		for(int scan = 20; scan <= 30; scan++) {
			totalIonSignal = new TotalScanSignal(scan * 10, 0.0f, 1000.0f);
			totalIonSignals.add(totalIonSignal);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCheckBackgroundAbundanceRange_1() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(1500.0f, 1500.0f);
		try {
			correctedBackground = PeakBuilderMSD.checkBackgroundAbundanceRange(totalIonSignals, scanRange, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
		assertEquals("Start", 1000.0f, correctedBackground.getStartBackgroundAbundance());
		assertEquals("Stop", 1000.0f, correctedBackground.getStopBackgroundAbundance());
	}

	public void testCheckBackgroundAbundanceRange_2() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(800.0f, 1500.0f);
		try {
			correctedBackground = PeakBuilderMSD.checkBackgroundAbundanceRange(totalIonSignals, scanRange, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
		assertEquals("Start", 800.0f, correctedBackground.getStartBackgroundAbundance());
		assertEquals("Stop", 1000.0f, correctedBackground.getStopBackgroundAbundance());
	}

	public void testCheckBackgroundAbundanceRange_3() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(1500.0f, 750.0f);
		try {
			correctedBackground = PeakBuilderMSD.checkBackgroundAbundanceRange(totalIonSignals, scanRange, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
		assertEquals("Start", 1000.0f, correctedBackground.getStartBackgroundAbundance());
		assertEquals("Stop", 750.0f, correctedBackground.getStopBackgroundAbundance());
	}

	public void testCheckBackgroundAbundanceRange_4() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(1500.0f, 750.0f);
		try {
			correctedBackground = PeakBuilderMSD.checkBackgroundAbundanceRange(null, scanRange, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCheckBackgroundAbundanceRange_5() {

		backgroundAbundanceRange = new BackgroundAbundanceRange(1500.0f, 750.0f);
		try {
			correctedBackground = PeakBuilderMSD.checkBackgroundAbundanceRange(totalIonSignals, null, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCheckBackgroundAbundanceRange_6() {

		try {
			correctedBackground = PeakBuilderMSD.checkBackgroundAbundanceRange(totalIonSignals, scanRange, null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
