/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor.PeakIntegrator;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.core.runtime.NullProgressMonitor;

public class TrapezoidPeakIntegrator_2_Test extends DefaultPeakTestCase {

	private PeakIntegrator integrator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		integrator = new PeakIntegrator();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testIntegrate_1() {

		try {
			integrator.integrate(super.getPeak(), null, new NullProgressMonitor());
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", true);
		}
	}

	public void testIntegrate_2() {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		peaks.add(super.getPeak());
		try {
			integrator.integrate(peaks, null, new NullProgressMonitor());
		} catch(ValueMustNotBeNullException e) {
			assertTrue("ValueMustNotBeNullException", true);
		}
	}
}
