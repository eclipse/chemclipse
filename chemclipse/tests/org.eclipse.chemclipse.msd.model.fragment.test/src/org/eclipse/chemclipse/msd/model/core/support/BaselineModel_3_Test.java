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

public class BaselineModel_3_Test extends TestCase {

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
		baselineModel.addBaseline(500, 50000, 100, 100, true);
		baselineModel.addBaseline(90000, 150000, 200, 200, true);
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

	public void testBaseline_1() {

		assertEquals("BackgroundAbundance", 200.0f, baselineModel.getBackgroundAbundance(90000));
		assertEquals("BackgroundAbundance", 200.0f, baselineModel.getBackgroundAbundance(100000));
		assertEquals("BackgroundAbundance", 200.0f, baselineModel.getBackgroundAbundance(95000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackgroundAbundance(150000));
	}

	public void testBaseline_2() {

		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackgroundAbundance(500));
		assertEquals("BackgroundAbundance", 100.0f, baselineModel.getBackgroundAbundance(1000));
		assertEquals("BackgroundAbundance", 100.0f, baselineModel.getBackgroundAbundance(50000));
		assertEquals("BackgroundAbundance", 100.0f, baselineModel.getBackgroundAbundance(5000));
	}
}
