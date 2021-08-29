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
package org.eclipse.chemclipse.msd.model.core.selection;

import org.easymock.EasyMock;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

import junit.framework.TestCase;

public class ChromatogramSelection_3_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD selection;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Use createNiceMock if you use void methods that are not important to
		 * test.
		 */
		chromatogram = EasyMock.createNiceMock(IChromatogramMSD.class);
		EasyMock.expect(chromatogram.getStartRetentionTime()).andStubReturn(4500);
		EasyMock.expect(chromatogram.getStopRetentionTime()).andStubReturn(25000);
		EasyMock.expect(chromatogram.getMaxSignal()).andStubReturn(560000.0f);
		EasyMock.replay(chromatogram);
		/*
		 * Default values from IChromatogram will be chosen.
		 */
		selection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		selection = null;
		super.tearDown();
	}

	public void testChromatogram_1() {

		assertEquals("StartRetentionTime", 4500, chromatogram.getStartRetentionTime());
		assertEquals("StopRetentionTime", 25000, chromatogram.getStopRetentionTime());
		assertEquals("MaxSignal", 560000.0f, chromatogram.getMaxSignal());
	}

	public void testGetChromatogram_1() {

		assertNotNull(selection.getChromatogram());
	}

	public void testGetStartRetentionTime_1() {

		selection.setStartRetentionTime(6500);
		selection.reset();
		assertEquals("StartRetentionTime", 4500, selection.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		selection.setStopRetentionTime(24000);
		selection.reset();
		assertEquals("StopRetentionTime", 25000, selection.getStopRetentionTime());
	}

	public void testGetStartAbundance_1() {

		selection.setStartAbundance(0.5f);
		selection.reset();
		assertEquals("StartAbundance", 0.0f, selection.getStartAbundance());
	}

	public void testGetStopAbundance_1() {

		selection.setStopAbundance(250000.0f);
		selection.reset();
		assertEquals("StopAbundance", 560000.0f, selection.getStopAbundance());
	}
}
