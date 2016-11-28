/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msp;

import java.io.File;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.massspectrum.IMassSpectrumImportConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class MSPImportConverter_4_ITest extends TestCase {

	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_LIB_4_MSP));
		IMassSpectrumImportConverter importConverter = new MSPMassSpectrumImportConverter();
		IMassSpectrumImportConverterProcessingInfo processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
		massSpectra = processingInfo.getMassSpectra();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectra = null;
		super.tearDown();
	}

	public void test_1() {

		assertEquals(1, massSpectra.size());
	}

	public void test_2() throws AbundanceLimitExceededException, IonLimitExceededException {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		assertEquals(5000, massSpectrum.getNumberOfIons());
	}
}
