/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
		assertEquals(137400, entry.getRetentionTime());
		assertEquals(800.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test4() {

		IRetentionIndexEntry entry = separationColumnIndices.get(150000);
		assertEquals(900.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test5() {

		IRetentionIndexEntry entry = separationColumnIndices.get(194400);
		assertEquals(1000.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test6() {

		IRetentionIndexEntry entry = separationColumnIndices.get(249600);
		assertEquals(1100.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test7() {

		IRetentionIndexEntry entry = separationColumnIndices.get(314400);
		assertEquals(1200.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test8() {

		IRetentionIndexEntry entry = separationColumnIndices.get(385200);
		assertEquals(1300.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test9() {

		IRetentionIndexEntry entry = separationColumnIndices.get(483000);
		assertEquals(1400.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test10() {

		IRetentionIndexEntry entry = separationColumnIndices.get(556800);
		assertEquals(1500.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test11() {

		IRetentionIndexEntry entry = separationColumnIndices.get(629400);
		assertEquals(1600.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test12() {

		IRetentionIndexEntry entry = separationColumnIndices.get(724800);
		assertEquals(1700.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test13() {

		IRetentionIndexEntry entry = separationColumnIndices.get(794400);
		assertEquals(1800.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test14() {

		IRetentionIndexEntry entry = separationColumnIndices.get(861000);
		assertEquals(1900.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test15() {

		IRetentionIndexEntry entry = separationColumnIndices.get(927000);
		assertEquals(2000.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test16() {

		IRetentionIndexEntry entry = separationColumnIndices.get(983400);
		assertEquals(2100.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test17() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1053000);
		assertEquals(2200.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test18() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1113000);
		assertEquals(2300.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test19() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1175400);
		assertEquals(2400.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test20() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1267200);
		assertEquals(2500.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test21() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1343400);
		assertEquals(2600.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test22() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1455600);
		assertEquals(2700.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test23() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1584600);
		assertEquals(2800.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test24() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1758600);
		assertEquals(2900.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test25() {

		IRetentionIndexEntry entry = separationColumnIndices.get(1984800);
		assertEquals(3000.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void test26() {

		IRetentionIndexEntry entry = separationColumnIndices.get(2248800);
		assertEquals(3100.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}

	public void testX() {

		IRetentionIndexEntry entry = separationColumnIndices.lastEntry().getValue();
		assertEquals(2610600, entry.getRetentionTime());
		assertEquals(3200.0f, entry.getRetentionIndex());
		assertEquals("", entry.getName());
	}
}
