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
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.model.ChromatogramInputEntry;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class BatchProcessJobWriter_1_ITest extends TestCase {

	private IBatchProcessJob batchProcessJob;
	private BatchProcessJobWriter writer;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		writer = new BatchProcessJobWriter();
		batchProcessJob = new BatchProcessJob();
		String inputChromatogram = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_TEST);
		String outputChromatogram = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_TEST);
		/*
		 * Input
		 */
		batchProcessJob.getChromatogramInputEntries().add(new ChromatogramInputEntry(inputChromatogram));
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testProcess_1() {

		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_BATCH_PROCESS_JOB));
		try {
			writer.writeBatchProcessJob(file, batchProcessJob, new NullProgressMonitor());
		} catch(FileNotFoundException e) {
			assertTrue("FileNotFoundException should not be thrown here.", false);
		} catch(FileIsNotWriteableException e) {
			assertTrue("FileIsNotWriteableException should not be thrown here.", false);
		} catch(IOException e) {
			assertTrue("IOException should not be thrown here.", false);
		} catch(XMLStreamException e) {
			assertTrue("XMLStreamException should not be thrown here.", false);
		}
		assertNotNull(file);
	}
}
