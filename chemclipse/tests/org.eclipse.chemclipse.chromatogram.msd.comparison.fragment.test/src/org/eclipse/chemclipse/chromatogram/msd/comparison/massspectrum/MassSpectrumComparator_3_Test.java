/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity.IMassSpectrumPurityResult;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

import junit.framework.TestCase;

/**
 * Test the MassSpectrumComparator
 *
 * @author eselmeister
 */
public class MassSpectrumComparator_3_Test extends TestCase {

	private IScanMSD massSpectrum1;
	private IScanMSD massSpectrum2;

	@Override
	protected void setUp() throws Exception {

		massSpectrum1 = new ScanMSD();
		massSpectrum2 = new ScanMSD();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum1 = null;
		massSpectrum2 = null;
		super.tearDown();
	}

	public void testMassSpectrumComparatorCompare_1() {

		IProcessingInfo<IMassSpectrumPurityResult> processingInfo = MassSpectrumComparator.getPurityResult(null, null);
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testMassSpectrumComparatorCompare_2() {

		IProcessingInfo<IMassSpectrumPurityResult> processingInfo = MassSpectrumComparator.getPurityResult(massSpectrum1, null);
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testMassSpectrumComparatorCompare_3() {

		IProcessingInfo<IMassSpectrumPurityResult> processingInfo = MassSpectrumComparator.getPurityResult(null, massSpectrum2);
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testMassSpectrumComparatorCompare_4() {

		IProcessingInfo<IMassSpectrumPurityResult> processingInfo = MassSpectrumComparator.getPurityResult(massSpectrum1, massSpectrum2);
		assertFalse(processingInfo.hasErrorMessages());
		IMassSpectrumPurityResult result = processingInfo.getProcessingResult();
		assertEquals(0.0f, result.getFitValue());
		assertEquals(0.0f, result.getReverseFitValue());
	}
}
