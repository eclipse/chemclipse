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

import org.eclipse.chemclipse.model.support.AnalysisSegment;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.CombinedMassSpectrum;

import junit.framework.TestCase;

public class NoiseSegment_1_Test extends TestCase {

	private INoiseSegment noiseSegment;
	private IAnalysisSegment analysisSegment;
	private ICombinedMassSpectrum noiseMassSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		analysisSegment = new AnalysisSegment(20, 200);
		noiseMassSpectrum = new CombinedMassSpectrum();
		noiseSegment = new NoiseSegment(analysisSegment, noiseMassSpectrum);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetAnalysisSegment_1() {

		assertNotNull(noiseSegment.getAnalysisSegment());
	}

	public void testGetAnalysisSegment_2() {

		assertEquals("Segment Width", 200, noiseSegment.getAnalysisSegment().getSegmentWidth());
	}

	public void testGetNoiseMassSpectrum_1() {

		assertNotNull(noiseSegment.getNoiseMassSpectrum());
	}
}
