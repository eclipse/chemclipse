/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakInputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIntegrationEntry;

import junit.framework.TestCase;

public class PeakIdentificationBatchJobReader_1_ITest extends TestCase {

	private IPeakIdentificationBatchJob batchProcessJob;
	private IPeakIdentificationBatchJobReader reader;
	private File file;
	private IPeakInputEntry inputEntry;
	private IPeakIntegrationEntry integrationEntry;
	private IPeakIdentificationEntry identificationEntry;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		reader = new PeakIdentificationBatchJobReader();
		file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_BATCH_PROCESS_JOB));
		batchProcessJob = reader.read(file, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	/*
	 * HEADER
	 */
	public void testGetReportFolder_1() {

		assertEquals("/org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.fragment.test/testData/files/import", batchProcessJob.getReportFolder());
	}

	public void testIsOverrideReport_1() {

		assertEquals(false, batchProcessJob.isOverrideReport());
	}

	/*
	 * INPUT
	 */
	public void testGetInputEntries_1() {

		assertEquals(131, batchProcessJob.getPeakInputEntries().size());
	}

	public void testGetInputEntries_2() {

		String inputFile = batchProcessJob.getPeakInputEntries().get(0).getInputFile();
		assertEquals("/org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.fragment.test/testData/files/import/snip40_P1.mpl", inputFile);
	}

	public void testGetInputEntries_3() {

		inputEntry = batchProcessJob.getPeakInputEntries().get(0);
		assertEquals("/org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.fragment.test/testData/files/import/snip40_P1.mpl", inputEntry.getInputFile());
	}

	/*
	 * PROCESS
	 */
	public void testGetIntegrationEntry_1() {

		integrationEntry = batchProcessJob.getPeakIntegrationEntry();
		assertEquals("org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.peakIntegrator", integrationEntry.getProcessorId());
	}

	public void testGetIdentificationEntry_1() {

		identificationEntry = batchProcessJob.getPeakIdentificationEntry();
		assertEquals("org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.nist.peak", identificationEntry.getProcessorId());
	}

	/*
	 * OUTPUT
	 */
	public void testGetOutputEntries_1() {

		assertEquals(3, batchProcessJob.getPeakOutputEntries().size());
	}

	public void testGetOutputEntries_2() {

		String inputFile = batchProcessJob.getPeakOutputEntries().get(0).getOutputFolder();
		assertEquals("/org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.fragment.test/testData/files/import", inputFile);
	}

	public void testGetOutputEntries_3() {

		String converterId = batchProcessJob.getPeakOutputEntries().get(0).getConverterId();
		assertEquals("org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac", converterId);
	}
}
