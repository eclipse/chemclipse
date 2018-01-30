/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.io.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class ChromatogramWriter extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	private static final String DELIMITER = ",";
	//
	private PeakRetentionTimeComparator chromatogramPeakComparator;
	private TargetExtendedComparator targetExtendedComparator;
	private DecimalFormat decimalFormat;

	public ChromatogramWriter() {
		chromatogramPeakComparator = new PeakRetentionTimeComparator(SortOrder.DESC);
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
	}

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		/*
		 * Create the list writer.
		 */
		FileWriter writer = new FileWriter(file);
		CsvPreference csvPreference = new CsvPreference.Builder('"', DELIMITER.charAt(0), "\n").build();
		ICsvListWriter csvListWriter = new CsvListWriter(writer, csvPreference);
		/*
		 * Header
		 */
		List<String> headerValues = new ArrayList<String>();
		headerValues.add("mzLo");
		headerValues.add("mzHi");
		headerValues.add("rtLo");
		headerValues.add("rtHi");
		headerValues.add("color");
		headerValues.add("opacity");
		headerValues.add("identification");
		headerValues.add("matchFactor");
		headerValues.add("reverseMatchFactor");
		csvListWriter.write(headerValues);
		/*
		 * Data
		 */
		try {
			List<IChromatogramPeakMSD> chromatogramPeaks = new ArrayList<>(chromatogram.getPeaks());
			Collections.sort(chromatogramPeaks, chromatogramPeakComparator);
			//
			for(IChromatogramPeakMSD chromatogramPeak : chromatogramPeaks) {
				IPeakMassSpectrum peakMassSpectrum = chromatogramPeak.getExtractedMassSpectrum();
				List<IPeakTarget> peakTargets = new ArrayList<>(chromatogramPeak.getTargets());
				Collections.sort(peakTargets, targetExtendedComparator);
				//
				List<String> targetValues = new ArrayList<String>();
				targetValues.add(decimalFormat.format(peakMassSpectrum.getLowestIon().getIon())); // mzLo
				targetValues.add(decimalFormat.format(peakMassSpectrum.getHighestIon().getIon())); // mzHi
				targetValues.add(decimalFormat.format(chromatogramPeak.getPeakModel().getStartRetentionTime() / IChromatogramMSD.MINUTE_CORRELATION_FACTOR)); // rtLo
				targetValues.add(decimalFormat.format(chromatogramPeak.getPeakModel().getStopRetentionTime() / IChromatogramMSD.MINUTE_CORRELATION_FACTOR)); // rtHigh
				targetValues.add("#A70000"); // color
				targetValues.add("0.75"); // opacity
				if(peakTargets.size() > 0) {
					IPeakTarget peakTarget = peakTargets.get(0);
					targetValues.add(peakTarget.getLibraryInformation().getName()); // identification
					targetValues.add(decimalFormat.format(peakTarget.getComparisonResult().getMatchFactor())); // matchFactor
					targetValues.add(decimalFormat.format(peakTarget.getComparisonResult().getReverseMatchFactor())); // reverseMatchFactor
				} else {
					targetValues.add(""); // identification
					targetValues.add(""); // matchFactor
					targetValues.add(""); // reverseMatchFactor
				}
				csvListWriter.write(targetValues);
			}
		} catch(Exception e) {
			throw new IOException(e);
		} finally {
			csvListWriter.close();
		}
	}
}
