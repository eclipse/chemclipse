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
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.exceptions.BaselineDetectorSettingsException;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.exceptions.NoBaselineDetectorAvailableException;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

/**
 * Test the BaselineDetector.
 * 
 * @author eselmeister
 */
public class BaselineDetector_2_Test extends TestCase {

	IBaselineDetectorSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = BaselineDetector.getBaselineDetectorSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		super.tearDown();
	}

	public void testBaselineDetector_1() throws BaselineDetectorSettingsException {

		try {
			String detectorId = BaselineDetector.getBaselineDetectorSupport().getDetectorId(0);
			IProcessingInfo processingInfo = BaselineDetector.setBaseline(null, null, detectorId, new NullProgressMonitor());
			assertTrue(processingInfo.hasErrorMessages());
		} catch(NoBaselineDetectorAvailableException e) {
			assertTrue("NoBaselineDetectorAvailableException", false);
		}
	}

	public void testBaselineDetector_2() throws org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException {

		String detectorId = "";
		IChromatogramSelectionMSD chromatogramSelection = new ChromatogramSelectionMSD(new ChromatogramMSD());
		IProcessingInfo processingInfo = BaselineDetector.setBaseline(chromatogramSelection, null, detectorId, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}
}
