/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.converter;

import java.io.File;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class MatlabParafacPeakImportConverter_6_ITest extends TestCase {

	private IPeaks<IPeakMSD> peaks;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		MatlabParafacPeakImportConverter converter = new MatlabParafacPeakImportConverter();
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PARAFAC_TEST_2));
		IProcessingInfo<IPeaks<IPeakMSD>> processingInfo = converter.convert(file, new NullProgressMonitor());
		peaks = processingInfo.getProcessingResult();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testImport_1() {

		assertEquals(1, peaks.getPeaks().size());
	}
}
