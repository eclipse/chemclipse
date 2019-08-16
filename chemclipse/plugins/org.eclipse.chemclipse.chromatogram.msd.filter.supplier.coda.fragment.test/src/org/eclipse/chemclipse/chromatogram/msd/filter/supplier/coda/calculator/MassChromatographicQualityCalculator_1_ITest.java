/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.exceptions.CodaCalculatorException;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class MassChromatographicQualityCalculator_1_ITest extends TestCase {

	private IMassChromatographicQualityResult result;
	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD chromatogramSelection;
	private float codaThreshold;
	private WindowSize windowSize = WindowSize.WIDTH_3;
	private File importFile;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1));
		IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(importFile, new NullProgressMonitor());
		chromatogram = processingInfo.getProcessingResult(IChromatogramMSD.class);
		codaThreshold = 0.7f;
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		importFile = null;
		chromatogram = null;
		chromatogramSelection = null;
		//
		System.gc();
		//
		super.tearDown();
	}

	public void testGetMassChromatographicQualityResult_1() {

		try {
			result = MassChromatographicQualityCalculator.calculate(chromatogramSelection, codaThreshold, windowSize);
			assertNotNull(result);
			float drv = result.getDataReductionValue();
			assertEquals("Data reduction value", 0.8737201f, drv);
			IMarkedIons exludedIons = result.getExcludedIons();
			assertNotNull(exludedIons);
		} catch(CodaCalculatorException e) {
			assertTrue("CodaCalculatorException", false);
		}
	}

	public void testGetMassChromatographicQualityResult_2() {

		try {
			result = MassChromatographicQualityCalculator.calculate(null, codaThreshold, windowSize);
		} catch(CodaCalculatorException e) {
			assertTrue("CodaCalculatorException", true);
		}
	}

	public void testGetMassChromatographicQualityResult_3() {

		try {
			result = MassChromatographicQualityCalculator.calculate(chromatogramSelection, -1, windowSize);
		} catch(CodaCalculatorException e) {
			assertTrue("CodaCalculatorException", true);
		}
	}

	public void testGetMassChromatographicQualityResult_4() {

		try {
			result = MassChromatographicQualityCalculator.calculate(chromatogramSelection, codaThreshold, null);
		} catch(CodaCalculatorException e) {
			assertTrue("CodaCalculatorException", true);
		}
	}
}
