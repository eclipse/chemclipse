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

import java.io.File;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.chromatogram.xxd.report.model.IChromatogramReportSupplierEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramOutputEntry;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;

public class BatchProcess implements IBatchProcess {

	private Logger logger = Logger.getLogger(BatchProcess.class);
	private static final String DESCRIPTION = "Batch Processor";
	private ProcessTypeSupport processTypeSupport;

	public BatchProcess() {
		processTypeSupport = new ProcessTypeSupport();
	}

	@Override
	public IProcessingInfo execute(IBatchProcessJob batchProcessJob, IProgressMonitor monitor) {

		IProcessingInfo batchProcessingInfo = new ProcessingInfo();
		/*
		 * The batch process jobs must not be null.
		 */
		if(batchProcessJob == null) {
			batchProcessingInfo.addErrorMessage(DESCRIPTION, "The batch job was null.");
		} else {
			/*
			 * Process all entries.
			 * Input -> Process -> Output
			 */
			for(IChromatogramInputEntry chromatogramInput : batchProcessJob.getChromatogramInputEntries()) {
				/*
				 * Get the chromatogram.
				 */
				File chromatogramInputFile = new File(chromatogramInput.getInputFile());
				try {
					IChromatogramMSD chromatogram = loadChromatogram(chromatogramInputFile, batchProcessingInfo, monitor);
					processChromatogram(chromatogram, batchProcessJob, batchProcessingInfo, monitor);
					batchProcessingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, "The file has been processed successfully: " + chromatogramInputFile));
				} catch(TypeCastException e) {
					batchProcessingInfo.addErrorMessage(DESCRIPTION, "A failure occurred fetching the chromatogram: " + chromatogramInputFile);
				}
			}
		}
		return batchProcessingInfo;
	}

	private IChromatogramMSD loadChromatogram(File chromatogramInputFile, IProcessingInfo batchProcessingInfo, IProgressMonitor monitor) throws TypeCastException {

		IProcessingInfo processingInfo = ChromatogramConverterMSD.convert(chromatogramInputFile, monitor);
		batchProcessingInfo.addMessages(processingInfo);
		IChromatogramMSD chromatogram = processingInfo.getProcessingResult(IChromatogramMSD.class);
		return chromatogram;
	}

	private void processChromatogram(IChromatogramMSD chromatogram, IBatchProcessJob batchProcessJob, IProcessingInfo batchProcessingInfo, IProgressMonitor monitor) {

		/*
		 * The chromatogram must be not null.
		 */
		if(chromatogram != null) {
			processChromatogramEntry(chromatogram, batchProcessJob, batchProcessingInfo, monitor);
			writeChromatogramOutputEntries(chromatogram, batchProcessJob, batchProcessingInfo, monitor);
			processChromatogramReportEntries(chromatogram, batchProcessJob, batchProcessingInfo, monitor);
		} else {
			batchProcessingInfo.addErrorMessage(DESCRIPTION, "The chromatogram must be not null.");
		}
	}

	/**
	 * Process the chromatogram selection with each process entry.
	 * 
	 * @param chromatogramSelection
	 * @param batchProcessJob
	 * @param batchProcessReport
	 * @param monitor
	 */
	private void processChromatogramEntry(IChromatogramMSD chromatogram, IBatchProcessJob batchProcessJob, IProcessingInfo batchProcessingInfo, IProgressMonitor monitor) {

		for(IChromatogramProcessEntry processEntry : batchProcessJob.getChromatogramProcessEntries()) {
			/*
			 * Retrieve each time the chromatogram selection, cause e.g. the RT Shifter moves retention times forward and backward.
			 * Hence the chromatogram selection range could change.
			 * The chromatogram selection must be not null.
			 */
			IChromatogramSelectionMSD chromatogramSelection = getChromatogramSelection(chromatogram, monitor);
			if(chromatogramSelection == null) {
				batchProcessingInfo.addErrorMessage(DESCRIPTION, "The chromatogram selection must not be null.");
			} else {
				/*
				 * Add the processing messages.
				 */
				IProcessingInfo processingInfo = processTypeSupport.applyProcessor(chromatogramSelection, processEntry, monitor);
				batchProcessingInfo.addMessages(processingInfo);
			}
		}
	}

	/**
	 * Write the chromatogram output entries.
	 * 
	 * @param chromatogramSelection
	 * @param batchProcessJob
	 * @param batchProcessReport
	 * @param monitor
	 */
	private void writeChromatogramOutputEntries(IChromatogramMSD chromatogram, IBatchProcessJob batchProcessJob, IProcessingInfo batchProcessingInfo, IProgressMonitor monitor) {

		/*
		 * Write the chromatogram to each listed output format.
		 */
		IProcessingInfo processingInfo = null;
		for(IChromatogramOutputEntry chromatogramOutput : batchProcessJob.getChromatogramOutputEntries()) {
			/*
			 * Append the "/" or "\" to the end of the folder if not exists.
			 */
			String outputFolder = chromatogramOutput.getOutputFolder();
			if(!outputFolder.endsWith(File.separator)) {
				outputFolder += File.separator;
			}
			/*
			 * Write the chromatogram.
			 */
			File chromatogramOutputFile = new File(outputFolder + chromatogram.getName());
			processingInfo = ChromatogramConverterMSD.convert(chromatogramOutputFile, chromatogram, chromatogramOutput.getConverterId(), monitor);
			batchProcessingInfo.addMessages(processingInfo);
		}
	}

	/**
	 * Process the chromatogram report entries.
	 * 
	 * @param chromatogramSelection
	 * @param batchProcessJob
	 * @param batchProcessReport
	 * @param monitor
	 */
	private void processChromatogramReportEntries(IChromatogramMSD chromatogram, IBatchProcessJob batchProcessJob, IProcessingInfo batchProcessingInfo, IProgressMonitor monitor) {

		/*
		 * Report the chromatogram by each selected report supplier.
		 */
		IProcessingInfo processingInfo = null;
		for(IChromatogramReportSupplierEntry chromatogramReport : batchProcessJob.getChromatogramReportEntries()) {
			/*
			 * Append the reports?
			 */
			boolean appendReport;
			String reportFolderOrFile = chromatogramReport.getReportFolderOrFile();
			File chromatogramReportFile = new File(reportFolderOrFile);
			/*
			 * If it's a directory, then prepare the file name. Otherwise, the stored selection is the file name.
			 */
			if(chromatogramReportFile.isDirectory()) {
				appendReport = false;
				//
				if(!reportFolderOrFile.endsWith(File.separator)) {
					reportFolderOrFile += File.separator;
				}
				chromatogramReportFile = new File(reportFolderOrFile + chromatogram.getName());
			} else {
				appendReport = true;
			}
			/*
			 * Report the chromatogram.
			 */
			processingInfo = ChromatogramReports.generate(chromatogramReportFile, appendReport, chromatogram, chromatogramReport.getReportSupplierId(), monitor);
			batchProcessingInfo.addMessages(processingInfo);
		}
	}

	/**
	 * Returns the chromatogram selection or null if something has gone wrong.
	 * 
	 * @param chromatogram
	 * @return
	 */
	private IChromatogramSelectionMSD getChromatogramSelection(IChromatogramMSD chromatogram, IProgressMonitor monitor) {

		assert chromatogram != null : "The chromatogram must be not null.";
		IChromatogramSelectionMSD chromatogramSelection = null;
		try {
			chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		return chromatogramSelection;
	}
}
