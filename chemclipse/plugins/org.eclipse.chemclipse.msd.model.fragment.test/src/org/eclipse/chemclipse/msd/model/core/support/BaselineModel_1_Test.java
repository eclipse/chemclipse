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

import org.eclipse.chemclipse.model.baseline.BaselineModel;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import junit.framework.TestCase;
import org.easymock.EasyMock;

public class BaselineModel_1_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IBaselineModel baselineModel;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = EasyMock.createMock(IChromatogramMSD.class);
		EasyMock.expect(chromatogram.getStartRetentionTime()).andStubReturn(5000);
		EasyMock.expect(chromatogram.getStopRetentionTime()).andStubReturn(500000);
		EasyMock.replay(chromatogram);
		baselineModel = new BaselineModel(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		baselineModel = null;
		super.tearDown();
	}

	public void testChromatogram_1() {

		assertEquals("StartRetentionTime", 5000, chromatogram.getStartRetentionTime());
		assertEquals("StopRetentionTime", 500000, chromatogram.getStopRetentionTime());
	}

	public void testGetBackgroundAbundance_1() {

		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackgroundAbundance(-1));
	}

	public void testGetBackgroundAbundance_2() {

		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackgroundAbundance(0));
	}

	public void testGetBackgroundAbundance_3() {

		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackgroundAbundance(5500));
	}

	public void testGetBackgroundAbundance_4() {

		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackgroundAbundance(500000));
	}

	public void testGetBackgroundAbundance_5() {

		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackgroundAbundance(500001));
	}
}
