/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.io;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.PathResolver;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.TestPathHelper;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

import junit.framework.TestCase;

public class AMDISConverter_1_ITest extends TestCase {

	private ISeparationColumnIndices separationColumnIndices;

	@Override
	protected void setUp() throws Exception {

		File file = new File(PathResolver.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CALIBRATION_CAL_1));
		AMDISConverter converter = new AMDISConverter();
		IProcessingInfo<ISeparationColumnIndices> processingInfo = converter.parseRetentionIndices(file);
		separationColumnIndices = processingInfo.getProcessingResult();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertNotNull(separationColumnIndices);
	}

	public void test2() {

		assertEquals(27, separationColumnIndices.size());
	}

	public void test3() {

		IRetentionIndexEntry entry = separationColumnIndices.firstEntry().getValue();
		assertEquals(51943, entry.getRetentionTime());
		assertEquals(800.0f, entry.getRetentionIndex());
		assertEquals("KW800", entry.getName());
	}

	public void test4() {

		IRetentionIndexEntry entry = separationColumnIndices.get(65239);
		assertEquals(900.0f, entry.getRetentionIndex());
		assertEquals("KW900", entry.getName());
	}

	public void test5() {

		IRetentionIndexEntry entry = separationColumnIndices.get(82126);
		assertEquals(1000.0f, entry.getRetentionIndex());
		assertEquals("KW1000", entry.getName());
	}

	public void test6() {

		IRetentionIndexEntry entry = separationColumnIndices.get(110872);
		assertEquals(1100.0f, entry.getRetentionIndex());
		assertEquals("KW1100", entry.getName());
	}

	public void test7() {

		IRetentionIndexEntry entry = separationColumnIndices.get(155428);
		assertEquals(1200.0f, entry.getRetentionIndex());
		assertEquals("KW1200", entry.getName());
	}

	public void test8() {

		IRetentionIndexEntry entry = separationColumnIndices.get(214714);
		assertEquals(1300.0f, entry.getRetentionIndex());
		assertEquals("KW1300", entry.getName());
	}

	public void test9() {

		IRetentionIndexEntry entry = separationColumnIndices.get(285142);
		assertEquals(1400.0f, entry.getRetentionIndex());
		assertEquals("KW1400", entry.getName());
	}

	public void test10() {

		IRetentionIndexEntry entry = separationColumnIndices.get(358443);
		assertEquals(1500.0f, entry.getRetentionIndex());
		assertEquals("KW1500", entry.getName());
	}

	public void test11() {

		IRetentionIndexEntry entry = separationColumnIndices.get(433899);
		assertEquals(1600.0f, entry.getRetentionIndex());
		assertEquals("KW1600", entry.getName());
	}

	public void test12() {

		IRetentionIndexEntry entry = separationColumnIndices.get(507559);
		assertEquals(1700.0f, entry.getRetentionIndex());
		assertEquals("KW1700", entry.getName());
	}

	public void test13() {

		IRetentionIndexEntry entry = separationColumnIndices.get(580501);
		assertEquals(1800.0f, entry.getRetentionIndex());
		assertEquals("KW1800", entry.getName());
	}

	public void test14() {

		IRetentionIndexEntry entry = separationColumnIndices.get(648054);
		assertEquals(1900.0f, entry.getRetentionIndex());
		assertEquals("KW1900", entry.getName());
	}

	public void test15() {

		IRetentionIndexEntry entry = separationColumnIndices.get(715608);
		assertEquals(2000.0f, entry.getRetentionIndex());
		assertEquals("KW2000", entry.getName());
	}

	public void test16() {

		IRetentionIndexEntry entry = separationColumnIndices.get(777768);
		assertEquals(2100.0f, entry.getRetentionIndex());
		assertEquals("KW2100", entry.getName());
	}

	public void test17() {

		IRetentionIndexEntry entry = separationColumnIndices.get(838854);
		assertEquals(2200.0f, entry.getRetentionIndex());
		assertEquals("KW2200", entry.getName());
	}

	public void test18() {

		IRetentionIndexEntry entry = separationColumnIndices.get(897420);
		assertEquals(2300.0f, entry.getRetentionIndex());
		assertEquals("KW2300", entry.getName());
	}

	public void test19() {

		IRetentionIndexEntry entry = separationColumnIndices.get(953832);
		assertEquals(2400.0f, entry.getRetentionIndex());
		assertEquals("KW2400", entry.getName());
	}

	public void test20() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1008090);
		assertEquals(2500.0f, entry.getRetentionIndex());
		assertEquals("KW2500", entry.getName());
	}

	public void test21() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1060908);
		assertEquals(2600.0f, entry.getRetentionIndex());
		assertEquals("KW2600", entry.getName());
	}

	public void test22() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1111572);
		assertEquals(2700.0f, entry.getRetentionIndex());
		assertEquals("KW2700", entry.getName());
	}

	public void test23() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1161876);
		assertEquals(2800.0f, entry.getRetentionIndex());
		assertEquals("KW2800", entry.getName());
	}

	public void test24() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1221528);
		assertEquals(2900.0f, entry.getRetentionIndex());
		assertEquals("KW2900", entry.getName());
	}

	public void test25() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1294470);
		assertEquals(3000.0f, entry.getRetentionIndex());
		assertEquals("KW3000", entry.getName());
	}

	public void test26() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1386096);
		assertEquals(3100.0f, entry.getRetentionIndex());
		assertEquals("KW3100", entry.getName());
	}

	public void test27() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1502154);
		assertEquals(3200.0f, entry.getRetentionIndex());
		assertEquals("KW3200", entry.getName());
	}

	public void test28() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1649472);
		assertEquals(3300.0f, entry.getRetentionIndex());
		assertEquals("KW3300", entry.getName());
	}

	public void testX() {

		IRetentionIndexEntry entry = separationColumnIndices.lastEntry().getValue();
		assertEquals(1837398, entry.getRetentionTime());
		assertEquals(3400.0f, entry.getRetentionIndex());
		assertEquals("KW3400", entry.getName());
	}
}
