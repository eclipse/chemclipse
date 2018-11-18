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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.core;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class BatchProcess {

	private static final String DESCRIPTION = "Batch Processor";

	public IProcessingInfo execute(IBatchProcessJob batchProcessJob, IProgressMonitor monitor) {

		IProcessingInfo batchProcessingInfo = new ProcessingInfo();
		/*
		 * The batch process jobs must not be null.
		 */
		if(batchProcessJob == null) {
			batchProcessingInfo.addErrorMessage(DESCRIPTION, "The batch job was null.");
		} else {
			ProcessMethod processMethod = batchProcessJob.getProcessMethod();
			for(IChromatogramInputEntry chromatogramInput : batchProcessJob.getChromatogramInputEntries()) {
				String inputFile = chromatogramInput.getInputFile();
				System.out.println("TODO Batch Job");
			}
		}
		return batchProcessingInfo;
	}
}
