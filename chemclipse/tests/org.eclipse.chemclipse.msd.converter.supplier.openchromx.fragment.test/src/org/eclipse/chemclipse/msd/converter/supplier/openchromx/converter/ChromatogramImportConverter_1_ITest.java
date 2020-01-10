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
package org.eclipse.chemclipse.msd.converter.supplier.openchromx.converter;

import java.io.File;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.TestPathHelper;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class ChromatogramImportConverter_1_ITest extends TestCase {

	private File importFile;

	// private File exportFile;
	@Override
	protected void setUp() throws Exception {

		super.setUp();
		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1));
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testImport_1() {

		ChromatogramImportConverter converter = new ChromatogramImportConverter();
		IChromatogramOverview chromatogram;
		IProcessingInfo<IChromatogramOverview> processingInfo = converter.convertOverview(importFile, new NullProgressMonitor());
		try {
			chromatogram = processingInfo.getProcessingResult();
			assertEquals("NumberOfScans", 5726, chromatogram.getNumberOfScans());
			assertEquals("TotalSignal", 1.0242423E9f, chromatogram.getTotalSignal());
		} catch(TypeCastException e) {
			assertTrue("TypeCastException", false);
		}
	}
}
