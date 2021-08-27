/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.baseline.BaselineModel;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;

import junit.framework.TestCase;

public class BaselineSupport_1_Test extends TestCase {

	private IBaselineSupport baselineSupport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		baselineSupport = new BaselineSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testBaselineBack_1() {

		float actual;
		baselineSupport.setBaselineBack(1500);
		actual = baselineSupport.getBackgroundAbundance(1500);
		assertEquals(0.0f, actual);
		actual = baselineSupport.getBackgroundAbundance(1);
		assertEquals(0.0f, actual);
	}

	public void testBaselineHoldOn_1() {

		float actual;
		baselineSupport.setBaselineHoldOn(1500, 2500);
		actual = baselineSupport.getBackgroundAbundance(1500);
		assertEquals(0.0f, actual);
		actual = baselineSupport.getBackgroundAbundance(2500);
		assertEquals(0.0f, actual);
	}

	public void testBaselineNow_1() {

		float actual;
		baselineSupport.setBaselineNow(1500);
		actual = baselineSupport.getBackgroundAbundance(1500);
		assertEquals(0.0f, actual);
		actual = baselineSupport.getBackgroundAbundance(2000);
		assertEquals(0.0f, actual);
	}

	public void testSetBaselineModel_1() {

		float actual;
		IChromatogramMSD chromatogram = new ChromatogramMSD();
		IBaselineModel baselineModel = new BaselineModel(chromatogram);
		baselineSupport.setBaselineModel(baselineModel);
		actual = baselineSupport.getBackgroundAbundance(1500);
		assertEquals(0.0f, actual);
		actual = baselineSupport.getBackgroundAbundance(2000);
		assertEquals(0.0f, actual);
	}

	public void testReset_1() {

		float actual;
		baselineSupport.setBaselineNow(1500);
		baselineSupport.reset();
		actual = baselineSupport.getBackgroundAbundance(1500);
		assertEquals(0.0f, actual);
		actual = baselineSupport.getBackgroundAbundance(2000);
		assertEquals(0.0f, actual);
	}
}
