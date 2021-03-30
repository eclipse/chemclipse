/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.core.internal.support;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsGapFiller;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public class ChromatogramFilterGapFilller_1_Test extends ChromatogramTestCase {

	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD chromatogramSelection;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = getChromatogram();
		chromatogram.removeScans(2, 9);
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test_1() {

		IVendorMassSpectrum scan;
		/*
		 * PRE TESTS
		 */
		assertEquals(2, chromatogram.getNumberOfScans());
		assertEquals(1500, chromatogram.getScanDelay());
		scan = chromatogram.getSupplierScan(1);
		assertEquals(1500, scan.getRetentionTime());
		scan = chromatogram.getSupplierScan(2);
		assertEquals(10500, scan.getRetentionTime());
	}

	public void test_2() {

		IVendorMassSpectrum scan;
		FilterSettingsGapFiller settings = new FilterSettingsGapFiller();
		settings.setLimitFactor(4);
		//
		GapFiller.autofillScans(chromatogramSelection, settings);
		assertEquals(1003, chromatogram.getNumberOfScans());
		assertEquals(1500, chromatogram.getScanDelay());
		scan = chromatogram.getSupplierScan(1);
		assertEquals(1500, scan.getRetentionTime());
		scan = chromatogram.getSupplierScan(10);
		assertEquals(2556, scan.getRetentionTime());
		scan = chromatogram.getSupplierScan(1003);
		assertEquals(10500, scan.getRetentionTime());
	}
}
