/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.calculator.TestPathHelper;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

import junit.framework.TestCase;

public class MassLibConverter_1_ITest extends TestCase {

	private ISeparationColumnIndices separationColumnIndices;

	@Override
	protected void setUp() throws Exception {

		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CALIBRATION_INF_1));
		MassLibConverter converter = new MassLibConverter();
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

		assertEquals(25, separationColumnIndices.size());
	}

	public void test3() {

		IRetentionIndexEntry entry = separationColumnIndices.firstEntry().getValue();
		assertEquals(149000, entry.getRetentionTime());
		assertEquals(800.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test4() {

		IRetentionIndexEntry entry = separationColumnIndices.get(170000);
		assertEquals(900.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test5() {

		IRetentionIndexEntry entry = separationColumnIndices.get(204000);
		assertEquals(1000.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test6() {

		IRetentionIndexEntry entry = separationColumnIndices.get(256000);
		assertEquals(1100.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test7() {

		IRetentionIndexEntry entry = separationColumnIndices.get(324000);
		assertEquals(1200.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test8() {

		IRetentionIndexEntry entry = separationColumnIndices.get(402000);
		assertEquals(1300.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test9() {

		IRetentionIndexEntry entry = separationColumnIndices.get(485000);
		assertEquals(1400.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test10() {

		IRetentionIndexEntry entry = separationColumnIndices.get(568000);
		assertEquals(1500.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test11() {

		IRetentionIndexEntry entry = separationColumnIndices.get(649000);
		assertEquals(1600.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test12() {

		IRetentionIndexEntry entry = separationColumnIndices.get(728000);
		assertEquals(1700.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test13() {

		IRetentionIndexEntry entry = separationColumnIndices.get(804000);
		assertEquals(1800.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test14() {

		IRetentionIndexEntry entry = separationColumnIndices.get(875000);
		assertEquals(1900.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test15() {

		IRetentionIndexEntry entry = separationColumnIndices.get(945000);
		assertEquals(2000.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test16() {

		IRetentionIndexEntry entry = separationColumnIndices.get(999000);
		assertEquals(2100.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test17() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1075000);
		assertEquals(2200.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test18() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1135000);
		assertEquals(2300.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test19() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1199000);
		assertEquals(2400.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test20() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1272000);
		assertEquals(2500.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test21() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1359000);
		assertEquals(2600.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test22() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1466000);
		assertEquals(2700.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test23() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1601000);
		assertEquals(2800.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test24() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1771000);
		assertEquals(2900.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test25() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1988000);
		assertEquals(3000.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test26() {

		IRetentionIndexEntry entry = separationColumnIndices.get(2268000);
		assertEquals(3100.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void testX() {

		IRetentionIndexEntry entry = separationColumnIndices.lastEntry().getValue();
		assertEquals(2631000, entry.getRetentionTime());
		assertEquals(3200.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}
}
