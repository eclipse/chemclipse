/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.implementation.Scan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class PenaltyCalculationSupport_7_Test {

	private IScan unknown;
	private IScan reference;

	@BeforeAll
	public void setUp() {

		unknown = new Scan(1000.0f);
		reference = new Scan(1000.0f);
	}

	@Test
	public void test1a() {

		unknown.setRetentionIndex(0.0f);
		reference.setRetentionIndex(2505.5f);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.RETENTION_INDEX);
		penaltyCalculationSettings.setPenaltyWindow(5.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(20.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(27.5f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(27.5f, value, 0);
	}

	@Test
	public void test1b() {

		unknown.setRetentionIndex(0.0f);
		reference.setRetentionIndex(2505.5f);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.RETENTION_INDEX);
		penaltyCalculationSettings.setPenaltyWindow(5.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(20.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(0.0f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(0.0f, value, 0);
	}

	@Test
	public void test2a() {

		unknown.setRetentionIndex(0.0f);
		reference.setRetentionIndex(0.0f);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.RETENTION_INDEX);
		penaltyCalculationSettings.setPenaltyWindow(5.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(20.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(26.2f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(26.2f, value, 0);
	}

	@Test
	public void test2b() {

		unknown.setRetentionIndex(0.0f);
		reference.setRetentionIndex(0.0f);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.RETENTION_INDEX);
		penaltyCalculationSettings.setPenaltyWindow(5.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(20.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(0.0f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(0.0f, value, 0);
	}

	@Test
	public void test3a() {

		unknown.setRetentionTime(0);
		reference.setRetentionTime(3000);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.RETENTION_TIME_MS);
		penaltyCalculationSettings.setPenaltyWindow(500.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(10.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(34.8f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(34.8f, value, 0);
	}

	@Test
	public void test3b() {

		unknown.setRetentionTime(0);
		reference.setRetentionTime(3000);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.RETENTION_TIME_MS);
		penaltyCalculationSettings.setPenaltyWindow(500.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(10.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(0.0f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(0.0f, value, 0);
	}

	@Test
	public void test4a() {

		unknown.setRetentionTime(0);
		reference.setRetentionTime(0);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.RETENTION_TIME_MS);
		penaltyCalculationSettings.setPenaltyWindow(500.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(20.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(32.5f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(32.5f, value, 0);
	}

	@Test
	public void test4b() {

		unknown.setRetentionTime(0);
		reference.setRetentionTime(0);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.RETENTION_TIME_MS);
		penaltyCalculationSettings.setPenaltyWindow(500.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(20.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(0.0f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(0.0f, value, 0);
	}

	@Test
	public void test5a() {

		unknown.setRetentionIndex(2501.2f);
		reference.setRetentionIndex(2505.5f);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.NONE);
		penaltyCalculationSettings.setPenaltyWindow(5.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(20.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(27.5f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(0.0f, value, 0);
	}

	@Test
	public void test5b() {

		unknown.setRetentionTime(7645);
		reference.setRetentionTime(7622);
		IPenaltyCalculationSettings penaltyCalculationSettings = new PenaltyCalculationSettings();
		penaltyCalculationSettings.setPenaltyCalculation(PenaltyCalculation.RETENTION_TIME_MS);
		penaltyCalculationSettings.setPenaltyWindow(500.0f);
		penaltyCalculationSettings.setPenaltyLevelFactor(10.0f);
		penaltyCalculationSettings.setMaxPenalty(20.0f);
		penaltyCalculationSettings.setPenaltyMissingReference(32.5f);
		float value = PenaltyCalculationSupport.calculatePenalty(unknown, reference, penaltyCalculationSettings);
		assertEquals(0.0f, value, 0);
	}
}