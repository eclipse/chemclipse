/*******************************************************************************
 * Copyright (c) 2016 Dr. Philip Wenig.
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

public class PenaltyCalculationSupport_2_Test extends TestCase {

	private IScanMSD unknown;
	private IScanMSD reference;
	int retentionTimeWindow;
	float penaltyCalculationLevelFactor;
	float penaltyCalculationMaxValue;
	private PenaltyCalculationSupport penaltyCalculationSupport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		unknown = new ScanMSD();
		reference = new ScanMSD();
		penaltyCalculationSupport = new PenaltyCalculationSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		unknown = null;
		reference = null;
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test2() {

		unknown = null;
		reference.setRetentionTime(3000);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test3() {

		unknown.setRetentionTime(2500);
		reference = null;
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test4() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3000);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test5() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3001);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.019999743f, value);
	}

	public void test6() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3999);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(19.98f, value);
	}

	public void test7() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(4000);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(20.0f, value);
	}

	public void test8() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(4001);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(20.0f, value);
	}

	public void test9() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3999);
		retentionTimeWindow = 0;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test10() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3999);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = -0.1f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test11() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3999);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 100.1f;
		float value = penaltyCalculationSupport.calculatePenaltyFromRetentionTime(unknown, reference, retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}
}
