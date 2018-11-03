/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.io.ChromatogramReportWriter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISingleChromatogramReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.SingleChromatogramReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.model.ChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.supplier.BaselineDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramCalculatorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class EvaluationProcessor {

	public static final String REPORT_FILE_EXTENSION = ".scr";
	//
	private static final Logger logger = Logger.getLogger(EvaluationProcessor.class);
	//
	private static final String CHROMATOGRAM_CONVERTER_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	private static final String CHROMATOGRAM_FILE_EXTENSION = ".ocb";

	public void processAndWriteReport(File chromatogramEvaluationReportFile, List<IChromatogramProcessEntry> chromatogramProcessingEntries, String description, String notes, String pathChromatogramOriginal, IProgressMonitor monitor) {

		try {
			File chromatogramImportFile = new File(pathChromatogramOriginal);
			IProcessingInfo processingInfoImport = ChromatogramConverterMSD.convert(chromatogramImportFile, monitor);
			IChromatogramMSD chromatogramMSD = processingInfoImport.getProcessingResult(IChromatogramMSD.class);
			IChromatogramSelectionMSD chromatogramSelectionMSD = new ChromatogramSelectionMSD(chromatogramMSD);
			processChromatogram(chromatogramSelectionMSD, chromatogramProcessingEntries, monitor);
			//
			File chromatogramExportFile = new File(chromatogramEvaluationReportFile.getAbsolutePath().replace(REPORT_FILE_EXTENSION, CHROMATOGRAM_FILE_EXTENSION));
			IProcessingInfo processingInfoExport = ChromatogramConverterMSD.convert(chromatogramExportFile, chromatogramMSD, CHROMATOGRAM_CONVERTER_ID, monitor);
			File chromatogramExportFileVerify = processingInfoExport.getProcessingResult(File.class);
			/*
			 * Chromatogram Report File
			 */
			ISingleChromatogramReport chromatogramReport = new SingleChromatogramReport();
			chromatogramReport.setChromatogramName(chromatogramMSD.getName());
			chromatogramReport.setChromatogramPath(chromatogramExportFileVerify.getAbsolutePath());
			chromatogramReport.setEvaluationDate(new Date());
			chromatogramReport.setDescription(description);
			//
			ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();
			List<String> processorNames = new ArrayList<String>();
			for(IChromatogramProcessEntry processEntry : chromatogramProcessingEntries) {
				//
				String processorName = "";
				if(!processEntry.getProcessorId().equals("")) {
					processorName = processTypeSupport.getProcessorName(processEntry);
					processorNames.add(processorName);
				}
			}
			chromatogramReport.setProcessorNames(processorNames);
			chromatogramReport.setNotes(notes);
			//
			ChromatogramReportWriter chromatogramReportWriter = new ChromatogramReportWriter();
			chromatogramReportWriter.write(chromatogramEvaluationReportFile, chromatogramReport, monitor);
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	/**
	 * This methods processes the chromatogram.
	 * The chromatogram processing entries stored in the settings will be used.
	 * 
	 * @param chromatogramSelectionMSD
	 * @param monitor
	 */
	public void processChromatogram(IChromatogramSelectionMSD chromatogramSelectionMSD, IProgressMonitor monitor) {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		//
		List<IChromatogramProcessEntry> chromatogramProcessingEntries = new ArrayList<IChromatogramProcessEntry>();
		chromatogramProcessingEntries.add(new ChromatogramProcessEntry(ChromatogramFilterTypeSupplierMSD.CATEGORY, preferences.get(PreferenceSupplier.P_EVALUATION_CHROMATOGRAM_MSD_FILTER, PreferenceSupplier.DEF_EVALUATION_CHROMATOGRAM_MSD_FILTER)));
		chromatogramProcessingEntries.add(new ChromatogramProcessEntry(ChromatogramFilterTypeSupplier.CATEGORY, preferences.get(PreferenceSupplier.P_EVALUATION_CHROMATOGRAM_FILTER, PreferenceSupplier.DEF_EVALUATION_CHROMATOGRAM_FILTER)));
		chromatogramProcessingEntries.add(new ChromatogramProcessEntry(BaselineDetectorTypeSupplier.CATEGORY, preferences.get(PreferenceSupplier.P_EVALUATION_BASELINE_DETECTOR, PreferenceSupplier.DEF_EVALUATION_BASELINE_DETECTOR)));
		chromatogramProcessingEntries.add(new ChromatogramProcessEntry(PeakDetectorTypeSupplierMSD.CATEGORY, preferences.get(PreferenceSupplier.P_EVALUATION_PEAK_DETECTOR, PreferenceSupplier.DEF_EVALUATION_PEAK_DETECTOR)));
		chromatogramProcessingEntries.add(new ChromatogramProcessEntry(PeakIntegratorTypeSupplier.CATEGORY, preferences.get(PreferenceSupplier.P_EVALUATION_PEAK_INTEGRATOR, PreferenceSupplier.DEF_EVALUATION_PEAK_INTEGRATOR)));
		chromatogramProcessingEntries.add(new ChromatogramProcessEntry(ChromatogramCalculatorTypeSupplier.CATEGORY, preferences.get(PreferenceSupplier.P_EVALUATION_CHROMATOGRAM_CALCULATOR, PreferenceSupplier.DEF_EVALUATION_CHROMATOGRAM_CALCULATOR)));
		chromatogramProcessingEntries.add(new ChromatogramProcessEntry(PeakIdentifierTypeSupplierMSD.CATEGORY, preferences.get(PreferenceSupplier.P_EVALUATION_PEAK_IDENTIFIER, PreferenceSupplier.DEF_EVALUATION_PEAK_IDENTIFIER)));
		//
		processChromatogram(chromatogramSelectionMSD, chromatogramProcessingEntries, monitor);
	}

	/**
	 * This methods processes the chromatogram using the processing entries.
	 * 
	 * @param chromatogramSelectionMSD
	 * @param chromatogramProcessingEntries
	 * @param monitor
	 */
	public void processChromatogram(IChromatogramSelectionMSD chromatogramSelectionMSD, List<IChromatogramProcessEntry> chromatogramProcessingEntries, IProgressMonitor monitor) {

		ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();
		for(IChromatogramProcessEntry processEntry : chromatogramProcessingEntries) {
			if(!processEntry.getProcessorId().equals("")) {
				processTypeSupport.applyProcessor(chromatogramSelectionMSD, processEntry, monitor);
			}
		}
	}
}
