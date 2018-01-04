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
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import junit.framework.TestCase;

public class PeakMassSpectrum_4_Test extends TestCase {

	@SuppressWarnings("unused")
	private IPeakMassSpectrum peakMassSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		peakMassSpectrum = null;
		super.tearDown();
	}

	public void testGetNumberOfIons_1() {

		try {
			peakMassSpectrum = new PeakMassSpectrum(null);
		} catch(IllegalArgumentException e) {
			assertTrue("IllegalArgumentException", true);
		}
	}
}
