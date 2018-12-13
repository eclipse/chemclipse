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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class ChromatogramImportOCBTestCase extends TestCase {

	private IChromatogramMSD chromatogram;
	protected IChromatogramSelectionMSD chromatogramSelection;
	protected String chromatogramRelativePath;
	private static final String converterId = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File fileImport = new File(TestPathHelper.getAbsolutePath(chromatogramRelativePath));
		org.eclipse.chemclipse.processing.core.IProcessingInfo processingInfo = ChromatogramConverterMSD.getInstance().convert(fileImport, converterId, new NullProgressMonitor());
		chromatogram = processingInfo.getProcessingResult(IChromatogramMSD.class);
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogramSelection = null;
		chromatogram = null;
		super.tearDown();
	}
}
