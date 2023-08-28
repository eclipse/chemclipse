/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.splash;

import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

import junit.framework.TestCase;

public class Splash_Test extends TestCase {

	IVendorMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(100, 1));
		massSpectrum.addIon(new Ion(101, 2));
		massSpectrum.addIon(new Ion(102, 3));
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSplash() {

		assertEquals("splash10-0udi-0900000000-f5bf6f6a4a1520a35d4f", new SplashFactory(massSpectrum).getSplash());
	}
}
