/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.core;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings.FilterSettings;
import org.eclipse.core.runtime.NullProgressMonitor;

public class CodaFilter_1_ITest extends ChromatogramImporterTestCase {

	private IChromatogramFilterMSD<?> chromatogramFilter;
	private FilterSettings chromatogramFilterSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogramFilter = new ChromatogramFilter();
		chromatogramFilterSettings = new FilterSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogramFilter = null;
		chromatogramFilterSettings = null;
		//
		System.gc();
		//
		super.tearDown();
	}

	public void testApplyFilter_1() {

		int scan = 1;
		chromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, new NullProgressMonitor());
		assertEquals("total signal", 180262.0f, chromatogram.getScan(scan).getTotalSignal());
	}
}
