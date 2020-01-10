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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IIntegrationSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IntegrationSupport;

import junit.framework.TestCase;

public class IntegrationSupport_1_Test extends TestCase {

	private IIntegrationSupport integrationSupport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		integrationSupport = new IntegrationSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetMinimumPeakWidth_1() {

		assertEquals(IIntegrationSupport.INITIAL_PEAK_WIDTH, integrationSupport.getMinimumPeakWidth());
	}

	public void testGetMinimumPeakWidth_2() {

		integrationSupport.setMinimumPeakWidth(9600);
		assertEquals(9600, integrationSupport.getMinimumPeakWidth());
	}

	public void testIsIntegratorOff_1() {

		assertFalse(integrationSupport.isIntegratorOff(1500));
	}

	public void testIsIntegratorOff_2() {

		integrationSupport.setIntegratorOff(1500, 2500);
		assertFalse(integrationSupport.isIntegratorOff(1499));
		assertTrue(integrationSupport.isIntegratorOff(1500));
		assertTrue(integrationSupport.isIntegratorOff(2500));
		assertFalse(integrationSupport.isIntegratorOff(2501));
	}

	public void testIsIntegratorOff_3() {

		integrationSupport.setIntegratorOff(1500, 2500);
		integrationSupport.resetIntegratorOff();
		assertFalse(integrationSupport.isIntegratorOff(1499));
		assertFalse(integrationSupport.isIntegratorOff(1500));
		assertFalse(integrationSupport.isIntegratorOff(2500));
		assertFalse(integrationSupport.isIntegratorOff(2501));
	}

	public void testReport_1() {

		assertFalse(integrationSupport.report(null));
	}

	public void testReset() {

		integrationSupport.reset();
		assertEquals(IIntegrationSupport.INITIAL_PEAK_WIDTH, integrationSupport.getMinimumPeakWidth());
		assertFalse(integrationSupport.isIntegratorOff(1500));
	}
}
