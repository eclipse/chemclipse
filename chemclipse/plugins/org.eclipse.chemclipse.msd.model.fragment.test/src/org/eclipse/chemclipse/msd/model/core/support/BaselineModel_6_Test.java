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

public class BaselineModel_6_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IBaselineModel baselineModel;
	private IBaselineModel baselineModelCopy;

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
		baselineModelCopy = baselineModel.makeDeepCopy();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		baselineModel = null;
		super.tearDown();
	}

	public void testMakeDeepCopy_1() {

		assertNotSame(baselineModelCopy, baselineModel);
	}

	public void testMakeDeepCopy_2() {

		baselineModelCopy.removeBaseline();
		assertEquals(0.0f, baselineModelCopy.getBackgroundAbundance(20000));
		assertFalse(baselineModelCopy.getBackgroundAbundance(20000) == baselineModel.getBackgroundAbundance(20000));
	}
}
