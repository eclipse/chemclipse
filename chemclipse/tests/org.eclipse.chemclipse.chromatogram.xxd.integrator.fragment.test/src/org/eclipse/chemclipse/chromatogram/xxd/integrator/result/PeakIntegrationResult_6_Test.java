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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.result;

import java.util.Set;

import junit.framework.TestCase;

public class PeakIntegrationResult_6_Test extends TestCase {

	private IPeakIntegrationResult result;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		result = new PeakIntegrationResult();
		result.setIntegratedArea(-500325.0d);
		result.addIntegratedIon(55);
		result.setIntegratorType("FirstDerivative");
		result.setModelDescription("TIC");
		result.setPeakType("VV");
		result.setPurity(-0.85f);
		result.setSN(-159.5f);
		result.setStartRetentionTime(-1500);
		result.setStopRetentionTime(-5500);
		result.setTailing(-1.56f);
		result.setWidth(-4000);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetIntegratedArea_1() {

		assertEquals(-500325.0d, result.getIntegratedArea());
	}

	public void testGetIntegratedIons_1() {

		Set<Integer> ions = result.getIntegratedIons();
		assertEquals(1, ions.size());
		assertTrue(ions.contains(55));
		assertFalse(ions.contains(73));
		assertFalse(ions.contains(34));
		assertFalse(ions.contains(48));
	}

	public void testGetIntegratorType_1() {

		assertEquals("FirstDerivative", result.getIntegratorType());
	}

	public void testGetModelDescription_1() {

		assertEquals("TIC", result.getModelDescription());
	}

	public void testGetPeakType_1() {

		assertEquals("VV", result.getPeakType());
	}

	public void testGetPurity_1() {

		assertEquals(-0.85f, result.getPurity());
	}

	public void testGetSN_1() {

		assertEquals(-159.5f, result.getSN());
	}

	public void testGetStartRetentionTime_1() {

		assertEquals(-1500, result.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals(-5500, result.getStopRetentionTime());
	}

	public void testGetTailing_1() {

		assertEquals(-1.56f, result.getTailing());
	}

	public void testGetWidth_1() {

		assertEquals(-4000, result.getWidth());
	}
}
