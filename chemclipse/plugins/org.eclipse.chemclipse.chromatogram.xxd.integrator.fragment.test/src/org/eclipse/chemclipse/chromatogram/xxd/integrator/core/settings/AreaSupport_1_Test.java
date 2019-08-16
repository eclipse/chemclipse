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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.AreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IAreaSupport;

import junit.framework.TestCase;

public class AreaSupport_1_Test extends TestCase {

	private IAreaSupport areaSupport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		areaSupport = new AreaSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetMinimumArea_1() {

		assertEquals(0.0d, areaSupport.getMinimumArea());
	}

	public void testIsAreaSumOn_1() {

		assertFalse(areaSupport.isAreaSumOn(1500));
	}

	public void testReport_1() {

		assertFalse(areaSupport.report(null));
	}

	public void testReset_1() {

		areaSupport.reset();
		assertEquals(IAreaSupport.INITIAL_AREA_REJECT, areaSupport.getMinimumArea());
		assertFalse(areaSupport.isAreaSumOn(1500));
	}

	public void testResetAreaSumOn_1() {

		areaSupport.resetAreaSumOn();
		assertFalse(areaSupport.isAreaSumOn(1500));
	}

	public void testSetAreaSumOn_1() {

		areaSupport.setAreaSumOn(1500, 2500);
		assertFalse(areaSupport.isAreaSumOn(1499));
		assertTrue(areaSupport.isAreaSumOn(1500));
		assertTrue(areaSupport.isAreaSumOn(2500));
		assertFalse(areaSupport.isAreaSumOn(2501));
	}

	public void testSetMinimumArea_1() {

		areaSupport.setMinimumArea(1522);
		assertEquals(1522.0d, areaSupport.getMinimumArea());
	}
}
