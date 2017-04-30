/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Janos Binder - adjusted test cases
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.unitsumnormalizer.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.unitsumnormalizer.core.ChromatogramFilter;
import org.eclipse.core.runtime.NullProgressMonitor;

public class UnitSumNormalizerFilter_1_Test extends ChromatogramImporterTestCase {

	private IChromatogramFilter chromatogramFilter;
	private IChromatogramFilterSettings chromatogramFilterSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogramFilter = new ChromatogramFilter();
		chromatogramFilterSettings = new ChromatogramFilterSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogramFilter = null;
		chromatogramFilterSettings = null;
		super.tearDown();
	}

	public void testApplyFilter_1() {

		float totalSignal;
		totalSignal = chromatogram.getScan(1).getTotalSignal();
		assertEquals("scan totalSignal", 67864.0f, totalSignal);
		totalSignal = chromatogram.getScan(5726).getTotalSignal();
		assertEquals("scan totalSignal", 153220.0f, totalSignal);
		totalSignal = chromatogram.getScan(238).getTotalSignal();
		assertEquals("scan totalSignal", 94184.0f, totalSignal);
		totalSignal = chromatogram.getScan(628).getTotalSignal();
		assertEquals("scan totalSignal", 2747568.0f, totalSignal);
		chromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, new NullProgressMonitor());
		totalSignal = chromatogram.getScan(1).getTotalSignal(); // Total signal intensity 1.024242304E9 - with this number, we divide the values above
		assertEquals("scan totalSignal", 6.625776E-5f, totalSignal);
		totalSignal = chromatogram.getScan(5726).getTotalSignal();
		assertEquals("scan totalSignal", 1.495935E-4f, totalSignal);
		totalSignal = chromatogram.getScan(238).getTotalSignal();
		assertEquals("scan totalSignal", 9.1954804E-5f, totalSignal);
		totalSignal = chromatogram.getScan(628).getTotalSignal();
		assertEquals("scan totalSignal", 2.682537E-3f, totalSignal);
	}
}
