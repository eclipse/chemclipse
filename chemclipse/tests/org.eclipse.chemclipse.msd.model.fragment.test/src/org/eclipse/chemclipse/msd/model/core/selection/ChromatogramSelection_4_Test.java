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
package org.eclipse.chemclipse.msd.model.core.selection;

import org.easymock.EasyMock;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

import junit.framework.TestCase;

public class ChromatogramSelection_4_Test extends TestCase {

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

		selection.setStartRetentionTime(-1);
		assertEquals("StartRetentionTime", 4500, selection.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		selection.setStopRetentionTime(25001);
		assertEquals("StopRetentionTime", 25000, selection.getStopRetentionTime());
	}

	public void testGetStartAbundance_1() {

		selection.setStartAbundance(-0.1f);
		assertEquals("StartAbundance", -0.1f, selection.getStartAbundance());
	}

	public void testGetStartAbundance_2() {

		selection.setStartAbundance(Float.NaN);
		assertEquals("StartAbundance", 0.0f, selection.getStartAbundance());
	}

	public void testGetStartAbundance_3() {

		selection.setStartAbundance(Float.POSITIVE_INFINITY);
		assertEquals("StartAbundance", 0.0f, selection.getStartAbundance());
	}

	public void testGetStartAbundance_4() {

		selection.setStartAbundance(Float.NEGATIVE_INFINITY);
		assertEquals("StartAbundance", 0.0f, selection.getStartAbundance());
	}

	public void testGetStopAbundance_1() {

		selection.setStopAbundance(560001.0f);
		assertEquals("StopAbundance", 560001.0f, selection.getStopAbundance());
	}

	public void testGetStopAbundance_2() {

		selection.setStopAbundance(Float.NaN);
		assertEquals("StopAbundance", 560000.0f, selection.getStopAbundance());
	}

	public void testGetStopAbundance_3() {

		selection.setStopAbundance(Float.POSITIVE_INFINITY);
		assertEquals("StopAbundance", 560000.0f, selection.getStopAbundance());
	}

	public void testGetStopAbundance_4() {

		selection.setStopAbundance(Float.NEGATIVE_INFINITY);
		assertEquals("StopAbundance", 560000.0f, selection.getStopAbundance());
	}
}
