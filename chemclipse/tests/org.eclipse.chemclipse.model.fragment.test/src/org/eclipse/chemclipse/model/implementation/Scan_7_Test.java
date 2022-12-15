/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.core.IScan;

import junit.framework.TestCase;

public class Scan_7_Test extends TestCase {

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

		assertEquals(0.0f, scan.getRetentionIndex());
	}

	public void test_2() {

		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.NON_POLAR));
	}

	public void test_3() {

		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.POLAR));
	}

	public void test_4() {

		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.SEMI_POLAR));
	}

	public void test_5() {

		scan.setRetentionIndex(0.1f);
		assertEquals(0.1f, scan.getRetentionIndex());
	}

	public void test_6() {

		scan.setRetentionIndex(SeparationColumnType.NON_POLAR, 0.1f);
		assertEquals(0.1f, scan.getRetentionIndex(SeparationColumnType.NON_POLAR));
	}

	public void test_7() {

		scan.setRetentionIndex(SeparationColumnType.POLAR, 0.1f);
		assertEquals(0.1f, scan.getRetentionIndex(SeparationColumnType.POLAR));
	}

	public void test_8() {

		scan.setRetentionIndex(SeparationColumnType.SEMI_POLAR, 0.1f);
		assertEquals(0.1f, scan.getRetentionIndex(SeparationColumnType.SEMI_POLAR));
	}

	public void test_9() {

		scan.setRetentionIndex(0.1f);
		scan.setRetentionIndex(0.0f);
		assertEquals(0.0f, scan.getRetentionIndex());
	}

	public void test_10() {

		scan.setRetentionIndex(SeparationColumnType.NON_POLAR, 0.1f);
		scan.setRetentionIndex(SeparationColumnType.NON_POLAR, 0.0f);
		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.NON_POLAR));
	}

	public void test_11() {

		scan.setRetentionIndex(SeparationColumnType.POLAR, 0.1f);
		scan.setRetentionIndex(SeparationColumnType.POLAR, 0.0f);
		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.POLAR));
	}

	public void test_12() {

		scan.setRetentionIndex(SeparationColumnType.SEMI_POLAR, 0.1f);
		scan.setRetentionIndex(SeparationColumnType.SEMI_POLAR, 0.0f);
		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.SEMI_POLAR));
	}

	public void test_13() {

		scan.setRetentionIndex(0.1f);
		scan.setRetentionIndex(-0.1f);
		assertEquals(0.0f, scan.getRetentionIndex());
	}

	public void test_14() {

		scan.setRetentionIndex(SeparationColumnType.NON_POLAR, 0.1f);
		scan.setRetentionIndex(SeparationColumnType.NON_POLAR, -0.1f);
		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.NON_POLAR));
	}

	public void test_15() {

		scan.setRetentionIndex(SeparationColumnType.POLAR, 0.1f);
		scan.setRetentionIndex(SeparationColumnType.POLAR, -0.1f);
		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.POLAR));
	}

	public void test_16() {

		scan.setRetentionIndex(SeparationColumnType.SEMI_POLAR, 0.1f);
		scan.setRetentionIndex(SeparationColumnType.SEMI_POLAR, -0.1f);
		assertEquals(0.0f, scan.getRetentionIndex(SeparationColumnType.SEMI_POLAR));
	}
}