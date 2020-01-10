/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import junit.framework.TestCase;

import org.eclipse.chemclipse.model.core.IScan;

public class Chromatogram_3_Test extends TestCase {

	private Chromatogram chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new Chromatogram();
		chromatogram.addScan(new Scan(1000.0f));
		chromatogram.addScan(new Scan(1000.0f));
		chromatogram.addScan(new Scan(1000.0f));
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testChromatogram_1() {

		assertFalse(chromatogram.containsScanCycles());
	}

	public void testChromatogram_2() {

		IScan scan = new Scan(1000.0f);
		scan.setCycleNumber(1);
		chromatogram.addScan(scan);
		assertFalse(chromatogram.containsScanCycles());
	}

	public void testChromatogram_3() {

		IScan scan = new Scan(1000.0f);
		scan.setCycleNumber(0);
		chromatogram.addScan(scan);
		assertTrue(chromatogram.containsScanCycles());
	}

	public void testChromatogram_4() {

		IScan scan = new Scan(1000.0f);
		scan.setCycleNumber(2);
		chromatogram.addScan(scan);
		assertTrue(chromatogram.containsScanCycles());
	}
}
