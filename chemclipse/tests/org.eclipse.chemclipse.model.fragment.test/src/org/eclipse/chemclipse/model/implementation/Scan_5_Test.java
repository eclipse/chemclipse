/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.core.IScan;

import junit.framework.TestCase;

public class Scan_5_Test extends TestCase {

	private IScan scan;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		scan = new Scan(1000.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		scan = null;
		super.tearDown();
	}

	public void test_1() {

		assertEquals(false, scan.hasAdditionalRetentionIndices());
	}

	public void test_2() {

		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.POLAR));
	}

	public void test_3() {

		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.SEMI_POLAR));
	}

	public void test_4() {

		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.NON_POLAR));
	}

	public void test_5() {

		scan.setRetentionIndex(SeparationColumnType.POLAR, 3456.34f);
		assertEquals(true, scan.hasAdditionalRetentionIndices());
		assertEquals(3456.34f, scan.getRetentionIndex(SeparationColumnType.POLAR));
	}

	public void test_6() {

		scan.setRetentionIndex(SeparationColumnType.SEMI_POLAR, 2358.22f);
		assertEquals(true, scan.hasAdditionalRetentionIndices());
		assertEquals(2358.22f, scan.getRetentionIndex(SeparationColumnType.SEMI_POLAR));
	}

	public void test_7() {

		scan.setRetentionIndex(SeparationColumnType.NON_POLAR, 789.11f);
		assertEquals(true, scan.hasAdditionalRetentionIndices());
		assertEquals(789.11f, scan.getRetentionIndex(SeparationColumnType.NON_POLAR));
	}

	public void test_8() {

		scan.setRetentionIndex(SeparationColumnType.POLAR, 3456.34f);
		scan.setRetentionIndex(SeparationColumnType.SEMI_POLAR, 2358.22f);
		scan.setRetentionIndex(SeparationColumnType.NON_POLAR, 789.11f);
		assertEquals(true, scan.hasAdditionalRetentionIndices());
		assertEquals(3456.34f, scan.getRetentionIndex(SeparationColumnType.POLAR));
		assertEquals(2358.22f, scan.getRetentionIndex(SeparationColumnType.SEMI_POLAR));
		assertEquals(789.11f, scan.getRetentionIndex(SeparationColumnType.NON_POLAR));
	}
}