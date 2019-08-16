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

public class BaselineModel_4_Test extends TestCase {

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
		baselineModel.addBaseline(1500, 50000, 100, 100, true);
		baselineModel.addBaseline(1501, 49999, 200, 200, true);
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

		assertEquals("BackgroundAbundance", 100.0f, baselineModel.getBackgroundAbundance(1500));
		assertEquals("BackgroundAbundance", 200.0f, baselineModel.getBackgroundAbundance(1501));
		assertEquals("BackgroundAbundance", 200.0f, baselineModel.getBackgroundAbundance(49999));
		assertEquals("BackgroundAbundance", 100.0f, baselineModel.getBackgroundAbundance(50000));
	}
}
