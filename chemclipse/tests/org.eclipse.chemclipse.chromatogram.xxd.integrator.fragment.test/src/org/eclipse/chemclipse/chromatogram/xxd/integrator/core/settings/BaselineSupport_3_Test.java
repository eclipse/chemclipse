/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings;

import junit.framework.TestCase;

import org.eclipse.chemclipse.model.baseline.BaselineModel;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

public class BaselineSupport_3_Test extends TestCase {

	private IBaselineSupport baselineSupport;
	private IChromatogramMSD chromatogram;
	private IVendorMassSpectrum ms;
	private IBaselineModel baselineModel;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		baselineSupport = new BaselineSupport();
		chromatogram = new ChromatogramMSD();
		chromatogram.setScanDelay(500);
		chromatogram.setScanInterval(1000);
		for(int scan = 1; scan <= 100; scan++) {
			ms = new VendorMassSpectrum();
			chromatogram.addScan(ms);
		}
		chromatogram.recalculateRetentionTimes();
		baselineModel = new BaselineModel(chromatogram);
		baselineModel.addBaseline(500, 99500, 4000.0f, 4000.0f, true);
		baselineSupport.setBaselineModel(baselineModel);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSetBaselineModel_1() {

		assertEquals("BM", 0.0f, baselineModel.getBackgroundAbundance(400));
		assertEquals("BS", 0.0f, baselineSupport.getBackgroundAbundance(400));
	}

	public void testSetBaselineModel_2() {

		assertEquals("BM", 4000.0f, baselineModel.getBackgroundAbundance(500));
		assertEquals("BS", 4000.0f, baselineSupport.getBackgroundAbundance(500));
	}

	public void testSetBaselineModel_3() {

		assertEquals("BM", 4000.0f, baselineModel.getBackgroundAbundance(18500));
		assertEquals("BS", 4000.0f, baselineSupport.getBackgroundAbundance(18500));
	}

	public void testSetBaselineModel_4() {

		assertEquals("BM", 4000.0f, baselineModel.getBackgroundAbundance(99500));
		assertEquals("BS", 4000.0f, baselineSupport.getBackgroundAbundance(99500));
	}

	public void testSetBaselineModel_5() {

		assertEquals("BM", 0.0f, baselineModel.getBackgroundAbundance(100000));
		assertEquals("BS", 0.0f, baselineSupport.getBackgroundAbundance(100000));
	}
}
