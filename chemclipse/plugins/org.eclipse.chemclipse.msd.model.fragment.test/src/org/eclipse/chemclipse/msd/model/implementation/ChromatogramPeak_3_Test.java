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

import org.eclipse.chemclipse.model.core.PeakType;
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
public class ChromatogramPeak_3_Test extends ChromatogramPeakTestCase {

	private IChromatogramPeakMSD peak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peak = new ChromatogramPeakMSD(getPeakModel(), getChromatogram());
	}

	@Override
	protected void tearDown() throws Exception {

		peak = null;
		super.tearDown();
	}

	public void testPeakType_1() {

		assertEquals("PeakType", PeakType.DEFAULT, peak.getPeakType());
	}

	public void testPeakType_2() {

		peak.setPeakType(null);
		assertEquals("PeakType", PeakType.DEFAULT, peak.getPeakType());
	}

	public void testPeakType_3() {

		peak.setPeakType(PeakType.BB);
		assertEquals("PeakType", PeakType.BB, peak.getPeakType());
	}

	public void testPeakType_4() {

		peak.setPeakType(PeakType.DD);
		assertEquals("PeakType", PeakType.DD, peak.getPeakType());
	}

	public void testPeakType_5() {

		peak.setPeakType(PeakType.MM);
		assertEquals("PeakType", PeakType.MM, peak.getPeakType());
	}
}
