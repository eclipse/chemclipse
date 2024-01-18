/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.implementation.Chromatogram;
import org.eclipse.chemclipse.model.implementation.Scan;

import junit.framework.TestCase;

public class RetentionIndexMap_2_Test extends TestCase {

	private RetentionIndexMap retentionIndexMap = new RetentionIndexMap();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Start
		 * C5 - C54
		 * Stop
		 */
		IChromatogram<?> chromatogram = new Chromatogram();
		for(int i = 1, j = 499; i < 5000; i++, j++) {
			IScan scan = new Scan((float)Math.random());
			scan.setRetentionTime(i * 750);
			scan.setRetentionIndex(j);
			chromatogram.addScan(scan);
		}
		retentionIndexMap.update(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(52, retentionIndexMap.getSeparationColumnIndices().size());
	}

	public void test2() {

		assertEquals(500.0f, retentionIndexMap.getRetentionIndex(1500)); // C5
	}

	public void test3() {

		assertEquals(1500, retentionIndexMap.getRetentionTime(500)); // C5
	}

	public void test4() {

		assertEquals(3600.0f, retentionIndexMap.getRetentionIndex(2326500)); // C36
	}

	public void test5() {

		assertEquals(2326500, retentionIndexMap.getRetentionTime(3600)); // C36
	}

	public void test6() {

		assertEquals(5400.0f, retentionIndexMap.getRetentionIndex(3676500)); // C54
	}

	public void test7() {

		assertEquals(3676500, retentionIndexMap.getRetentionTime(5400)); // C54
	}

	public void test8() {

		assertFalse(retentionIndexMap.isEmpty());
	}

	public void test9() {

		assertEquals(499, retentionIndexMap.getRetentionIndexStart());
	}

	public void test10() {

		assertEquals(5497, retentionIndexMap.getRetentionIndexStop());
	}
}