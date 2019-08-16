/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.quantitation;

import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;

import junit.framework.TestCase;

public class QuantitationCompound_5_Test extends TestCase {

	private QuantitationCompound quantitationCompound;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationCompound = new QuantitationCompound("", "", 0);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertNotNull(quantitationCompound);
	}

	public void test2() {

		assertEquals("", quantitationCompound.getName());
	}

	public void test3() {

		assertEquals("", quantitationCompound.getChemicalClass());
	}

	public void test4() {

		assertEquals(0, quantitationCompound.getRetentionTimeWindow().getRetentionTime());
	}

	public void test5() {

		assertEquals(0.0f, quantitationCompound.getRetentionTimeWindow().getAllowedNegativeDeviation());
	}

	public void test6() {

		assertEquals(0.0f, quantitationCompound.getRetentionTimeWindow().getAllowedPositiveDeviation());
	}

	public void test7() {

		assertEquals(0.0f, quantitationCompound.getRetentionIndexWindow().getRetentionIndex());
	}

	public void test8() {

		assertEquals(0.0f, quantitationCompound.getRetentionIndexWindow().getAllowedNegativeDeviation());
	}

	public void test9() {

		assertEquals(0.0f, quantitationCompound.getRetentionIndexWindow().getAllowedPositiveDeviation());
	}

	public void test10() {

		assertEquals("", quantitationCompound.getConcentrationUnit());
	}

	public void test11() {

		assertTrue(quantitationCompound.isUseTIC());
	}

	public void test12() {

		assertEquals(CalibrationMethod.LINEAR, quantitationCompound.getCalibrationMethod());
	}

	public void test13() {

		assertTrue(quantitationCompound.isCrossZero());
	}

	public void test14() {

		assertEquals(0, quantitationCompound.getQuantitationSignals().size());
	}

	public void test15() {

		assertEquals(0, quantitationCompound.getResponseSignals().size());
	}

	public void test16() {

		assertEquals(0, quantitationCompound.getQuantitationPeaks().size());
	}
}
