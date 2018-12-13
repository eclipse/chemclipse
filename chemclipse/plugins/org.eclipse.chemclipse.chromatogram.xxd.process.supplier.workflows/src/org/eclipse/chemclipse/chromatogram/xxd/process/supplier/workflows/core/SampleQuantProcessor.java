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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter.SampleQuantWriter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter.TargetsReader;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.SampleQuantSubstance;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.converter.model.IReportRowModel;
import org.eclipse.chemclipse.converter.report.ReportConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class SampleQuantProcessor {

	public static final String REPORT_FILE_EXTENSION = ".sqr";
	//
	private static final Logger logger = Logger.getLogger(SampleQuantProcessor.class);
	//
	private static final String EXTENSION_POINT_ID_RTERES = "net.openchrom.msd.converter.supplier.agilent.hp.report.rteres";
	private static final String EXTENSION_POINT_ID_SUMRPT = "net.openchrom.msd.converter.supplier.agilent.hp.report.sumrpt";
	//
	private static final String CHROMATOGRAM_CONVERTER_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	private static final String CHROMATOGRAM_FILE_EXTENSION = ".ocb";
	//
	private Pattern pattern = Pattern.compile("(\\d+)-(\\d\\d)-(\\d)"); // CAS#

	public void createSampleQuantReport(File sampleQuantReportFile, ISampleQuantReport sampleQuantReport, String pathChromatogramOriginal, IProgressMonitor monitor) {

		try {
			/*
			 * Load the chromatogram
			 */
			sampleQuantReport.setPathChromatogramOriginal(pathChromatogramOriginal);
			File chromatogramImportFile = new File(pathChromatogramOriginal);
			IProcessingInfo processingInfoImport = ChromatogramConverterMSD.getInstance().convert(chromatogramImportFile, monitor);
			IChromatogramMSD chromatogramMSD = processingInfoImport.getProcessingResult(IChromatogramMSD.class);
			//
			sampleQuantReport.setName(chromatogramMSD.getName());
			sampleQuantReport.setDataName(chromatogramMSD.getDataName());
			sampleQuantReport.setDate(ValueFormat.getDateFormatEnglish().format(chromatogramMSD.getDate()));
			sampleQuantReport.setOperator(chromatogramMSD.getOperator());
			sampleQuantReport.setMiscInfo(chromatogramMSD.getMiscInfo());
			/*
			 * Identify scans
			 */
			List<IScanMSD> scansToIdentify = new ArrayList<IScanMSD>();
			List<ISampleQuantSubstance> sampleQuantSubstances = sampleQuantReport.getSampleQuantSubstances();
			for(ISampleQuantSubstance sampleQuantSubstance : sampleQuantSubstances) {
				int maxScan = sampleQuantSubstance.getMaxScan();
				IScan scan = chromatogramMSD.getScan(maxScan);
				if(scan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)scan;
					scansToIdentify.add(scanMSD);
				}
			}
			String identifierId = PreferenceSupplier.INSTANCE().getPreferences().get(PreferenceSupplier.P_SAMPLEQUANT_SCAN_IDENTIFIER, PreferenceSupplier.DEF_SAMPLEQUANT_SCAN_IDENTIFIER);
			MassSpectrumIdentifier.identify(scansToIdentify, identifierId, monitor);
			/*
			 * Export the chromatogram
			 */
			File chromatogramExportFile = new File(sampleQuantReportFile.getAbsolutePath().replace(REPORT_FILE_EXTENSION, CHROMATOGRAM_FILE_EXTENSION));
			IProcessingInfo processingInfoExport = ChromatogramConverterMSD.getInstance().convert(chromatogramExportFile, chromatogramMSD, CHROMATOGRAM_CONVERTER_ID, monitor);
			sampleQuantReport.setPathChromatogramEdited(processingInfoExport.getProcessingResult(File.class).getAbsolutePath());
			/*
			 * Write sample quant report
			 */
			SampleQuantWriter sampleQuantWriter = new SampleQuantWriter();
			sampleQuantWriter.write(sampleQuantReportFile, sampleQuantReport, monitor);
			//
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	/**
	 * The additional report data file could be null if no additional data is available.
	 * 
	 * @param fileAdditionalReportData
	 * @param fileAreaPercentReport
	 * @param fileQuantitationReport
	 * @return List<ISampleQuantSubstance>
	 */
	public List<ISampleQuantSubstance> extractSampleQuantSubstances(File fileAdditionalReportData, File fileAreaPercentReport, File fileQuantitationReport) {

		List<ISampleQuantSubstance> sampleQuantSubstances = new ArrayList<ISampleQuantSubstance>();
		/*
		 * Extract the report data.
		 */
		Map<String, String> reportCompoundCasMap = extractReportCompoundCasMap(fileAdditionalReportData);
		IReportRowModel reportRowModelAreaPercent = extractReportRowModel(fileAreaPercentReport, EXTENSION_POINT_ID_RTERES);
		IReportRowModel reportRowModelQuantitation = extractReportRowModel(fileQuantitationReport, EXTENSION_POINT_ID_SUMRPT);
		/*
		 * Create a temporary area percent map.
		 */
		try {
			final int INDEX_RTRERES_RT = getRowIndex(reportRowModelAreaPercent, "R.T. min");
			final int INDEX_RTRERES_MAX_SCAN = getRowIndex(reportRowModelAreaPercent, "max scan");
			//
			final int INDEX_SUMRPT_ID = getRowIndex(reportRowModelQuantitation, "Id");
			final int INDEX_SUMRPT_NAME = getRowIndex(reportRowModelQuantitation, "Compound");
			final int INDEX_SUMRPT_RT = getRowIndex(reportRowModelQuantitation, "R.T.");
			final int INDEX_SUMRPT_CONCENTRATION = getRowIndex(reportRowModelQuantitation, "Conc");
			final int INDEX_SUMRPT_UNITS = getRowIndex(reportRowModelQuantitation, "Units");
			final int INDEX_SUMRPT_MISC = getRowIndex(reportRowModelQuantitation, "Misc");
			final int INDEX_SUMRPT_QVALUE = getRowIndex(reportRowModelQuantitation, "Qvalue");
			//
			Map<Integer, List<String>> areaPercentRowMap = new HashMap<Integer, List<String>>();
			for(List<String> row : reportRowModelAreaPercent) {
				int retentionTime = (int)(getValueDouble(row.get(INDEX_RTRERES_RT).trim()) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
				areaPercentRowMap.put(retentionTime, row);
			}
			//
			for(int index = 0; index < reportRowModelQuantitation.size(); index++) {
				/*
				 * Skip the headline.
				 */
				if(index == 0) {
					continue;
				}
				/*
				 * Get the data.
				 */
				List<String> row = reportRowModelQuantitation.get(index);
				String name = row.get(INDEX_SUMRPT_NAME).trim();
				String misc = row.get(INDEX_SUMRPT_MISC).trim();
				String retentionTimeInMinutes = row.get(INDEX_SUMRPT_RT);
				//
				int retentionTime = (int)(getValueDouble(retentionTimeInMinutes) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
				List<String> rowAreaPercent = getRowByCompound(areaPercentRowMap, retentionTime, 1000);
				//
				ISampleQuantSubstance sampleQuantSubstance = new SampleQuantSubstance();
				sampleQuantSubstance.setId(getValueInteger(rowAreaPercent.get(INDEX_SUMRPT_ID)));
				sampleQuantSubstance.setName(name);
				/*
				 * CAS#
				 */
				Matcher matcher = pattern.matcher(name);
				if(matcher.find()) {
					sampleQuantSubstance.setCasNumber(matcher.group());
				} else {
					sampleQuantSubstance.setCasNumber(getCasNumber(name, reportCompoundCasMap));
				}
				//
				sampleQuantSubstance.setMaxScan(getMaxScan(rowAreaPercent, INDEX_RTRERES_MAX_SCAN));
				sampleQuantSubstance.setConcentration(getValueDouble(row.get(INDEX_SUMRPT_CONCENTRATION)));
				sampleQuantSubstance.setUnit(row.get(INDEX_SUMRPT_UNITS));
				sampleQuantSubstance.setMisc(misc);
				sampleQuantSubstance.setMatchQuality(getValueDouble(row.get(INDEX_SUMRPT_QVALUE)));
				sampleQuantSubstance.setMinMatchQuality(PreferenceSupplier.INSTANCE().getPreferences().getDouble(PreferenceSupplier.P_SAMPLEQUANT_MIN_MATCH_QUALITY, PreferenceSupplier.DEF_SAMPLEQUANT_MIN_MATCH_QUALITY));
				//
				sampleQuantSubstances.add(sampleQuantSubstance);
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		return sampleQuantSubstances;
	}

	private String getCasNumber(String name, Map<String, String> reportCompoundCasMap) {

		return (reportCompoundCasMap.get(name) != null) ? reportCompoundCasMap.get(name) : "";
	}

	private int getMaxScan(List<String> rowAreaPercent, int INDEX_RTRERES_MAX_SCAN) {

		if(rowAreaPercent != null) {
			return getValueInteger(rowAreaPercent.get(INDEX_RTRERES_MAX_SCAN));
		} else {
			return 0;
		}
	}

	private int getRowIndex(IReportRowModel reportRowModel, String columnName) throws Exception {

		int index = reportRowModel.getColumnIndex(columnName);
		if(index == -1) {
			throw new Exception("The column with the given name could't be found: " + columnName);
		}
		return index;
	}

	private Map<String, String> extractReportCompoundCasMap(File fileAdditionalReportData) {

		Map<String, String> reportCompoundCasMap = new HashMap<String, String>();
		if(fileAdditionalReportData != null && fileAdditionalReportData.exists()) {
			TargetsReader targetsReader = new TargetsReader();
			reportCompoundCasMap = targetsReader.getCompoundCasMap(fileAdditionalReportData);
		}
		return reportCompoundCasMap;
	}

	private IReportRowModel extractReportRowModel(File fileImport, String extensionPointId) {

		IReportRowModel reportRowModel;
		IProcessingInfo processingInfo = ReportConverter.convert(fileImport, extensionPointId, new NullProgressMonitor());
		try {
			reportRowModel = processingInfo.getProcessingResult(IReportRowModel.class);
		} catch(TypeCastException e) {
			reportRowModel = null;
		}
		return reportRowModel;
	}

	private int getValueInteger(String value) {

		try {
			return Integer.parseInt(value);
		} catch(Exception e) {
			return 0;
		}
	}

	private double getValueDouble(String value) {

		try {
			return Double.parseDouble(value);
		} catch(Exception e) {
			return 0.0d;
		}
	}

	private List<String> getRowByCompound(Map<Integer, List<String>> areaPercentRowMap, int retentionTime, int deltaRetentionTime) {

		int retentionTimeLeftBorder = retentionTime - deltaRetentionTime;
		int retentionTimeRightBorder = retentionTime + deltaRetentionTime;
		//
		for(Map.Entry<Integer, List<String>> entry : areaPercentRowMap.entrySet()) {
			int retentionTimeTarget = entry.getKey();
			if(retentionTimeTarget >= retentionTimeLeftBorder && retentionTimeTarget <= retentionTimeRightBorder) {
				return entry.getValue();
			}
		}
		return null;
	}
}
