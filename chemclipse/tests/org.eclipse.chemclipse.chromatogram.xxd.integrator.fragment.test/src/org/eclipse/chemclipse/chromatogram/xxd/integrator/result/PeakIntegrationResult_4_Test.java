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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.result;

import junit.framework.TestCase;

public class PeakIntegrationResult_4_Test extends TestCase {

	private IPeakIntegrationResult result;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		result = new PeakIntegrationResult();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetIntegratedArea_1() {

		assertEquals(0.0d, result.getIntegratedArea());
	}

	public void testGetIntegratedIons_1() {

		assertEquals(0, result.getIntegratedTraces().size());
	}

	public void testGetIntegratorType_1() {

		assertEquals("", result.getIntegratorType());
	}

	public void testGetModelDescription_1() {

		assertEquals("", result.getModelDescription());
	}

	public void testGetPeakType_1() {

		assertEquals("", result.getPeakType());
	}

	public void testGetPurity_1() {

		assertEquals(0.0f, result.getPurity());
	}

	public void testGetSN_1() {

		assertEquals(0.0f, result.getSN());
	}

	public void testGetStartRetentionTime_1() {

		assertEquals(0, result.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals(0, result.getStopRetentionTime());
	}

	public void testGetTailing_1() {

		assertEquals(0.0f, result.getTailing());
	}

	public void testGetWidth_1() {

		assertEquals(0, result.getWidth());
	}
}
