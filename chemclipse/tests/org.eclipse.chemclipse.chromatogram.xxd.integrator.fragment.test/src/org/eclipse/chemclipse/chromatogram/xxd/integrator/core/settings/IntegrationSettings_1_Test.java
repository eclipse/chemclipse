/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;

import junit.framework.TestCase;

public class IntegrationSettings_1_Test extends TestCase {

	private IPeakIntegrationSettings settings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		settings = new PeakIntegrationSettings();
		// settings.addReportDecider(null);
		// settings.removeReportDecider(null);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetAreaSupport_1() {

		assertNotNull(settings.getAreaSupport());
	}

	public void testGetBaselineSupport_1() {

		assertNotNull(settings.getBaselineSupport());
	}

	public void testGetIntegrationSupport_1() {

		assertNotNull(settings.getIntegrationSupport());
	}

	public void testGetSelectedIons_1() {

		assertNotNull(settings.getMarkedTraces());
	}

	public void testGetSettingStatus_1() {

		/*
		 * Peak model and peak mocks.
		 */
		IPeakModelMSD peakModel = EasyMock.createMock(IPeakModelMSD.class);
		EasyMock.expect(peakModel.getStartRetentionTime()).andStubReturn(4500);
		EasyMock.expect(peakModel.getWidthByInflectionPoints()).andStubReturn(1500);
		EasyMock.replay(peakModel);
		IChromatogramPeakMSD peak = EasyMock.createMock(IChromatogramPeakMSD.class);
		EasyMock.expect(peak.getPeakModel()).andStubReturn(peakModel);
		EasyMock.expect(peak.getIntegratedArea()).andStubReturn(4500.0d);
		EasyMock.expect(peak.getSignalToNoiseRatio()).andStubReturn(11.0f);
		EasyMock.replay(peak);
		assertNotNull(settings.getSettingStatus(peak));
	}
}
