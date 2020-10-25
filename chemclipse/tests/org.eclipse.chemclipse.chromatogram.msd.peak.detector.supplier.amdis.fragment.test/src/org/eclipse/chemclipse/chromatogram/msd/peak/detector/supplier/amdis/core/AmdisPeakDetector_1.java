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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.core;

import java.io.File;
import java.util.Date;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.PathResolver;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.SettingsAMDIS;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class AmdisPeakDetector_1 extends TestCase {

	private File file;
	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD chromatogramSelection;
	private SettingsAMDIS peakDetectorSettings;
	private PeakDetectorAMDIS detector;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		file = new File(PathResolver.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_OP17760));
		IProcessingInfo<?> processingInfo = ChromatogramConverterMSD.getInstance().convert(file, new NullProgressMonitor());
		chromatogram = (IChromatogramMSD)processingInfo.getProcessingResult();
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
		peakDetectorSettings = new SettingsAMDIS();
		detector = new PeakDetectorAMDIS();
	}

	@Override
	protected void tearDown() throws Exception {

		file = null;
		chromatogram = null;
		chromatogramSelection = null;
		peakDetectorSettings = null;
		detector = null;
		super.tearDown();
	}

	public void testIntegrate() {

		Date start = new Date();
		IProcessingInfo<?> processingInfo = detector.detect(chromatogramSelection, peakDetectorSettings, new NullProgressMonitor());
		assertFalse(processingInfo.hasErrorMessages());
		Date stop = new Date();
		System.out.println("Zeit ms:" + (stop.getTime() - start.getTime()));
	}
}
