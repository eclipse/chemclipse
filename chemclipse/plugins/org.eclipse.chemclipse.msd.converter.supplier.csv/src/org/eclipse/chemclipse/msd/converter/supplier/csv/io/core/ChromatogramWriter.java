/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.io.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.csv.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.msd.model.xic.ITotalIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.TotalIonSignalExtractor;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	public static final String RT_MILLISECONDS_COLUMN = "RT(milliseconds)";
	public static final String RT_MINUTES_COLUMN = "RT(minutes) - NOT USED BY IMPORT";
	public static final String RI_COLUMN = "RI";
	public static final String TIC_COLUMN = "TIC";

	public ChromatogramWriter() {
	}

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		/*
		 * Create the list writer.
		 */
		FileWriter writer = new FileWriter(file);
		CSVPrinter csvFilePrinter = new CSVPrinter(writer, CSVFormat.EXCEL);
		/*
		 * Get the chromatographic data.
		 */
		try {
			/*
			 * TIC / XIC
			 */
			if(PreferenceSupplier.isUseTic()) {
				ITotalIonSignalExtractor totalIonSignalExtractor = new TotalIonSignalExtractor(chromatogram);
				ITotalScanSignals totalScanSignals = totalIonSignalExtractor.getTotalScanSignals();
				writeHeaderTIC(csvFilePrinter);
				writeScansTIC(csvFilePrinter, totalScanSignals);
			} else {
				IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
				IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals();
				int startIon = extractedIonSignals.getStartIon();
				int stopIon = extractedIonSignals.getStopIon();
				writeHeaderXIC(csvFilePrinter, startIon, stopIon);
				writeScansXIC(csvFilePrinter, extractedIonSignals, startIon, stopIon);
			}
		} catch(ChromatogramIsNullException e) {
			throw new IOException("The chromatogram is null.");
		} finally {
			if(csvFilePrinter != null) {
				csvFilePrinter.close();
			}
		}
	}

	private void writeHeaderTIC(CSVPrinter csvFilePrinter) throws IOException {

		/*
		 * Write the header.
		 * RT(milliseconds), RT(minutes) - NOT USED BY IMPORT, RI, ion 18, ...
		 */
		List<String> headerList = new ArrayList<String>();
		headerList.add(RT_MILLISECONDS_COLUMN);
		headerList.add(RT_MINUTES_COLUMN);
		headerList.add(RI_COLUMN);
		headerList.add(TIC_COLUMN);
		csvFilePrinter.printRecord(headerList);
	}

	private void writeHeaderXIC(CSVPrinter csvFilePrinter, int startIon, int stopIon) throws IOException {

		/*
		 * Write the header.
		 * RT(milliseconds), RT(minutes) - NOT USED BY IMPORT, RI, ion 18, ...
		 */
		List<String> headerList = new ArrayList<String>();
		headerList.add(RT_MILLISECONDS_COLUMN);
		headerList.add(RT_MINUTES_COLUMN);
		headerList.add(RI_COLUMN);
		for(Integer ion = startIon; ion <= stopIon; ion++) {
			headerList.add(ion.toString());
		}
		csvFilePrinter.printRecord(headerList);
	}

	private void writeScansTIC(CSVPrinter csvFilePrinter, ITotalScanSignals totalScanSignals) throws IOException {

		/*
		 * Write the data.
		 */
		List<Number> scanValues;
		for(ITotalScanSignal totalScanSignal : totalScanSignals.getTotalScanSignals()) {
			scanValues = new ArrayList<Number>();
			/*
			 * RT (milliseconds)
			 * RT(minutes)
			 * RI
			 */
			int milliseconds = totalScanSignal.getRetentionTime();
			scanValues.add(milliseconds);
			scanValues.add(milliseconds / IChromatogramMSD.MINUTE_CORRELATION_FACTOR);
			scanValues.add(totalScanSignal.getRetentionIndex());
			scanValues.add(totalScanSignal.getTotalSignal());
			csvFilePrinter.printRecord(scanValues);
		}
	}

	private void writeScansXIC(CSVPrinter csvFilePrinter, IExtractedIonSignals extractedIonSignals, int startIon, int stopIon) throws IOException {

		/*
		 * Write the data.
		 */
		List<Number> scanValues;
		for(IExtractedIonSignal extractedIonSignal : extractedIonSignals.getExtractedIonSignals()) {
			scanValues = new ArrayList<Number>();
			/*
			 * RT (milliseconds)
			 * RT(minutes)
			 * RI
			 */
			int milliseconds = extractedIonSignal.getRetentionTime();
			scanValues.add(milliseconds);
			scanValues.add(milliseconds / IChromatogramMSD.MINUTE_CORRELATION_FACTOR);
			scanValues.add(extractedIonSignal.getRetentionIndex());
			/*
			 * ion data
			 */
			for(int ion = startIon; ion <= stopIon; ion++) {
				scanValues.add(extractedIonSignal.getAbundance(ion));
			}
			csvFilePrinter.printRecord(scanValues);
		}
	}
}
