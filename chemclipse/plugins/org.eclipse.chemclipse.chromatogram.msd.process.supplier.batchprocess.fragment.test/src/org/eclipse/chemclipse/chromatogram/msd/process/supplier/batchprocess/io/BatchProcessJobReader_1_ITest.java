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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;

import junit.framework.TestCase;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class BatchProcessJobReader_1_ITest extends TestCase {

	private IBatchProcessJob batchProcessJob;
	private BatchProcessJobReader reader;
	private File file;
	private IChromatogramProcessEntry processEntry;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		reader = new BatchProcessJobReader();
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

	/*
	 * PROCESS
	 */
	public void testGetProcessEntries_1() {

		assertEquals(6, batchProcessJob.getChromatogramProcessEntries().size());
	}

	public void testGetProcessEntries_2() {

		processEntry = batchProcessJob.getChromatogramProcessEntries().get(0);
		assertEquals("org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising", processEntry.getProcessorId());
		assertEquals("FILTER", processEntry.getProcessCategory());
	}

	public void testGetProcessEntries_3() {

		processEntry = batchProcessJob.getChromatogramProcessEntries().get(4);
		assertEquals("org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.combinedIntegrator", processEntry.getProcessorId());
		assertEquals("COMBINED_INTEGRATOR", processEntry.getProcessCategory());
	}

	public void testGetProcessEntries_4() {

		processEntry = batchProcessJob.getChromatogramProcessEntries().get(5);
		assertEquals("org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.nist.peak", processEntry.getProcessorId());
		assertEquals("PEAK_IDENTIFIER", processEntry.getProcessCategory());
	}

	/*
	 * OUTPUT
	 */
	public void testGetOutputEntries_1() {

		assertEquals(1, batchProcessJob.getChromatogramOutputEntries().size());
	}

	public void testGetOutputEntries_2() {

		String inputFile = batchProcessJob.getChromatogramOutputEntries().get(0).getOutputFolder();
		assertEquals("org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.fragment.test/testData/files/export/ChromatogramOutput.CDF", inputFile);
	}

	public void testGetOutputEntries_3() {

		String converterId = batchProcessJob.getChromatogramOutputEntries().get(0).getConverterId();
		assertEquals("net.openchrom.msd.converter.supplier.cdf", converterId);
	}
}
