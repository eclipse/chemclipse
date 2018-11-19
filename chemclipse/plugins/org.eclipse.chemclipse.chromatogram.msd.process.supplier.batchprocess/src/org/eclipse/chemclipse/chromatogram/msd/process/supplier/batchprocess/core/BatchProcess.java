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

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.xxd.process.support.ChromatogramTypeSupport;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;

public class BatchProcess {

	private static final Logger logger = Logger.getLogger(BatchProcess.class);
	private static final String DESCRIPTION = "Batch Processor";
	private ChromatogramTypeSupport chromatogramTypeSupport = new ChromatogramTypeSupport();
	private ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();

	@SuppressWarnings("rawtypes")
	public IProcessingInfo execute(BatchProcessJob batchProcessJob, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		/*
		 * The batch process jobs must not be null.
		 */
		if(batchProcessJob == null || batchProcessJob.getProcessMethod() == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The batch job and/or process method was null.");
		} else {
			ProcessMethod processMethod = batchProcessJob.getProcessMethod();
			for(IChromatogramInputEntry chromatogramInput : batchProcessJob.getChromatogramInputEntries()) {
				String pathChromatogram = chromatogramInput.getInputFile();
				IProcessingInfo processingInfoX = chromatogramTypeSupport.getChromatogramSelection(pathChromatogram, monitor);
				if(!processingInfoX.hasErrorMessages()) {
					try {
						IChromatogramSelection chromatogramSelection = processingInfoX.getProcessingResult(IChromatogramSelection.class);
						IProcessingInfo processingInfoY = processTypeSupport.applyProcessor(chromatogramSelection, processMethod, monitor);
						if(!processingInfoY.hasErrorMessages()) {
							processingInfo.addInfoMessage(DESCRIPTION, "Success to process: " + pathChromatogram);
						} else {
							processingInfo.addErrorMessage(DESCRIPTION, "Failure to process: " + pathChromatogram);
						}
					} catch(TypeCastException e) {
						logger.warn(e);
						processingInfo.addErrorMessage(DESCRIPTION, "Failure to process: " + pathChromatogram);
					}
				} else {
					processingInfo.addErrorMessage(DESCRIPTION, "Failure to process: " + pathChromatogram);
				}
			}
		}
		return processingInfo;
	}
}
