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

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IIntegrationSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IntegrationSupport;

import org.easymock.EasyMock;

import junit.framework.TestCase;

public class IntegrationSupport_2_Test extends TestCase {

	private IIntegrationSupport integrationSupport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		integrationSupport = new IntegrationSupport();
		integrationSupport.setMinimumPeakWidth(5000);
		integrationSupport.setIntegratorOff(1500, 2500);
		integrationSupport.setMinimumSignalToNoiseRatio(10.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetMinimumPeakWidth_1() {

		assertEquals(5000, integrationSupport.getMinimumPeakWidth());
	}

	public void testIsIntegratorOff_1() {

		assertFalse(integrationSupport.isIntegratorOff(1499));
		assertTrue(integrationSupport.isIntegratorOff(1500));
		assertTrue(integrationSupport.isIntegratorOff(2500));
		assertFalse(integrationSupport.isIntegratorOff(2501));
	}

	public void testReport_1() {

		/*
		 * Peak model and peak mocks.
		 */
		IPeakModelMSD peakModel = EasyMock.createMock(IPeakModelMSD.class);
		EasyMock.expect(peakModel.getStartRetentionTime()).andStubReturn(1499);
		EasyMock.expect(peakModel.getWidthByInflectionPoints()).andStubReturn(5000);
		EasyMock.replay(peakModel);
		IChromatogramPeakMSD peak = EasyMock.createMock(IChromatogramPeakMSD.class);
		EasyMock.expect(peak.getPeakModel()).andStubReturn(peakModel);
		EasyMock.expect(peak.getIntegratedArea()).andStubReturn(4500.0d);
		EasyMock.expect(peak.getSignalToNoiseRatio()).andStubReturn(11.0f);
		EasyMock.replay(peak);
		/*
		 * StartRetentionTime integrator on
		 */
		assertTrue(integrationSupport.report(peak));
	}

	public void testReport_2() {

		/*
		 * Peak model and peak mocks.
		 */
		IPeakModelMSD peakModel = EasyMock.createMock(IPeakModelMSD.class);
		EasyMock.expect(peakModel.getStartRetentionTime()).andStubReturn(1500);
		EasyMock.expect(peakModel.getWidthByInflectionPoints()).andStubReturn(5000);
		EasyMock.replay(peakModel);
		IChromatogramPeakMSD peak = EasyMock.createMock(IChromatogramPeakMSD.class);
		EasyMock.expect(peak.getPeakModel()).andStubReturn(peakModel);
		EasyMock.expect(peak.getIntegratedArea()).andStubReturn(4500.0d);
		EasyMock.expect(peak.getSignalToNoiseRatio()).andStubReturn(11.0f);
		EasyMock.replay(peak);
		/*
		 * StartRetentionTime integrator off
		 */
		assertFalse(integrationSupport.report(peak));
	}

	public void testReport_3() {

		/*
		 * Peak model and peak mocks.
		 */
		IPeakModelMSD peakModel = EasyMock.createMock(IPeakModelMSD.class);
		EasyMock.expect(peakModel.getStartRetentionTime()).andStubReturn(2500);
		EasyMock.expect(peakModel.getWidthByInflectionPoints()).andStubReturn(5000);
		EasyMock.replay(peakModel);
		IChromatogramPeakMSD peak = EasyMock.createMock(IChromatogramPeakMSD.class);
		EasyMock.expect(peak.getPeakModel()).andStubReturn(peakModel);
		EasyMock.expect(peak.getIntegratedArea()).andStubReturn(4500.0d);
		EasyMock.expect(peak.getSignalToNoiseRatio()).andStubReturn(11.0f);
		EasyMock.replay(peak);
		/*
		 * StartRetentionTime integrator off
		 */
		assertFalse(integrationSupport.report(peak));
	}

	public void testReport_4() {

		/*
		 * Peak model and peak mocks.
		 */
		IPeakModelMSD peakModel = EasyMock.createMock(IPeakModelMSD.class);
		EasyMock.expect(peakModel.getStartRetentionTime()).andStubReturn(2501);
		EasyMock.expect(peakModel.getWidthByInflectionPoints()).andStubReturn(5000);
		EasyMock.replay(peakModel);
		IChromatogramPeakMSD peak = EasyMock.createMock(IChromatogramPeakMSD.class);
		EasyMock.expect(peak.getPeakModel()).andStubReturn(peakModel);
		EasyMock.expect(peak.getIntegratedArea()).andStubReturn(4500.0d);
		EasyMock.expect(peak.getSignalToNoiseRatio()).andStubReturn(11.0f);
		EasyMock.replay(peak);
		/*
		 * StartRetentionTime integrator on
		 */
		assertTrue(integrationSupport.report(peak));
	}

	public void testReport_5() {

		/*
		 * Peak model and peak mocks.
		 */
		IPeakModelMSD peakModel = EasyMock.createMock(IPeakModelMSD.class);
		EasyMock.expect(peakModel.getStartRetentionTime()).andStubReturn(2501);
		EasyMock.expect(peakModel.getWidthByInflectionPoints()).andStubReturn(4999);
		EasyMock.replay(peakModel);
		IChromatogramPeakMSD peak = EasyMock.createMock(IChromatogramPeakMSD.class);
		EasyMock.expect(peak.getPeakModel()).andStubReturn(peakModel);
		EasyMock.expect(peak.getIntegratedArea()).andStubReturn(4500.0d);
		EasyMock.expect(peak.getSignalToNoiseRatio()).andStubReturn(11.0f);
		EasyMock.replay(peak);
		/*
		 * getWidthByInflectionPoints integrator off
		 */
		assertFalse(integrationSupport.report(peak));
	}
}
