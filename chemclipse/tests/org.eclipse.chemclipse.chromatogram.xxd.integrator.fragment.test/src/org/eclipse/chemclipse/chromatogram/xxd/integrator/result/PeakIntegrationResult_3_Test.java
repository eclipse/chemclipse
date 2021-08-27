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

import junit.framework.TestCase;

public class PeakIntegrationResult_3_Test extends TestCase {

	private IPeakIntegrationResult result1;
	private IPeakIntegrationResult result2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testEquals_1() {

		result1 = new PeakIntegrationResult();
		result2 = new PeakIntegrationResult();
		assertEquals("equals", result1.hashCode(), result2.hashCode());
	}

	public void testEquals_2() {

		result1 = new PeakIntegrationResult();
		result1.setStartRetentionTime(1500);
		result2 = new PeakIntegrationResult();
		assertFalse("equals", result1.hashCode() == result2.hashCode());
	}
}
