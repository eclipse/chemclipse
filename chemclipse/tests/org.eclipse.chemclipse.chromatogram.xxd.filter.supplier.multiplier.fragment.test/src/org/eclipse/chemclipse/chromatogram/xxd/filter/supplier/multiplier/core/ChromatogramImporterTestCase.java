/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Janos Binder - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.core;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

import junit.framework.TestCase;

public class ChromatogramImporterTestCase extends TestCase {

	protected IChromatogramMSD chromatogram;
	protected IChromatogramSelectionMSD chromatogramSelection;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Import
		 */
		// File fileImport = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1));
		// org.eclipse.chemclipse.processing.core.IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(fileImport, EXTENSION_POINT_ID, new NullProgressMonitor());
		// chromatogram = processingInfo.getProcessingResult();
		// chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		chromatogramSelection = null;
		super.tearDown();
	}
}
