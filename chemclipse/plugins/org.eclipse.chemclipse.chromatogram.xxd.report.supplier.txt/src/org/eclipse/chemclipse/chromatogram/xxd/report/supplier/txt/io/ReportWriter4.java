/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class ReportWriter4 {

	private static final int NUMBER_OF_IONS_TO_PRINT = 5;
	private static final String DELIMITER = "\t";
	private static final String ION_DELIMITER = " ";
	private static final String NO_VALUE = "--";
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0####");
	//
	private IonAbundanceComparator ionComparator = new IonAbundanceComparator(SortOrder.DESC);
	private PeakRetentionTimeComparator peakComparator = new PeakRetentionTimeComparator(SortOrder.ASC);

	public void generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IProgressMonitor monitor) throws IOException {

		try (PrintWriter printWriter = new PrintWriter(new FileWriter(file, append))) {
			for(IChromatogram<? extends IPeak> chromatogram : chromatograms) {
				printHeader(printWriter, chromatogram);
				reportChromatogram(printWriter, chromatogram, monitor);
			}
		}
	}

	private void printHeader(PrintWriter printWriter, IChromatogramOverview chromatogramOverview) {

		printHeaderLine(printWriter, "Name", chromatogramOverview.getName());
	}

	private void printHeaderLine(PrintWriter printWriter, String key, String value) {

		printWriter.print(key);
		printWriter.print(": ");
		printWriter.println(value);
	}

	/**
	 * The report chromatogram is responsible how to print each section.
	 * 
	 * @param printWriter
	 * @param chromatogram
	 * @param monitor
	 */
	private void reportChromatogram(PrintWriter printWriter, IChromatogram<?> chromatogram, IProgressMonitor monitor) {

		List<IPeak> peaks = new ArrayList<>(chromatogram.getPeaks());
		Collections.sort(peaks, peakComparator);
		reportPeaksOverview(printWriter, peaks, "Peaks are not available.");
	}

	private void reportPeaksOverview(PrintWriter printWriter, List<IPeak> peaks, String messageNoResults) {

		if(!peaks.isEmpty()) {
			printPeaksOverview(printWriter, peaks);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printPeaksOverview(PrintWriter printWriter, List<IPeak> peaks) {

		/*
		 * Headline
		 */
		printWriter.print("[#]");
		printWriter.print(DELIMITER);
		printWriter.print("RT (Min)");
		printWriter.print(DELIMITER);
		printWriter.print("RI");
		printWriter.print(DELIMITER);
		printWriter.print("Start RT (Min)");
		printWriter.print(DELIMITER);
		printWriter.print("Stop RT (Min)");
		printWriter.print(DELIMITER);
		printWriter.print("Tailing");
		printWriter.print(DELIMITER);
		printWriter.print("Width (at 50% peak height by inflection points)");
		printWriter.print(DELIMITER);
		printWriter.print("Integrated Area");
		printWriter.print(DELIMITER);
		printWriter.print("S/N");
		printWriter.print(DELIMITER);
		printWriter.print("Leading");
		printWriter.print(DELIMITER);
		printWriter.print("Tailing");
		printWriter.print(DELIMITER);
		printWriter.print("Scan #");
		printWriter.print(DELIMITER);
		printWriter.print("Purity");
		printWriter.print(DELIMITER);
		printWriter.print("Integrator");
		printWriter.print(DELIMITER);
		printWriter.print("Detector");
		printWriter.print(DELIMITER);
		printWriter.print("Model");
		printWriter.print(DELIMITER);
		printWriter.print("Trace ...");
		printWriter.println("");
		/*
		 * Print each peak.
		 */
		int counter = 1;
		for(IPeak peak : peaks) {
			reportPeak(printWriter, peak, counter++);
		}
	}

	private void reportPeak(PrintWriter printWriter, IPeak peak, int number) {

		IPeakModel peakModel = peak.getPeakModel();
		IChromatogramPeak chromatogramPeak = null;
		if(peak instanceof IChromatogramPeak reportedPeak) {
			chromatogramPeak = reportedPeak;
		}
		//
		printWriter.print("[" + number + "]");
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getPeakMaximum().getRetentionIndex()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getTailing()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getWidthByInflectionPoints(0.5f) / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peak.getIntegratedArea()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format((chromatogramPeak != null) ? chromatogramPeak.getSignalToNoiseRatio() : NO_VALUE));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getLeading()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getTailing()));
		printWriter.print(DELIMITER);
		printWriter.print((chromatogramPeak != null) ? Integer.toString(chromatogramPeak.getScanMax()) : NO_VALUE);
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format((chromatogramPeak != null) ? chromatogramPeak.getPurity() : NO_VALUE));
		printWriter.print(DELIMITER);
		printWriter.print(peak.getIntegratorDescription());
		printWriter.print(DELIMITER);
		printWriter.print(peak.getDetectorDescription());
		printWriter.print(DELIMITER);
		printWriter.print(peak.getModelDescription());
		/*
		 * Print the highest traces values (m/z abundance or wavelength intensity)
		 */
		String highestTraces;
		if(peakModel instanceof IPeakModelMSD peakModelMSD) {
			highestTraces = extractHighestIons(peakModelMSD);
		} else {
			highestTraces = NO_VALUE;
		}
		printWriter.print(DELIMITER);
		printWriter.print(highestTraces);
		/*
		 * Close the line.
		 */
		printWriter.println("");
	}

	private String extractHighestIons(IPeakModelMSD peakModelMSD) {

		IPeakMassSpectrum peakMassSpectrum = peakModelMSD.getPeakMassSpectrum();
		List<IIon> ions = new ArrayList<>(peakMassSpectrum.getIons());
		/*
		 * Check how many ions shall be printed.
		 */
		int numberOfIonsToPrint = (ions.size() < NUMBER_OF_IONS_TO_PRINT) ? ions.size() : NUMBER_OF_IONS_TO_PRINT;
		Collections.sort(ions, ionComparator);
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < numberOfIonsToPrint; i++) {
			IIon ion = ions.get(i);
			if(ion.getIon() == IIon.TIC_ION) {
				builder.append(IIon.TIC_DESCRIPTION);
			} else {
				builder.append(decimalFormat.format(ion.getIon()));
			}
			builder.append(ION_DELIMITER);
		}
		builder.append("...");
		return builder.toString();
	}
}
