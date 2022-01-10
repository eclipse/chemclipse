/*******************************************************************************
 * Copyright (c) 2010, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.core;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.TestPathHelper;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.converter.model.ChromatogramInputEntry;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class BatchProcess_1_ITest extends TestCase {

	private BatchProcessJob batchProcessJob;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		batchProcessJob = new BatchProcessJob();
		String inputChromatogram = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_TEST);
		String outputChromatogram = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_TEST);
		/*
		 * Delete the output.
		 */
		File output = new File(outputChromatogram);
		if(output.exists()) {
			output.delete();
		}
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

		BatchProcess bp = new BatchProcess(new DataType[]{DataType.MSD}, new ProcessTypeSupport());
		bp.execute(batchProcessJob, new NullProgressMonitor());
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_TEST));
		assertNotNull(file);
	}
}