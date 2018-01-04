/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.implementation.Peaks;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public class ChromatogramPeaks_3_Test extends ChromatogramPeaksTestCase {

	private IPeaks peaks;
	private IPeakMSD peak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peaks = new Peaks();
		peaks.addPeak(getPeak1());
		peaks.addPeak(getPeak2());
		peaks.addPeak(getPeak3());
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals(3, peaks.size());
	}

	public void testRemovePeak_1() {

		peaks.removePeak(getPeak1());
		assertEquals(2, peaks.size());
		// Peak 2
		peak = (IPeakMSD)peaks.getPeak(1);
		assertTrue(peak.equals(getPeak2()));
		// Peak 3
		peak = (IPeakMSD)peaks.getPeak(2);
		assertTrue(peak.equals(getPeak3()));
	}

	public void testRemovePeak_2() {

		peaks.removePeak(getPeak2());
		assertEquals(2, peaks.size());
		// Peak 1
		peak = (IPeakMSD)peaks.getPeak(1);
		assertTrue(peak.equals(getPeak1()));
		// Peak 3
		peak = (IPeakMSD)peaks.getPeak(2);
		assertTrue(peak.equals(getPeak3()));
	}

	public void testRemovePeak_3() {

		peaks.removePeak(getPeak3());
		assertEquals(2, peaks.size());
		// Peak 1
		peak = (IPeakMSD)peaks.getPeak(1);
		assertTrue(peak.equals(getPeak1()));
		// Peak 2
		peak = (IPeakMSD)peaks.getPeak(2);
		assertTrue(peak.equals(getPeak2()));
	}
}
