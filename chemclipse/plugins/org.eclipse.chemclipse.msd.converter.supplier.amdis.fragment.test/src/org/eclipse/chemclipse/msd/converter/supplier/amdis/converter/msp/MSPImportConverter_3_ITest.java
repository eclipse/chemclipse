/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
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
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msp;

import java.io.File;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.database.IDatabaseImportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.PathResolver;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class MSPImportConverter_3_ITest extends TestCase {

	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File importFile = new File(PathResolver.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_LIB_3_MSP));
		IDatabaseImportConverter<IMassSpectra> importConverter = new MSPDatabaseImportConverter();
		IProcessingInfo<IMassSpectra> processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
		massSpectra = processingInfo.getProcessingResult();
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
		ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)massSpectrum;
		assertEquals(649080, massSpectrum.getRetentionTime());
		assertEquals(0, massSpectrum.getRelativeRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals("+EI Scan (rt: 10.818 min)", libraryMassSpectrum.getLibraryInformation().getName());
		assertEquals("", libraryMassSpectrum.getLibraryInformation().getCasNumber());
		assertEquals("365", libraryMassSpectrum.getLibraryInformation().getDatabase());
		assertEquals(65, massSpectrum.getNumberOfIons());
		assertEquals(0.80f, massSpectrum.getIon(50.0156d).getAbundance());
		assertEquals(0.07f, massSpectrum.getIon(50.0785d).getAbundance());
		assertEquals(0.04f, massSpectrum.getIon(55.2418d).getAbundance());
		assertEquals(5.66f, massSpectrum.getTotalSignal(), 0.01d);
	}
}
