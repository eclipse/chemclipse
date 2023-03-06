/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.settings.MultiplierSettings;
import org.eclipse.core.runtime.NullProgressMonitor;

public class MultiplierFilter_1_Test extends ChromatogramImporterTestCase {

	private IChromatogramFilter<?, ?, ?> chromatogramFilter;
	private MultiplierSettings multiplierFilterSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogramFilter = new MultiplierChromatogramFilter();
		multiplierFilterSettings = new MultiplierSettings();
		multiplierFilterSettings.setMultiplier(0.01f);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogramFilter = null;
		multiplierFilterSettings = null;
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	public void testApplyFilter_1() {

		float totalSignal;
		//
		totalSignal = chromatogram.getScan(1).getTotalSignal();
		assertEquals("scan totalSignal", 67864.0f, totalSignal);
		totalSignal = chromatogram.getScan(5726).getTotalSignal();
		assertEquals("scan totalSignal", 152824.0f, totalSignal);
		totalSignal = chromatogram.getScan(238).getTotalSignal();
		assertEquals("scan totalSignal", 94184.0f, totalSignal);
		totalSignal = chromatogram.getScan(628).getTotalSignal();
		assertEquals("scan totalSignal", 2747568.0f, totalSignal);
		//
		chromatogramFilter.applyFilter(chromatogramSelection, multiplierFilterSettings, new NullProgressMonitor());
		//
		totalSignal = chromatogram.getScan(1).getTotalSignal();
		assertEquals("scan totalSignal", 678.640f, totalSignal, 10E-2);
		totalSignal = chromatogram.getScan(5726).getTotalSignal();
		assertEquals("scan totalSignal", 1528.240f, totalSignal, 10E-2);
		totalSignal = chromatogram.getScan(238).getTotalSignal();
		assertEquals("scan totalSignal", 941.840f, totalSignal, 10E-2);
		totalSignal = chromatogram.getScan(628).getTotalSignal();
		assertEquals("scan totalSignal", 27475.680f, totalSignal, 10E-2);
	}
}
