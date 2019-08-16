/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class BatchProcessJobReader_1_ITest extends TestCase {

	private BatchProcessJob batchProcessJob;
	private JobReader reader;
	private File file;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		reader = new JobReader();
		file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_BATCH_PROCESS_JOB));
		batchProcessJob = reader.read(file, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	/*
	 * INPUT
	 */
	public void testGetInputEntries_1() {

		assertEquals(1, batchProcessJob.getChromatogramInputEntries().size());
	}

	public void testGetInputEntries_2() {

		String inputFile = batchProcessJob.getChromatogramInputEntries().get(0).getInputFile();
		assertEquals("org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.fragment.test/testData/files/import/ChromatogramInput.CDF", inputFile);
	}
}
