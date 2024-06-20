/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.elu;

import java.io.File;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.PathResolver;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.ELUReader;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class ELUReader_2_ITest extends TestCase {

	private ELUReader reader;
	private IProcessingInfo<IPeaks<IPeakMSD>> processingInfo;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		reader = new ELUReader();
		String pathname = PathResolver.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS_1_ELU);
		File file = new File(pathname);
		processingInfo = reader.read(file, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		reader = null;
		super.tearDown();
	}

	@Test
	public void testRead_1() {

		try {
			IPeaks<IPeakMSD> peaks = processingInfo.getProcessingResult();
			assertEquals(1154, peaks.getPeaks().size());
			assertEquals(1132, peaks.getPeaks().stream().filter(p -> p.getPeakModel().isStrictModel()).toList().size());
		} catch(TypeCastException e) {
			assertTrue(false);
		}
	}
}
