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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core;

import org.eclipse.chemclipse.model.baseline.IBaselineModel;

public class TrapezoidBackgroundIntegrator_1_Test extends SimpleChromatogramTestCase {

	private IBackgroundIntegrator integrator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		integrator = new BackgroundIntegrator();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testIntegrate_1() {

		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		baselineModel.addBaseline(4500, 6500, 200, 200, true);
		double area = integrator.integrate(chromatogramSelection);
		assertEquals("Background", 4000.0d, area);
	}

	public void testIntegrate_2() {

		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		baselineModel.addBaseline(4500, 6500, 400, 400, true);
		double area = integrator.integrate(chromatogramSelection);
		assertEquals("Background", 8000.0d, area);
	}

	public void testIntegrate_3() {

		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		baselineModel.addBaseline(4500, 6500, 200, 400, true);
		double area = integrator.integrate(chromatogramSelection);
		assertEquals("Background", 6000.0d, area);
	}
}
