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
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.smoothed.core;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.smoothed.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.smoothed.settings.DetectorSettings;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class SmoothedBaselineDetector_1_ITest extends TestCase {

	private final static String CHROMATOGRAM_CONVERTER_ID = "org.eclipse.chemclipse.msd.converter.supplier.agilent";
	private final static String DETECTOR_ID = "org.eclipse.chemclipse.chromatogram.msd.baseline.detector.supplier.smoothed";
	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD chromatogramSelection;
	private DetectorSettings settings;
	private String pathImport;
	private File fileImport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_OP17760);
		fileImport = new File(this.pathImport);
		IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(fileImport, CHROMATOGRAM_CONVERTER_ID, new NullProgressMonitor());
		chromatogram = processingInfo.getProcessingResult();
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
		settings = new DetectorSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		pathImport = null;
		fileImport = null;
		chromatogram = null;
		chromatogramSelection = null;
		settings = null;
		//
		System.gc();
		//
		super.tearDown();
	}

	public void testBaseline_1() {

		assertEquals("numberOfScans", 5726, chromatogram.getNumberOfScans());
		IProcessingInfo processingInfo = BaselineDetector.setBaseline(chromatogramSelection, settings, DETECTOR_ID, new NullProgressMonitor());
		assertFalse(processingInfo.hasErrorMessages());
	}
}
