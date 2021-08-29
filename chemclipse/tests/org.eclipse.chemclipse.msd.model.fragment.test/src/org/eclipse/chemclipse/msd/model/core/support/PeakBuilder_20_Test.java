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
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignals;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

import junit.framework.TestCase;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_20_Test extends TestCase {

	private IExtractedIonSignals extractedIonSignals;
	private IScanRange scanRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCheckScanRange_1() {

		extractedIonSignals = new ExtractedIonSignals(10, 20);
		scanRange = new ScanRange(10, 20);
		try {
			PeakBuilderMSD.checkScanRange(extractedIonSignals, scanRange);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testCheckScanRange_2() {

		extractedIonSignals = new ExtractedIonSignals(12, 20);
		scanRange = new ScanRange(10, 20);
		try {
			PeakBuilderMSD.checkScanRange(extractedIonSignals, scanRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCheckScanRange_3() {

		extractedIonSignals = new ExtractedIonSignals(10, 20);
		scanRange = new ScanRange(10, 23);
		try {
			PeakBuilderMSD.checkScanRange(extractedIonSignals, scanRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
