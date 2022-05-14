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
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		IChromatogram<?> chromatogram = new Chromatogram();
		for(int i = 1, j = 499; i < 5000; i++, j++) {
			IScan scan = new Scan((float)Math.random());
			scan.setRetentionTime(i * 750);
			scan.setRetentionIndex(j);
			chromatogram.addScan(scan);
		}
		retentionIndexMap.update(chromatogram);
		//
		assertEquals(51, retentionIndexMap.getSeparationColumnIndices().size());
		assertEquals(500.0f, retentionIndexMap.getRetentionIndex(1500)); // C5
		assertEquals(1500, retentionIndexMap.getRetentionTime(500));
		assertEquals(5200.0f, retentionIndexMap.getRetentionIndex(3526500)); // C52
		assertEquals(3526500, retentionIndexMap.getRetentionTime(5200));
	}

	public void test2() {

		IChromatogram<?> chromatogram = new Chromatogram();
		for(int i = 1, j = 499; i < 5000; i++, j++) {
			IScan scan = new Scan((float)Math.random());
			scan.setRetentionTime(i * 750);
			scan.setRetentionIndex(j);
			if(j % 100 != 0) {
				chromatogram.addScan(scan);
			}
		}
		retentionIndexMap.update(chromatogram);
		//
		assertEquals(51, retentionIndexMap.getSeparationColumnIndices().size());
		assertEquals(500.0f, retentionIndexMap.getRetentionIndex(1500)); // C5
		assertEquals(1500, retentionIndexMap.getRetentionTime(500));
		assertEquals(5200.0f, retentionIndexMap.getRetentionIndex(3526500)); // C52
		assertEquals(3526500, retentionIndexMap.getRetentionTime(5200));
	}

	public void test3() {

		IChromatogram<?> chromatogram = new Chromatogram();
		for(int i = 1, j = 499; i < 5000; i++, j++) {
			IScan scan = new Scan((float)Math.random());
			scan.setRetentionTime(i * 750);
			scan.setRetentionIndex(j);
			if(j % 100 == 0) {
				chromatogram.addScan(scan);
			}
		}
		retentionIndexMap.update(chromatogram);
		//
		assertEquals(49, retentionIndexMap.getSeparationColumnIndices().size()); // Start missing and C53 not calculated.
		assertEquals(500.0f, retentionIndexMap.getRetentionIndex(1500)); // C5
		assertEquals(1500, retentionIndexMap.getRetentionTime(500));
		assertEquals(5200.0f, retentionIndexMap.getRetentionIndex(3526500)); // C52
		assertEquals(3526500, retentionIndexMap.getRetentionTime(5200));
	}
}