/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import junit.framework.TestCase;

public class PenaltyCalculationSupport_3_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		float unknown = 1397.0f;
		float reference = 1406.0f; // <- RI
		float window = 10.0f;
		float penaltyCalculationLevelFactor = 5.0f;
		float maxValue = 30.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown, reference, window, penaltyCalculationLevelFactor, maxValue);
		assertEquals(0.0f, value); // windowRangeCount is < 1
	}

	public void test2() {

		float unknown = 1397.0f;
		float reference = 1407.0f; // <- RI
		float window = 10.0f;
		float penaltyCalculationLevelFactor = 5.0f;
		float maxValue = 30.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown, reference, window, penaltyCalculationLevelFactor, maxValue);
		assertEquals(0.0f, value); // windowRangeCount is 1
	}

	public void test3() {

		float unknown = 1397.0f;
		float reference = 1408.0f; // <- RI
		float window = 10.0f;
		float penaltyCalculationLevelFactor = 5.0f;
		float maxValue = 30.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown, reference, window, penaltyCalculationLevelFactor, maxValue);
		assertEquals(0.5f, value); // windowRangeCount is 1.1
	}

	public void test4() {

		float unknown = 1397.0f;
		float reference = 1409.0f; // <- RI
		float window = 10.0f;
		float penaltyCalculationLevelFactor = 5.0f;
		float maxValue = 30.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown, reference, window, penaltyCalculationLevelFactor, maxValue);
		assertEquals(1.0f, value); // windowRangeCount is 1.2
	}

	public void test5() {

		float unknown = 1397.0f;
		float reference = 1410.0f; // <- RI
		float window = 10.0f;
		float penaltyCalculationLevelFactor = 5.0f;
		float maxValue = 30.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown, reference, window, penaltyCalculationLevelFactor, maxValue);
		assertEquals(1.5f, value); // windowRangeCount is 1.3
	}
}