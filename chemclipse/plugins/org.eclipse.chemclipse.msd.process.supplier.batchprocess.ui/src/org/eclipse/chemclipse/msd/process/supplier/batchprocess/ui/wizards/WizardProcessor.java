/*******************************************************************************
 * Copyright (c) 2014, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.process.supplier.batchprocess.ui.wizards;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchProcessJob;
import org.eclipse.chemclipse.process.supplier.batchprocess.io.JobWriter;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.support.ui.wizards.AbstractWizard;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class WizardProcessor extends AbstractWizard {

	private static final Logger logger = Logger.getLogger(WizardProcessor.class);

	private BatchProcessWizardElements wizardElements;

	private File batchProcessFile;

	public WizardProcessor() {

		super(new BatchProcessWizardElements());
		this.wizardElements = (BatchProcessWizardElements)getWizardElements();
	}

	@Override
	public void addPages() {

		super.addPages();

		addPage(new PageBatchProcessFile(wizardElements));
	}

	@Override
	public void doFinish(IProgressMonitor monitor) throws CoreException {

		File file = wizardElements.getBatchProcessFile();
		try {
			JobWriter jobWriter = new JobWriter();
			jobWriter.writeBatchProcessJob(file, createBatchProcessJob());
		} catch(Exception e) {
			logger.warn(e);
		}

		this.batchProcessFile = file;
	}

	public File getBatchProcessFile() {

		return batchProcessFile;
	}

	private BatchProcessJob createBatchProcessJob() {

		ProcessMethod processMethod = new ProcessMethod(ProcessMethod.MS);
		BatchProcessJob batchProcessJob = new BatchProcessJob(processMethod);
		batchProcessJob.setDataType(DataType.MALDI);

		return batchProcessJob;
	}
}
