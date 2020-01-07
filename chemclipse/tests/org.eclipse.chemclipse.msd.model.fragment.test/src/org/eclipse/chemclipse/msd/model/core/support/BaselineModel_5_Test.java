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
package org.eclipse.chemclipse.msd.model.core.support;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.eclipse.chemclipse.model.baseline.BaselineModel;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

public class BaselineModel_5_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IBaselineModel baselineModel;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = EasyMock.createMock(IChromatogramMSD.class);
		EasyMock.expect(chromatogram.getStartRetentionTime()).andStubReturn(1000);
		EasyMock.expect(chromatogram.getStopRetentionTime()).andStubReturn(100000);
		EasyMock.replay(chromatogram);
		baselineModel = new BaselineModel(chromatogram);
		baselineModel.addBaseline(1500, 2500, 100, 100, true); // I
		baselineModel.addBaseline(3500, 5500, 150, 150, true); // II
		baselineModel.addBaseline(8000, 12000, 550, 50, true); // III
		baselineModel.addBaseline(20000, 30000, 1000, 1000, true); // IV
		baselineModel.addBaseline(35000, 40000, 20, 80, true); // V
		// nothing happens with I
		baselineModel.addBaseline(2700, 3700, 400, 400, true); // VI > II will be
																// cutted at the
																// beginning
		baselineModel.addBaseline(4000, 4500, 200, 200, true); // VII > II will be
																// cutted in two
																// peaces
		baselineModel.addBaseline(5000, 25000, 8000, 8000, true); // VIII > II will be
		// cutted at the
		// end, III will be
		// removed, IV will
		// be cutted at the
		// beginning
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		baselineModel = null;
		super.tearDown();
	}

	public void testChromatogram_1() {

		assertEquals("StartRetentionTime", 1000, chromatogram.getStartRetentionTime());
		assertEquals("StopRetentionTime", 100000, chromatogram.getStopRetentionTime());
	}

	public void testSegmentI_1() {

		assertEquals("BackgroundAbundance", 100.0f, baselineModel.getBackgroundAbundance(1500));
		assertEquals("BackgroundAbundance", 100.0f, baselineModel.getBackgroundAbundance(2500));
	}

	public void testSegmentII_1() {

		assertEquals("BackgroundAbundance", 150.0f, baselineModel.getBackgroundAbundance(3701));
		assertEquals("BackgroundAbundance", 150.0f, baselineModel.getBackgroundAbundance(3999));
		assertEquals("BackgroundAbundance", 150.0f, baselineModel.getBackgroundAbundance(4501));
		assertEquals("BackgroundAbundance", 150.0f, baselineModel.getBackgroundAbundance(4999));
	}

	public void testSegmentIII_1() {

		/*
		 * III is removed and is now replaced by VIII.
		 */
		assertEquals("BackgroundAbundance", 8000.0f, baselineModel.getBackgroundAbundance(8000));
		assertEquals("BackgroundAbundance", 8000.0f, baselineModel.getBackgroundAbundance(12000));
	}

	public void testSegmentIV_1() {

		assertEquals("BackgroundAbundance", 1000.0f, baselineModel.getBackgroundAbundance(25001));
		assertEquals("BackgroundAbundance", 1000.0f, baselineModel.getBackgroundAbundance(30000));
	}

	public void testSegmentV_1() {

		assertEquals("BackgroundAbundance", 20.0f, baselineModel.getBackgroundAbundance(35000));
		assertEquals("BackgroundAbundance", 80.0f, baselineModel.getBackgroundAbundance(40000));
	}

	public void testSegmentVI_1() {

		assertEquals("BackgroundAbundance", 400.0f, baselineModel.getBackgroundAbundance(2700));
		assertEquals("BackgroundAbundance", 400.0f, baselineModel.getBackgroundAbundance(3700));
	}

	public void testSegmentVII_1() {

		assertEquals("BackgroundAbundance", 200.0f, baselineModel.getBackgroundAbundance(4000));
		assertEquals("BackgroundAbundance", 200.0f, baselineModel.getBackgroundAbundance(4500));
	}

	public void testSegmentVIII_1() {

		assertEquals("BackgroundAbundance", 8000.0f, baselineModel.getBackgroundAbundance(5000));
		assertEquals("BackgroundAbundance", 8000.0f, baselineModel.getBackgroundAbundance(25000));
	}
}
