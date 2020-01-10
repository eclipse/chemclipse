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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core.ChromatogramIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core.IChromatogramIntegrator;

public class TrapezoidChromatogramIntegrator_2_ITest extends ChromatogramImportTestCase {

	private IChromatogramIntegrator integrator;

	@Override
	protected void setUp() throws Exception {

		chromatogramRelativePath = TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1;
		super.setUp();
		integrator = new ChromatogramIntegrator();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testIntegrate_1() {

		double area = integrator.integrate(chromatogramSelection);
		assertEquals("", 7.933076834045009E9, area);
	}
}
