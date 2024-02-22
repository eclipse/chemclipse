/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.csv.io;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class ChromatogramWriterTestCase extends TestCase {

	protected IChromatogramMSD chromatogramImport;
	protected IChromatogramMSD chromatogram;
	protected String pathImport;
	protected String pathExport;
	protected File fileImport;
	protected File fileExport;
	protected String extensionPointImport;
	protected String extensionPointExportReimport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Import the chromatogram.
		 */
		fileImport = new File(this.pathImport);
		IProcessingInfo<IChromatogramMSD> processingInfoImport = ChromatogramConverterMSD.getInstance().convert(fileImport, this.extensionPointImport, new NullProgressMonitor());
		chromatogramImport = processingInfoImport.getProcessingResult();
		/*
		 * Export the chromatogram.
		 */
		fileExport = new File(this.pathExport);
		IProcessingInfo<File> processingInfoExport = ChromatogramConverterMSD.getInstance().convert(fileExport, chromatogramImport, this.extensionPointExportReimport, new NullProgressMonitor());
		fileExport = processingInfoExport.getProcessingResult();
		/*
		 * Reimport the exported chromatogram.
		 */
		chromatogramImport = null;
		IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(fileExport, this.extensionPointExportReimport, new NullProgressMonitor());
		chromatogram = processingInfo.getProcessingResult();
	}

	@Override
	protected void tearDown() throws Exception {

		pathImport = null;
		pathExport = null;
		fileImport = null;
		fileExport.delete();
		fileExport = null;
		chromatogramImport = null;
		chromatogram = null;
		super.tearDown();
	}
}
