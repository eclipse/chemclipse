/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.settings.ReportSettings1;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.support.PeakQuantitation;
import org.eclipse.chemclipse.model.support.PeakQuantitations;
import org.eclipse.chemclipse.model.support.PeakQuantitationsExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ReportWriter1 {

	private static final int NUMBER_OF_IONS_TO_PRINT = 5;
	private static final String DELIMITER = "\t";
	private static final String ION_DELIMITER = " ";
	private static final String RESULTS_DELIMITER = "---";
	private static final String NO_VALUE = "--";
	private static final String HORIZONTAL_RULE = "------------------------------";
	//
	private PeakQuantitationsExtractor peakQuantitationsExtractor = new PeakQuantitationsExtractor();
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0####");
	private DateFormat dateFormat = ValueFormat.getDateFormatEnglish();
	//
	private IonAbundanceComparator ionComparator = new IonAbundanceComparator(SortOrder.DESC);
	private PeakRetentionTimeComparator peakComparator = new PeakRetentionTimeComparator(SortOrder.ASC);

	public void generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, ReportSettings1 reportSettings, IProgressMonitor monitor) throws IOException {

		try (PrintWriter printWriter = new PrintWriter(new FileWriter(file, append))) {
			printWriter.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			printWriter.println("ASCII Report");
			printWriter.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			//
			for(IChromatogram<? extends IPeak> chromatogram : chromatograms) {
				printHeader(printWriter, chromatogram, monitor);
				reportChromatogram(printWriter, chromatogram, monitor);
			}
		}
	}

	private void printHeader(PrintWriter printWriter, IChromatogramOverview chromatogramOverview, IProgressMonitor monitor) {

		printHeaderLine(printWriter, "Name", chromatogramOverview.getName());
		printHeaderLine(printWriter, "Data Name", chromatogramOverview.getDataName());
		printHeaderLine(printWriter, "Operator", chromatogramOverview.getOperator());
		/*
		 * Date
		 */
		Date date = chromatogramOverview.getDate();
		if(date != null) {
			printHeaderLine(printWriter, "Date", dateFormat.format(chromatogramOverview.getDate()));
		} else {
			printHeaderLine(printWriter, "Date", "");
		}
		//
		printHeaderLine(printWriter, "Info", chromatogramOverview.getShortInfo());
		printHeaderLine(printWriter, "Misc", chromatogramOverview.getMiscInfo());
		printHeaderLine(printWriter, "Misc (separated)", chromatogramOverview.getMiscInfoSeparated());
		printHeaderLine(printWriter, "Details", chromatogramOverview.getDetailedInfo());
		printHeaderLine(printWriter, "Scans", Integer.toString(chromatogramOverview.getNumberOfScans()));
		printHeaderLine(printWriter, "Start RT (min)", decimalFormat.format(chromatogramOverview.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		printHeaderLine(printWriter, "Stop RT (min)", decimalFormat.format(chromatogramOverview.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		printHeaderLine(printWriter, "Barcode", chromatogramOverview.getBarcode());
		printWriter.println("");
		printWriter.println(">>>");
		printWriter.println("");
		/*
		 * Header Entries
		 */
		for(Map.Entry<String, String> entry : chromatogramOverview.getHeaderDataMap().entrySet()) {
			printHeaderLine(printWriter, entry.getKey(), entry.getValue());
		}
		//
		printWriter.println(HORIZONTAL_RULE);
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

		/*
		 * Print
		 */
		List<IPeak> peaks = new ArrayList<>(chromatogram.getPeaks());
		Collections.sort(peaks, peakComparator);
		//
		printWriter.println("");
		printWriter.println("NAME: " + chromatogram.getName());
		printWriter.println("TYPE: " + getChromatogramType(chromatogram));
		printWriter.println(HORIZONTAL_RULE);
		printWriter.println("Integrated Chromatogram Area: " + decimalFormat.format(chromatogram.getChromatogramIntegratedArea()));
		printWriter.println("Integrated Background Area: " + decimalFormat.format(chromatogram.getBackgroundIntegratedArea()));
		printWriter.println("Integrated Peak Area: " + decimalFormat.format(chromatogram.getPeakIntegratedArea()));
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK LIST OVERVIEW");
		printWriter.println(HORIZONTAL_RULE);
		reportPeaksOverview(printWriter, peaks, "Peaks are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK INTEGRATION RESULTS");
		printWriter.println(HORIZONTAL_RULE);
		reportPeakIntegrationResults(printWriter, peaks, "Peak integration results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("SCAN IDENTIFICATION RESULTS");
		printWriter.println(HORIZONTAL_RULE);
		reportScanIdentificationResults(printWriter, chromatogram, "Scan identification results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK IDENTIFICATION RESULTS");
		printWriter.println(HORIZONTAL_RULE);
		reportPeakIdentificationResults(printWriter, peaks, "Peak identification results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("BACKGROUND INTEGRATION RESULTS");
		printWriter.println(HORIZONTAL_RULE);
		reportIntegrationResults(printWriter, chromatogram.getBackgroundIntegrationEntries(), "Background integration results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("CHROMATOGRAM INTEGRATION RESULTS");
		printWriter.println(HORIZONTAL_RULE);
		reportIntegrationResults(printWriter, chromatogram.getChromatogramIntegrationEntries(), "Chromatogram integration results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("CHROMATOGRAM IDENTIFICATION RESULTS");
		printWriter.println(HORIZONTAL_RULE);
		reportChromatogramIdentificationResults(printWriter, chromatogram, "Chromatogram identification results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK QUANTITATION RESULTS");
		printWriter.println(HORIZONTAL_RULE);
		reportPeakQuantitationResults(printWriter, peaks, "Peak quantitation results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK QUANTITATION SUMMARY");
		printWriter.println(HORIZONTAL_RULE);
		reportPeakListQuantitationSummary(printWriter, peakQuantitationsExtractor.extract(peaks), "The peak quantitation summary is not available.");
		printWriter.println("");
		printWriter.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	private String getChromatogramType(IChromatogram<?> chromatogram) {

		if(chromatogram instanceof IChromatogramCSD) {
			return "CSD";
		} else if(chromatogram instanceof IChromatogramMSD) {
			return "MSD";
		} else if(chromatogram instanceof IChromatogramWSD) {
			return "WSD";
		} else {
			return "???";
		}
	}

	private void reportPeaksOverview(PrintWriter printWriter, List<IPeak> peaks, String messageNoResults, IProgressMonitor monitor) {

		if(!peaks.isEmpty()) {
			printPeaksOverview(printWriter, peaks, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printPeaksOverview(PrintWriter printWriter, List<IPeak> peaks, IProgressMonitor monitor) {

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
		if(peak instanceof IChromatogramPeak) {
			chromatogramPeak = (IChromatogramPeak)peak;
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
		if(peakModel instanceof IPeakModelMSD) {
			IPeakModelMSD peakModelMSD = (IPeakModelMSD)peakModel;
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

	private void reportPeakIntegrationResults(PrintWriter printWriter, List<IPeak> peaks, String messageNoResults, IProgressMonitor monitor) {

		if(!peaks.isEmpty()) {
			printPeakIntegrationResults(printWriter, peaks, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printPeakIntegrationResults(PrintWriter printWriter, List<IPeak> peaks, IProgressMonitor monitor) {

		/*
		 * Headline
		 */
		printWriter.print("[#]");
		printWriter.print(DELIMITER);
		printWriter.print("Trace (0 = TIC or SUM XIC/XWC))");
		printWriter.print(DELIMITER);
		printWriter.print("Integrated Area");
		printWriter.println("");
		/*
		 * Print each peak.
		 */
		int counter = 1;
		for(IPeak peak : peaks) {
			reportPeakIntegrationResults(printWriter, peak, counter++);
		}
	}

	private void reportPeakIntegrationResults(PrintWriter printWriter, IPeak peak, int number) {

		List<IIntegrationEntry> integrationEntries = peak.getIntegrationEntries();
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			printWriter.print("[" + number + "]");
			printWriter.print(DELIMITER);
			/*
			 * TIC or XIC/XWC
			 */
			double signal = integrationEntry.getSignal();
			if(signal == ISignal.TOTAL_INTENSITY) {
				printWriter.print(ISignal.TOTAL_INTENSITY_DESCRIPTION);
			} else {
				printWriter.print(decimalFormat.format(signal));
			}
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(integrationEntry.getIntegratedArea()));
			printWriter.println("");
		}
	}

	private void reportPeakIdentificationResults(PrintWriter printWriter, List<IPeak> peaks, String messageNoResults, IProgressMonitor monitor) {

		if(!peaks.isEmpty()) {
			printPeakIdentificationResults(printWriter, peaks, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printPeakIdentificationResults(PrintWriter printWriter, List<IPeak> peaks, IProgressMonitor monitor) {

		/*
		 * Headline
		 */
		printTargetsHeadline(printWriter, "[#]");
		/*
		 * Print each peak.
		 */
		int counter = 1;
		for(IPeak peak : peaks) {
			Set<IIdentificationTarget> peakTargets = peak.getTargets();
			if(!peakTargets.isEmpty()) {
				printWriter.println(RESULTS_DELIMITER);
				printTargetsData(printWriter, peakTargets, Integer.toString(counter));
			}
			counter++;
		}
	}

	private void reportScanIdentificationResults(PrintWriter printWriter, IChromatogram<? extends IPeak> chromatogram, String messageNoResults, IProgressMonitor monitor) {

		if(scanTargetsAvailable(chromatogram)) {
			/*
			 * Headline
			 */
			printTargetsHeadline(printWriter, "[#]");
			/*
			 * Print each scan targets.
			 */
			int counter = 1;
			for(IScan scan : chromatogram.getScans()) {
				if(!scan.getTargets().isEmpty()) {
					printWriter.println(RESULTS_DELIMITER);
					printTargetsData(printWriter, scan.getTargets(), Integer.toString(counter));
				}
				counter++;
			}
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private boolean scanTargetsAvailable(IChromatogram<? extends IPeak> chromatogram) {

		chromatogram.getScans();
		for(IScan scan : chromatogram.getScans()) {
			if(!scan.getTargets().isEmpty()) {
				return true;
			}
		}
		//
		return false;
	}

	private void reportChromatogramIdentificationResults(PrintWriter printWriter, IChromatogram<?> chromatogram, String messageNoResults, IProgressMonitor monitor) {

		/*
		 * Get the targets
		 */
		Set<IIdentificationTarget> chromatogramTargets = chromatogram.getTargets();
		if(!chromatogramTargets.isEmpty()) {
			printTargetsHeadline(printWriter, null);
			printTargetsData(printWriter, chromatogramTargets, null);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printTargetsHeadline(PrintWriter printWriter, String prefix) {

		/*
		 * Headline
		 */
		if(prefix != null) {
			printWriter.print(prefix);
			printWriter.print(DELIMITER);
		}
		printWriter.print("Name");
		printWriter.print(DELIMITER);
		printWriter.print("CAS");
		printWriter.print(DELIMITER);
		printWriter.print("MatchFactor");
		printWriter.print(DELIMITER);
		printWriter.print("ReverseMatchFactor");
		printWriter.print(DELIMITER);
		printWriter.print("Formula");
		printWriter.print(DELIMITER);
		printWriter.print("Mol Weight");
		printWriter.print(DELIMITER);
		printWriter.print("Probability");
		printWriter.print(DELIMITER);
		printWriter.print("Advise");
		printWriter.print(DELIMITER);
		printWriter.print("Identifier");
		printWriter.print(DELIMITER);
		printWriter.print("Miscellaneous");
		printWriter.print(DELIMITER);
		printWriter.print("Comments");
		printWriter.println("");
	}

	/**
	 * Prefix could be null if not needed.
	 * 
	 * @param printWriter
	 * @param targets
	 * @param prefix
	 * @param monitor
	 */
	private void printTargetsData(PrintWriter printWriter, Set<IIdentificationTarget> targets, String prefix) {

		/*
		 * Iterate through all targets
		 */
		for(IIdentificationTarget identificationTarget : targets) {
			ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
			IComparisonResult comparisonResult = identificationTarget.getComparisonResult();
			/*
			 * 
			 */
			if(prefix != null) {
				printWriter.print("[" + prefix + "]");
				printWriter.print(DELIMITER);
			}
			printWriter.print(libraryInformation.getName());
			printWriter.print(DELIMITER);
			printWriter.print(libraryInformation.getCasNumber());
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(comparisonResult.getMatchFactor()));
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(comparisonResult.getReverseMatchFactor()));
			printWriter.print(DELIMITER);
			printWriter.print(libraryInformation.getFormula());
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(libraryInformation.getMolWeight()));
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(comparisonResult.getProbability()));
			printWriter.print(DELIMITER);
			printWriter.print(comparisonResult.getAdvise());
			printWriter.print(DELIMITER);
			printWriter.print(identificationTarget.getIdentifier());
			printWriter.print(DELIMITER);
			printWriter.print(libraryInformation.getMiscellaneous());
			printWriter.print(DELIMITER);
			printWriter.print(libraryInformation.getComments());
			printWriter.println("");
		}
	}

	private void reportIntegrationResults(PrintWriter printWriter, List<IIntegrationEntry> integrationEntries, String messageNoResults, IProgressMonitor monitor) {

		if(!integrationEntries.isEmpty()) {
			printIntegrationEntries(printWriter, integrationEntries);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printIntegrationEntries(PrintWriter printWriter, List<IIntegrationEntry> integrationEntries) {

		/*
		 * Headline
		 */
		printWriter.print("m/z (0 = TIC or SUM XIC))");
		printWriter.print(DELIMITER);
		printWriter.print("Integrated Area");
		printWriter.println("");
		/*
		 * Print the values.
		 */
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			/*
			 * TIC or XIC
			 */
			double signal = integrationEntry.getSignal();
			if(signal == ISignal.TOTAL_INTENSITY) {
				printWriter.print(ISignal.TOTAL_INTENSITY_DESCRIPTION);
			} else {
				printWriter.print(decimalFormat.format(signal));
			}
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(integrationEntry.getIntegratedArea()));
			printWriter.println("");
		}
	}

	private void reportPeakQuantitationResults(PrintWriter printWriter, List<IPeak> peaks, String messageNoResults, IProgressMonitor monitor) {

		if(!peaks.isEmpty()) {
			printPeakQuantitationResults(printWriter, peaks, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printPeakQuantitationResults(PrintWriter printWriter, List<IPeak> peaks, IProgressMonitor monitor) {

		/*
		 * Headline
		 */
		printWriter.print("[#]");
		printWriter.print(DELIMITER);
		printWriter.print("Name");
		printWriter.print(DELIMITER);
		printWriter.print("ChemicalClass");
		printWriter.print(DELIMITER);
		printWriter.print("Concentration");
		printWriter.print(DELIMITER);
		printWriter.print("ConcentrationUnit");
		printWriter.print(DELIMITER);
		printWriter.print("Area");
		printWriter.print(DELIMITER);
		printWriter.print("Description");
		printWriter.print(DELIMITER);
		printWriter.print("Ion");
		printWriter.println("");
		/*
		 * Print each peak.
		 */
		int counter = 1;
		for(IPeak peak : peaks) {
			List<IQuantitationEntry> quantitationEntries = peak.getQuantitationEntries();
			if(!quantitationEntries.isEmpty()) {
				printWriter.println(RESULTS_DELIMITER);
				printPeakQuantitationResults(printWriter, quantitationEntries, counter);
			}
			counter++;
		}
	}

	private void printPeakQuantitationResults(PrintWriter printWriter, List<IQuantitationEntry> quantitationEntries, int number) {

		/*
		 * Iterate through all targets
		 */
		for(IQuantitationEntry quantitationEntry : quantitationEntries) {
			printWriter.print("[" + number + "]");
			printWriter.print(DELIMITER);
			printWriter.print(quantitationEntry.getName());
			printWriter.print(DELIMITER);
			printWriter.print(quantitationEntry.getChemicalClass());
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(quantitationEntry.getConcentration()));
			printWriter.print(DELIMITER);
			printWriter.print(quantitationEntry.getConcentrationUnit());
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(quantitationEntry.getArea()));
			printWriter.print(DELIMITER);
			printWriter.print(quantitationEntry.getDescription());
			/*
			 * A mass spectrum quantitation entry stores additional information.
			 */
			double signal = quantitationEntry.getSignal();
			printWriter.print(DELIMITER);
			if(signal == ISignal.TOTAL_INTENSITY) {
				printWriter.print(ISignal.TOTAL_INTENSITY_DESCRIPTION);
			} else {
				printWriter.print(decimalFormat.format(signal));
			}
			printWriter.println("");
		}
	}

	private void reportPeakListQuantitationSummary(PrintWriter printWriter, PeakQuantitations peakQuantitations, String messageNoResults) {

		if(!peakQuantitations.getTitles().isEmpty()) {
			/*
			 * Headline
			 */
			Iterator<String> titles = peakQuantitations.getTitles().iterator();
			while(titles.hasNext()) {
				printWriter.print(titles.next());
				if(titles.hasNext()) {
					printWriter.print(DELIMITER);
				}
			}
			printWriter.println("");
			/*
			 * Data
			 */
			List<PeakQuantitation> peakQuantitationEntries = peakQuantitations.getQuantitationEntries();
			for(PeakQuantitation peakQuantitationEntry : peakQuantitationEntries) {
				printWriter.print(decimalFormat.format(peakQuantitationEntry.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				printWriter.print(DELIMITER);
				printWriter.print(decimalFormat.format(peakQuantitationEntry.getIntegratedArea()));
				for(double concentration : peakQuantitationEntry.getConcentrations()) {
					printWriter.print(DELIMITER);
					printWriter.print(decimalFormat.format(concentration));
				}
				printWriter.println("");
			}
		} else {
			printWriter.println(messageNoResults);
		}
	}
}
