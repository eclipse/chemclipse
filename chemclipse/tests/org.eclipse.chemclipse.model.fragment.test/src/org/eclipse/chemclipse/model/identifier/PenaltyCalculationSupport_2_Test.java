/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.implementation.Scan;

import junit.framework.TestCase;

public class PenaltyCalculationSupport_2_Test extends TestCase {

	private IScan unknown;
	private IScan reference;
	private int retentionTimeWindow;
	private float penaltyCalculationLevelFactor;
	private float penaltyCalculationMaxValue;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		unknown = new Scan(1000.0f);
		reference = new Scan(1000.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test4() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3000);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown.getRetentionTime(), reference.getRetentionTime(), retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test5() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3001);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown.getRetentionTime(), reference.getRetentionTime(), retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.02f, value);
	}

	public void test6() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3999);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown.getRetentionTime(), reference.getRetentionTime(), retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(19.98f, value);
	}

	public void test7() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(4000);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown.getRetentionTime(), reference.getRetentionTime(), retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(20.0f, value);
	}

	public void test8() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(4001);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown.getRetentionTime(), reference.getRetentionTime(), retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(20.0f, value);
	}

	public void test9() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3999);
		retentionTimeWindow = 0;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 20.0f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown.getRetentionTime(), reference.getRetentionTime(), retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test10() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3999);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = -0.1f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown.getRetentionTime(), reference.getRetentionTime(), retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}

	public void test11() {

		unknown.setRetentionTime(2500);
		reference.setRetentionTime(3999);
		retentionTimeWindow = 500;
		penaltyCalculationLevelFactor = 10.0f;
		penaltyCalculationMaxValue = 100.1f;
		float value = (float)PenaltyCalculationSupport.calculatePenalty(unknown.getRetentionTime(), reference.getRetentionTime(), retentionTimeWindow, penaltyCalculationLevelFactor, penaltyCalculationMaxValue);
		assertEquals(0.0f, value);
	}
}
