/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.support;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;

import junit.framework.TestCase;

public class PenaltyCalculationSupport_1_Test extends TestCase {

	private IScanMSD unknown;
	private IScanMSD reference;
	float retentionIndexWindow;
	float penaltyCalculationLevelFactor;
	float penaltyCalculationMaxValue;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		unknown = new ScanMSD();
		reference = new ScanMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		unknown = null;
		reference = null;
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test2() {

		unknown = null;
		reference.setRetentionIndex(2505.5f);
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test3() {

		unknown.setRetentionIndex(2500.5f);
		reference = null;
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test4() {

		unknown.setRetentionIndex(2500.5f);
		reference.setRetentionIndex(2505.5f);
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test5() {

		unknown.setRetentionIndex(2500.5f);
		reference.setRetentionIndex(2506.5f);
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(2.0000005f, value);
	}

	public void test6() {

		unknown.setRetentionIndex(2500.5f);
		reference.setRetentionIndex(2515.4f);
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(19.799805f, value);
	}

	public void test7() {

		unknown.setRetentionIndex(2500.5f);
		reference.setRetentionIndex(2515.5f);
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(20.0f, value);
	}

	public void test8() {

		unknown.setRetentionIndex(2500.5f);
		reference.setRetentionIndex(2515.6f);
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(20.0f, value);
	}

	public void test9() {

		unknown.setRetentionIndex(2500.5f);
		reference.setRetentionIndex(2515.4f);
		retentionIndexWindow = 0.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test10() {

		unknown.setRetentionIndex(2500.5f);
		reference.setRetentionIndex(2515.4f);
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = -0.1f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test11() {

		unknown.setRetentionIndex(2500.5f);
		reference.setRetentionIndex(2515.4f);
		retentionIndexWindow = 5.0f;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 100.1f;
		float value = PenaltyCalculationSupport.calculatePenaltyFromRetentionIndex(unknown, reference, retentionIndexWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}
}
