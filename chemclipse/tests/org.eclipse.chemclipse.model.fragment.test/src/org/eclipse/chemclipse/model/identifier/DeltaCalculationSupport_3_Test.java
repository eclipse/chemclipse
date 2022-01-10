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

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.implementation.Scan;

import junit.framework.TestCase;

public class DeltaCalculationSupport_3_Test extends TestCase {

	private IScan unknown;
	private IScan reference;
	private IIdentifierSettings identifierSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		unknown = new Scan(1000.0f);
		reference = new Scan(1000.0f);
		identifierSettings = new AbstractIdentifierSettings();
		identifierSettings.setDeltaCalculation(DeltaCalculation.RETENTION_TIME_MS);
		identifierSettings.setDeltaWindow(1000.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		unknown.setRetentionTime(6000);
		unknown.setRetentionIndex(600.0f);
		reference.setRetentionTime(6000);
		reference.setRetentionIndex(600.0f);
		assertTrue(DeltaCalculationSupport.useTarget(unknown, reference, identifierSettings));
	}

	public void test2() {

		unknown.setRetentionTime(6000);
		unknown.setRetentionIndex(600.0f);
		reference.setRetentionTime(5000);
		reference.setRetentionIndex(500.0f);
		assertTrue(DeltaCalculationSupport.useTarget(unknown, reference, identifierSettings));
	}

	public void test3() {

		unknown.setRetentionTime(6000);
		unknown.setRetentionIndex(600.0f);
		reference.setRetentionTime(7000);
		reference.setRetentionIndex(700.0f);
		assertTrue(DeltaCalculationSupport.useTarget(unknown, reference, identifierSettings));
	}

	public void test4() {

		unknown.setRetentionTime(6000);
		unknown.setRetentionIndex(600.0f);
		reference.setRetentionTime(4999);
		reference.setRetentionIndex(499.9f);
		assertFalse(DeltaCalculationSupport.useTarget(unknown, reference, identifierSettings));
	}

	public void test5() {

		unknown.setRetentionTime(6000);
		unknown.setRetentionIndex(600.0f);
		reference.setRetentionTime(7001);
		reference.setRetentionIndex(700.1f);
		assertFalse(DeltaCalculationSupport.useTarget(unknown, reference, identifierSettings));
	}
}