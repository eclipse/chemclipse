/*******************************************************************************
 * Copyright (c) 2010, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - propagate errors/infos from processors to the user
 *******************************************************************************/
package org.eclipse.chemclipse.msd.process.supplier.batchprocess.core;

import java.io.File;

import org.eclipse.chemclipse.model.supplier.IScanProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchProcessJob;
import org.eclipse.chemclipse.process.supplier.batchprocess.core.IBatchProcess;
import org.eclipse.chemclipse.process.supplier.batchprocess.io.IBatchProcessInputEntry;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.AbstractProcessEntryContainer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IBatchProcess.class)
public class BatchProcess implements IBatchProcess {

	private static final String DESCRIPTION = "Batch Processor";

	@Override
	public boolean isValidFor(DataType dataType) {

		return ProcessMethod.MS.contains(dataType.toDataCategory());
	}

	@Override
	public IProcessingInfo<?> execute(BatchProcessJob batchProcessJob, IProcessSupplierContext processSupplierContext, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		/*
		 * The batch process jobs must not be null.
		 */
		if(batchProcessJob == null || batchProcessJob.getProcessMethod() == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The batch job and/or process method was null.");
		} else {
			IProcessMethod processMethod = batchProcessJob.getProcessMethod();
			for(IBatchProcessInputEntry batchProcessInput : batchProcessJob.getBatchProcessInputEntries()) {
				File file = new File(batchProcessInput.getInputFile());
				IProcessingInfo<IMassSpectra> processingInfoImport = MassSpectrumConverter.convert(file, monitor);
				if(!processingInfoImport.hasErrorMessages()) {
					IMassSpectra massSpectra = processingInfoImport.getProcessingResult();
					for(IScanMSD scanMSD : massSpectra.getList()) {
						ProcessingInfo<?> processorResult = new ProcessingInfo<>();
						AbstractProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, processorResult, processSupplierContext), IScanProcessSupplier.createConsumer(scanMSD));
						if(processorResult.hasErrorMessages()) {
							processingInfo.addErrorMessage(DESCRIPTION, "Processing: " + file + " failed");
						} else {
							processingInfo.addInfoMessage(DESCRIPTION, "Processing: " + file + " completed");
						}
						processingInfo.addMessages(processorResult);
					}
				} else {
					processingInfo.addErrorMessage(DESCRIPTION, "Failure to process: " + file);
					processingInfo.addMessages(processingInfoImport);
				}
			}
		}
		return processingInfo;
	}
}
