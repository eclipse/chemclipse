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

import static org.junit.Assert.assertNotEquals;

import junit.framework.TestCase;

public class PeakIntegrationResult_1_Test extends TestCase {

	private IPeakIntegrationResult result1;
	private IPeakIntegrationResult result2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		result1 = new PeakIntegrationResult();
		result2 = new PeakIntegrationResult();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testEquals_1() {

		assertEquals("equals", result1, result2);
	}

	public void testEquals_2() {

		assertEquals("equals", result2, result1);
	}

	public void testEquals_3() {

		assertEquals("equals", result1, result1);
	}

	public void testEquals_4() {

		assertNotNull("equals", result1);
	}

	public void testEquals_5() {

		assertNotEquals("equals", result2, new Object());
	}
}
