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
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsMSD;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class FirstDerivativePeakDetector_1_ITest extends TestCase {

	private File file;
	private IChromatogramMSD chromatogram;
	private PeakDetectorMSD peakDetector;
	private PeakDetectorSettingsMSD peakDetectorSettings;
	private IChromatogramSelectionMSD chromatogramSelection;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_OP17760));
		IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(file, new NullProgressMonitor());
		chromatogram = processingInfo.getProcessingResult();
		peakDetector = new PeakDetectorMSD();
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
		peakDetectorSettings = new PeakDetectorSettingsMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		file = null;
		chromatogram = null;
		peakDetector = null;
		chromatogramSelection = null;
		peakDetectorSettings = null;
		super.tearDown();
	}

	public void testDetect_1() throws ChromatogramIsNullException, ValueMustNotBeNullException {

		// Default
		peakDetector.detect(chromatogramSelection, peakDetectorSettings, new NullProgressMonitor());
		assertEquals("Peaks", 83, chromatogram.getNumberOfPeaks());
	}

	public void testDetect_2() throws ChromatogramIsNullException, ValueMustNotBeNullException {

		peakDetectorSettings.setThreshold(Threshold.OFF);
		peakDetector.detect(chromatogramSelection, peakDetectorSettings, new NullProgressMonitor());
		assertEquals("Peaks", 208, chromatogram.getNumberOfPeaks());
	}

	public void testDetect_3() throws ChromatogramIsNullException, ValueMustNotBeNullException {

		peakDetectorSettings.setThreshold(Threshold.LOW);
		peakDetector.detect(chromatogramSelection, peakDetectorSettings, new NullProgressMonitor());
		assertEquals("Peaks", 185, chromatogram.getNumberOfPeaks());
	}

	public void testDetect_4() throws ChromatogramIsNullException, ValueMustNotBeNullException {

		peakDetectorSettings.setThreshold(Threshold.MEDIUM);
		peakDetector.detect(chromatogramSelection, peakDetectorSettings, new NullProgressMonitor());
		assertEquals("Peaks", 83, chromatogram.getNumberOfPeaks());
	}

	public void testDetect_5() throws ChromatogramIsNullException, ValueMustNotBeNullException {

		peakDetectorSettings.setThreshold(Threshold.HIGH);
		peakDetector.detect(chromatogramSelection, peakDetectorSettings, new NullProgressMonitor());
		assertEquals("Peaks", 4, chromatogram.getNumberOfPeaks());
	}
}
