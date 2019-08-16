/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.internal.core.support;

import junit.framework.TestCase;

public class NoiseSegment_2_Test extends TestCase {

	private INoiseSegment noiseSegment;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		noiseSegment = new NoiseSegment(null, null);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetAnalysisSegment_1() {

		assertNull(noiseSegment.getAnalysisSegment());
	}

	public void testGetNoiseMassSpectrum_1() {

		assertNull(noiseSegment.getNoiseMassSpectrum());
	}
}
