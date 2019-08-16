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

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;

/**
 * The chromatogram and peak will be initialized in DefaultPeakTestCase.<br/>
 * The peak has 15 scans, starting at a retention time of 1500 milliseconds (ms)
 * and ends at a retention time of 15500 ms.<br/>
 * The chromatogram has 17 scans, starting at a retention time of 500 ms and
 * ends at a retention time of 16500 ms. It has a background of 1750 units.
 * 
 * @author eselmeister
 */
public class ChromatogramPeak_2_Test extends ChromatogramPeakTestCase {

	private IChromatogramPeakMSD peak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		peak = null;
		super.tearDown();
	}

	public void testConstructor_1() {

		try {
			peak = new ChromatogramPeakMSD(getPeakModel(), getChromatogram());
		} catch(IllegalArgumentException e) {
			assertFalse("A IllegalArgumentException should not be thrown here.", true);
		} catch(PeakException e) {
			assertFalse("A PeakException should not be thrown here.", true);
		}
		assertNotNull(peak);
	}

	public void testConstructor_2() {

		try {
			peak = new ChromatogramPeakMSD(null, getChromatogram());
		} catch(IllegalArgumentException e) {
			assertTrue("IllegalArgumentException", true);
		} catch(PeakException e) {
			assertFalse("A PeakException should not be thrown here.", true);
		}
		assertNull(peak);
	}

	public void testConstructor_3() {

		try {
			peak = new ChromatogramPeakMSD(getPeakModel(), null);
		} catch(IllegalArgumentException e) {
			assertTrue("IllegalArgumentException", true);
		} catch(PeakException e) {
			assertFalse("A PeakException should not be thrown here.", true);
		}
		assertNull(peak);
	}

	public void testConstructor_4() {

		try {
			peak = new ChromatogramPeakMSD(null, null);
		} catch(IllegalArgumentException e) {
			assertTrue("IllegalArgumentException", true);
		} catch(PeakException e) {
			assertFalse("A PeakException should not be thrown here.", true);
		}
		assertNull(peak);
	}
}
