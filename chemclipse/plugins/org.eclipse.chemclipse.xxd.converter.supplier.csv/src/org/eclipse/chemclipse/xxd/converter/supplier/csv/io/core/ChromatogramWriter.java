/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.csv.io.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignals;
import org.eclipse.chemclipse.xxd.converter.supplier.csv.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramWriter {

	public static final String DESCRIPTION = "Chromatogram";
	public static final String FILE_EXTENSION = ".csv";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
	public static final String RT_MILLISECONDS_COLUMN = "RT(milliseconds)";
	public static final String RT_MINUTES_COLUMN = "RT(minutes) - NOT USED BY IMPORT";
	public static final String RI_COLUMN = "RI";
	public static final String TIC_COLUMN = "TIC";

	public void writeChromatogram(File file, IChromatogram<? extends IPeak> chromatogram, IProgressMonitor monitor) throws IOException {

		if(chromatogram instanceof IChromatogramCSD chromatogramCSD) {
			writeChromatogram(file, chromatogramCSD, monitor);
		} else if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
			writeChromatogram(file, chromatogramMSD, monitor);
		} else if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
			writeChromatogram(file, chromatogramWSD, monitor);
		}
	}

	private void writeChromatogram(File file, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws IOException {

		try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(file), CSVFormat.EXCEL)) {
			writeTIC(csvPrinter, chromatogram);
		} catch(ChromatogramIsNullException e) {
			throw new IOException("The chromatogram is null.");
		}
	}

	private void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(file), CSVFormat.EXCEL)) {
			if(PreferenceSupplier.isExportUseTic()) {
				writeTIC(csvPrinter, chromatogram);
			} else {
				/*
				 * XIC
				 */
				IExtractedIonSignalExtractor extractedSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
				IExtractedIonSignals extractedSignals = extractedSignalExtractor.getExtractedIonSignals();
				int startIon = extractedSignals.getStartIon();
				int stopIon = extractedSignals.getStopIon();
				writeHeaderXIC(csvPrinter, startIon, stopIon);
				writeScansXIC(csvPrinter, extractedSignals, startIon, stopIon);
			}
		} catch(ChromatogramIsNullException e) {
			throw new IOException("The chromatogram is null.");
		}
	}

	private void writeChromatogram(File file, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws IOException {

		try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(file), CSVFormat.EXCEL)) {
			if(PreferenceSupplier.isExportUseTic()) {
				writeTIC(csvPrinter, chromatogram);
			} else {
				/*
				 * XWC
				 */
				IExtractedWavelengthSignalExtractor extractedSignalExtractor = new ExtractedWavelengthSignalExtractor(chromatogram);
				IExtractedWavelengthSignals extractedSignals = extractedSignalExtractor.getExtractedWavelengthSignals();
				int startWavelength = extractedSignals.getStartWavelength();
				int stopWavelength = extractedSignals.getStopWavelength();
				writeHeaderXIC(csvPrinter, startWavelength, stopWavelength);
				writeScansXIC(csvPrinter, extractedSignals, startWavelength, stopWavelength);
			}
		} catch(ChromatogramIsNullException e) {
			throw new IOException("The chromatogram is null.");
		}
	}

	private void writeTIC(CSVPrinter csvPrinter, IChromatogram<? extends IPeak> chromatogram) throws IOException {

		ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
		ITotalScanSignals totalScanSignals = totalIonSignalExtractor.getTotalScanSignals();
		writeHeaderTIC(csvPrinter);
		writeScansTIC(csvPrinter, totalScanSignals);
	}

	private void writeHeaderTIC(CSVPrinter csvFilePrinter) throws IOException {

		/*
		 * Write the header.
		 * RT(milliseconds), RT(minutes) - NOT USED BY IMPORT, RI, TIC
		 */
		List<String> headerList = new ArrayList<>();
		headerList.add(RT_MILLISECONDS_COLUMN);
		headerList.add(RT_MINUTES_COLUMN);
		headerList.add(RI_COLUMN);
		headerList.add(TIC_COLUMN);
		csvFilePrinter.printRecord(headerList);
	}

	private void writeHeaderXIC(CSVPrinter csvFilePrinter, int startTrace, int stopTrace) throws IOException {

		/*
		 * Write the header.
		 * RT(milliseconds), RT(minutes) - NOT USED BY IMPORT, RI, trace 18, ...
		 */
		List<String> headerList = new ArrayList<>();
		headerList.add(RT_MILLISECONDS_COLUMN);
		headerList.add(RT_MINUTES_COLUMN);
		headerList.add(RI_COLUMN);
		for(Integer ion = startTrace; ion <= stopTrace; ion++) {
			headerList.add(ion.toString());
		}
		csvFilePrinter.printRecord(headerList);
	}

	private void writeScansTIC(CSVPrinter csvPrinter, ITotalScanSignals totalScanSignals) throws IOException {

		/*
		 * Write the data.
		 */
		List<Number> scanValues;
		for(ITotalScanSignal totalScanSignal : totalScanSignals.getTotalScanSignals()) {
			scanValues = new ArrayList<>();
			/*
			 * RT (milliseconds)
			 * RT(minutes)
			 * RI
			 */
			int milliseconds = totalScanSignal.getRetentionTime();
			scanValues.add(milliseconds);
			scanValues.add(milliseconds / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
			scanValues.add(totalScanSignal.getRetentionIndex());
			scanValues.add(totalScanSignal.getTotalSignal());
			csvPrinter.printRecord(scanValues);
		}
	}

	private void writeScansXIC(CSVPrinter csvPrinter, IExtractedIonSignals extractedSignals, int startIon, int stopIon) throws IOException {

		for(IExtractedIonSignal extractedIonSignal : extractedSignals.getExtractedIonSignals()) {
			/*
			 * RT (milliseconds)
			 * RT(minutes)
			 * RI
			 */
			List<Number> scanValues = new ArrayList<>();
			int milliseconds = extractedIonSignal.getRetentionTime();
			scanValues.add(milliseconds);
			scanValues.add(milliseconds / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
			scanValues.add(extractedIonSignal.getRetentionIndex());
			/*
			 * ion data
			 */
			for(int ion = startIon; ion <= stopIon; ion++) {
				scanValues.add(extractedIonSignal.getAbundance(ion));
			}
			csvPrinter.printRecord(scanValues);
		}
	}

	private void writeScansXIC(CSVPrinter csvPrinter, IExtractedWavelengthSignals extractedSignals, int startWavelength, int stopWavelength) throws IOException {

		for(IExtractedWavelengthSignal extractedSignal : extractedSignals.getExtractedWavelengthSignals()) {
			/*
			 * RT (milliseconds)
			 * RT(minutes)
			 * RI
			 */
			List<Number> scanValues = new ArrayList<>();
			int milliseconds = extractedSignal.getRetentionTime();
			scanValues.add(milliseconds);
			scanValues.add(milliseconds / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
			scanValues.add(extractedSignal.getRetentionIndex());
			/*
			 * ion data
			 */
			for(int ion = startWavelength; ion <= stopWavelength; ion++) {
				scanValues.add(extractedSignal.getAbundance(ion));
			}
			csvPrinter.printRecord(scanValues);
		}
	}
}