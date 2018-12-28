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

import org.easymock.EasyMock;
import org.eclipse.chemclipse.model.baseline.BaselineModel;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

import junit.framework.TestCase;

public class BaselineModel_7_Test extends TestCase {

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
		baselineModel.addBaseline(5000, 50000, 100, 10000, true);
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

		assertEquals("BackgroundAbundance", 100.0f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 10000.0f, baselineModel.getBackground(50000));
		assertEquals("BackgroundAbundance", 5600.0f, baselineModel.getBackground(30000));
	}

	public void testBaseline_2() {

		baselineModel.removeBaseline();
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(30000));
	}

	public void testBaseline_3() {

		/*
		 * Cut an existing segment into two peaces.
		 */
		baselineModel.removeBaseline(25000, 35000);
		assertEquals("BackgroundAbundance", 4499.78f, baselineModel.getBackground(24999));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(25000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(30000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(35000));
		assertEquals("BackgroundAbundance", 6700.22f, baselineModel.getBackground(35001));
	}

	public void testBaseline_4() {

		/*
		 * Cut the ending part of an existing segment.
		 */
		baselineModel.removeBaseline(40000, 60000);
		assertEquals("BackgroundAbundance", 7799.78f, baselineModel.getBackground(39999));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(40000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(60000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(100000));
	}

	public void testBaseline_5() {

		/*
		 * Cut the beginning part of an existing segment.
		 */
		baselineModel.removeBaseline(1000, 10000);
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(1000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(10000));
		assertEquals("BackgroundAbundance", 1200.22f, baselineModel.getBackground(10001));
	}

	public void testBaseline_6() {

		/*
		 * The baseline will be removed after calling
		 * baselineModel.removeBaseline(1000, 10000). But now check that the
		 * segment is right in place.
		 */
		baselineModel.addBaseline(2000, 4000, 500, 500, true);
		assertEquals("BackgroundAbundance", 500.0f, baselineModel.getBackground(2000));
		assertEquals("BackgroundAbundance", 500.0f, baselineModel.getBackground(2500));
		assertEquals("BackgroundAbundance", 500.0f, baselineModel.getBackground(4000));
		/*
		 * The baseline will be removed after calling
		 * baselineModel.removeBaseline(1000, 10000). But now check that the
		 * segment is right in place.
		 */
		baselineModel.addBaseline(4500, 8000, 800, 800, true);
		assertEquals("BackgroundAbundance", 800.0f, baselineModel.getBackground(4500));
		assertEquals("BackgroundAbundance", 800.0f, baselineModel.getBackground(6500));
		assertEquals("BackgroundAbundance", 800.0f, baselineModel.getBackground(8000));
		/*
		 * If the segment is totally hidden by the segment to be inserted,
		 * remove it.
		 */
		baselineModel.removeBaseline(1000, 10000);
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(1000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(2000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(2500));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(4000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(4500));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(6500));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(8000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(10000));
		assertEquals("BackgroundAbundance", 1200.22f, baselineModel.getBackground(10001));
	}

	public void testBaseline_7() {

		/*
		 * Cut an existing segment into two peaces.
		 */
		// baselineModel.addBaseline(5000, 50000, 100, 10000, true);
		baselineModel.removeBaseline(5001, 49999);
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(4999));
		assertEquals("BackgroundAbundance", 100f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(5001));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(49999));
		assertEquals("BackgroundAbundance", 10000f, baselineModel.getBackground(50000));
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(50001));
		baselineModel.removeBaseline(5000, 49999);
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 10000f, baselineModel.getBackground(50000));
	}

	public void testBaseline_8() {

		/*
		 * Cut an existing segment into two peaces.
		 */
		// baselineModel.addBaseline(5000, 50000, 100, 10000, true);
		baselineModel.removeBaseline(5001, 49999);
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(4999));
		assertEquals("BackgroundAbundance", 100f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(5001));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(49999));
		assertEquals("BackgroundAbundance", 10000f, baselineModel.getBackground(50000));
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(50001));
		baselineModel.addBaseline(5000, 5000, 1f, 1f, true);
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(4999));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(5001));
		assertEquals("BackgroundAbundance", 10000f, baselineModel.getBackground(50000));
	}

	public void testBaseline_9() {

		/*
		 * Cut an existing segment into two peaces.
		 */
		// baselineModel.addBaseline(5000, 50000, 100, 10000, true);
		baselineModel.removeBaseline(5001, 49999);
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(4999));
		assertEquals("BackgroundAbundance", 100f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(5001));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(49999));
		assertEquals("BackgroundAbundance", 10000f, baselineModel.getBackground(50000));
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(50001));
		baselineModel.addBaseline(2000, 49999, 1f, 1f, true);
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(4999));
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(49999));
		assertEquals("BackgroundAbundance", 10000f, baselineModel.getBackground(50000));
	}

	public void testBaseline_10() {

		/*
		 * Cut an existing segment into two peaces.
		 */
		// baselineModel.addBaseline(5000, 50000, 100, 10000, true);
		baselineModel.removeBaseline(5001, 49999);
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(4999));
		assertEquals("BackgroundAbundance", 100f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(5001));
		assertEquals("BackgroundAbundance", 0.0f, baselineModel.getBackground(49999));
		assertEquals("BackgroundAbundance", 10000f, baselineModel.getBackground(50000));
		assertEquals("BackgroundAbundance", 0f, baselineModel.getBackground(50001));
		baselineModel.addBaseline(2000, 49999, 1f, 1f, true);
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(2000));
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(4999));
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(5000));
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(49999));
		assertEquals("BackgroundAbundance", 10000f, baselineModel.getBackground(50000));
		baselineModel.addBaseline(4000, 60000, 2f, 2f, true);
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(2000));
		assertEquals("BackgroundAbundance", 1f, baselineModel.getBackground(3999));
		assertEquals("BackgroundAbundance", 2f, baselineModel.getBackground(4000));
		assertEquals("BackgroundAbundance", 2f, baselineModel.getBackground(4001));
		assertEquals("BackgroundAbundance", 2f, baselineModel.getBackground(50000));
		assertEquals("BackgroundAbundance", 2f, baselineModel.getBackground(50001));
		assertEquals("BackgroundAbundance", 2f, baselineModel.getBackground(60000));
	}
}
