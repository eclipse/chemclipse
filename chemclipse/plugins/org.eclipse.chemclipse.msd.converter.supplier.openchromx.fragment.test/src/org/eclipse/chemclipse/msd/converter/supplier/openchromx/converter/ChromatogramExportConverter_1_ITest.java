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

import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class ChromatogramExportConverter_1_ITest extends TestCase {

	private IChromatogramMSD chromatogram;
	private File importFile;
	private File exportFile;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1_AGILENT));
		IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(importFile, new NullProgressMonitor());
		chromatogram = processingInfo.getProcessingResult(IChromatogramMSD.class);
		chromatogram.setMiscInfo("Hello Test Misc Info");
		chromatogram.getEditHistory().add(new EditInformation("I have done </Chromatogram> something"));
		chromatogram.getEditHistory().add(new EditInformation("me too"));
		exportFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_TEST));
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testExport_1() {

		ChromatogramExportConverter converter = new ChromatogramExportConverter();
		IProcessingInfo<File> processingInfo = converter.convert(exportFile, chromatogram, new NullProgressMonitor());
		try {
			File file = processingInfo.getProcessingResult(File.class);
			assertNotNull(file);
		} catch(TypeCastException e) {
			assertTrue("TypeCastException", false);
		}
	}
}
