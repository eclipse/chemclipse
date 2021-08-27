/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter;

import java.io.File;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.TestPathHelper;

import junit.framework.TestCase;

public class TargetsReader_1_ITest extends TestCase {

	private Map<String, String> compoundsMap;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		TargetsReader targetsReader = new TargetsReader();
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_TARGETS_1));
		compoundsMap = targetsReader.getCompoundCasMap(file);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(4, compoundsMap.size());
	}

	public void test2() {

		assertEquals("000112-40-3", compoundsMap.get("Dodekan"));
	}

	public void test3() {

		assertEquals("000092-52-4", compoundsMap.get("Bisphenyl"));
	}

	public void test4() {

		assertEquals("002051-62-9", compoundsMap.get("4-Chlorobiphenyl"));
	}

	public void test5() {

		assertEquals("000112-39-0", compoundsMap.get("Methylpalmiate"));
	}
}
