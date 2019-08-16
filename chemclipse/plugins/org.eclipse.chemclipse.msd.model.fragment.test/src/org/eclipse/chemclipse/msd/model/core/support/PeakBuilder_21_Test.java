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
package org.eclipse.chemclipse.msd.model.core.support;

import org.easymock.EasyMock;

import junit.framework.TestCase;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.support.PeakBuilderMSD;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_21_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IScanRange scanRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = EasyMock.createMock(IChromatogramMSD.class);
		EasyMock.expect(chromatogram.getNumberOfScans()).andStubReturn(20);
		EasyMock.replay(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCheckScanRange_1() {

		scanRange = new ScanRange(10, 20);
		try {
			PeakBuilderMSD.checkScanRange(chromatogram, scanRange);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testCheckScanRange_2() {

		scanRange = new ScanRange(0, 20);
		try {
			PeakBuilderMSD.checkScanRange(chromatogram, scanRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCheckScanRange_3() {

		scanRange = new ScanRange(10, 22);
		try {
			PeakBuilderMSD.checkScanRange(chromatogram, scanRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
