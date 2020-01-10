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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings;

import org.easymock.EasyMock;

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.AreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IAreaSupport;

import junit.framework.TestCase;

public class AreaSupport_3_Test extends TestCase {

	private IAreaSupport areaSupport;
	private IChromatogramPeakMSD peak;
	private IPeakModelMSD peakModel;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		areaSupport = new AreaSupport();
		areaSupport.setMinimumArea(4500.0d);
		areaSupport.setAreaSumOn(1500, 5500);
		areaSupport.setAreaSumOn(8500, 10500);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetMinimumArea_1() {

		assertEquals(4500.0d, areaSupport.getMinimumArea());
	}

	public void testIsAreaSumOn_1() {

		assertFalse(areaSupport.isAreaSumOn(1499));
		assertTrue(areaSupport.isAreaSumOn(1500));
		assertTrue(areaSupport.isAreaSumOn(5500));
		assertFalse(areaSupport.isAreaSumOn(5501));
		assertFalse(areaSupport.isAreaSumOn(8499));
		assertTrue(areaSupport.isAreaSumOn(8500));
		assertTrue(areaSupport.isAreaSumOn(10500));
		assertFalse(areaSupport.isAreaSumOn(10501));
	}

	public void testReport_1() {

		/*
		 * Peak model and peak mocks.
		 */
		peakModel = EasyMock.createMock(IPeakModelMSD.class);
		EasyMock.expect(peakModel.getStartRetentionTime()).andStubReturn(4500);
		EasyMock.replay(peakModel);
		peak = EasyMock.createMock(IChromatogramPeakMSD.class);
		EasyMock.expect(peak.getPeakModel()).andStubReturn(peakModel);
		EasyMock.expect(peak.getIntegratedArea()).andStubReturn(4500.0d);
		EasyMock.replay(peak);
		assertTrue(areaSupport.report(peak));
	}
}
