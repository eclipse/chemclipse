/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakModelCSD;
import org.eclipse.chemclipse.csd.model.core.comparator.ChromatogramPeakCSDComparator;
import org.eclipse.chemclipse.model.comparator.SortOrder;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.ITarget;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.comparator.ChromatogramPeakMSDComparator;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.msd.model.core.identifier.chromatogram.IChromatogramTargetMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.IChemClipseChromatogramReportSettings;

public class ChromatogramReport {

	private static final int NUMBER_OF_IONS_TO_PRINT = 5;
	private static final String DELIMITER = "\t";
	private static final String ION_DELIMITER = " ";
	private static final String RESULTS_DELIMITER = "---";
	//
	private DecimalFormat decimalFormat;

	public ChromatogramReport() {

		decimalFormat = new DecimalFormat("0.0####");
	}

	public void generate(File file, boolean append, List<IChromatogram> chromatograms, IChemClipseChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor) throws IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		printWriter.println("ASCII Report");
		printWriter.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		/*
		 * Print each chromatogram.
		 */
		for(IChromatogram chromatogram : chromatograms) {
			if(chromatogram instanceof IChromatogramMSD) {
				/*
				 * MSD
				 */
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				reportChromatogram(printWriter, chromatogramMSD, monitor);
			} else if(chromatogram instanceof IChromatogramCSD) {
				/*
				 * FID
				 */
				IChromatogramCSD chromatogramFID = (IChromatogramCSD)chromatogram;
				reportChromatogram(printWriter, chromatogramFID, monitor);
			} else {
				printWriter.println("There is no suitable report for the chromatogram available.");
			}
		}
		/*
		 * Be sure to flush and close the stream.
		 */
		printWriter.flush();
		fileWriter.flush();
		printWriter.close();
		fileWriter.close();
	}

	/**
	 * The report chromatogram is responsible how to print each section.
	 * 
	 * @param printWriter
	 * @param chromatogram
	 * @param monitor
	 */
	private void reportChromatogram(PrintWriter printWriter, IChromatogramMSD chromatogram, IProgressMonitor monitor) {

		/*
		 * Print
		 */
		List<IChromatogramPeakMSD> peaks = chromatogram.getPeaks();
		Collections.sort(peaks, new ChromatogramPeakMSDComparator(SortOrder.ASC));
		//
		printWriter.println("");
		printWriter.println("NAME: " + chromatogram.getName());
		printWriter.println("TYPE: MSD");
		printWriter.println("------------------------------");
		printWriter.println("Integrated Chromatogram Area: " + decimalFormat.format(chromatogram.getChromatogramIntegratedArea()));
		printWriter.println("Integrated Background Area: " + decimalFormat.format(chromatogram.getBackgroundIntegratedArea()));
		printWriter.println("Integrated Peak Area: " + decimalFormat.format(chromatogram.getPeakIntegratedArea()));
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK LIST OVERVIEW");
		printWriter.println("------------------------------");
		reportPeakListMSDOverview(printWriter, peaks, "Peaks are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK INTEGRATION RESULTS");
		printWriter.println("------------------------------");
		reportPeakListIntegrationResults(printWriter, peaks, "Peak integration results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("SCAN IDENTIFICATION RESULTS");
		printWriter.println("------------------------------");
		reportScanIdentificationResults(printWriter, chromatogram, "Scan identification results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK IDENTIFICATION RESULTS");
		printWriter.println("------------------------------");
		reportPeakListIdentificationResults(printWriter, peaks, "Peak identification results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("BACKGROUND INTEGRATION RESULTS");
		printWriter.println("------------------------------");
		reportIntegrationResults(printWriter, chromatogram.getBackgroundIntegrationEntries(), "Background integration results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("CHROMATOGRAM INTEGRATION RESULTS");
		printWriter.println("------------------------------");
		reportIntegrationResults(printWriter, chromatogram.getChromatogramIntegrationEntries(), "Chromatogram integration results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("CHROMATOGRAM IDENTIFICATION RESULTS");
		printWriter.println("------------------------------");
		reportChromatogramIdentificationResults(printWriter, chromatogram, "Chromatogram identification results are not available.", monitor);
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK QUANTITATION RESULTS");
		printWriter.println("------------------------------");
		reportPeakListQuantitationResults(printWriter, peaks, "Peak quantitation results are not available.", monitor);
		printWriter.println("");
		printWriter.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	private void reportChromatogram(PrintWriter printWriter, IChromatogramCSD chromatogram, IProgressMonitor monitor) {

		/*
		 * Print
		 */
		List<IChromatogramPeakCSD> peaks = chromatogram.getPeaks();
		Collections.sort(peaks, new ChromatogramPeakCSDComparator(SortOrder.ASC));
		//
		printWriter.println("");
		printWriter.println("NAME: " + chromatogram.getName());
		printWriter.println("TYPE: FID");
		printWriter.println("------------------------------");
		printWriter.println("Integrated Chromatogram Area: " + decimalFormat.format(chromatogram.getChromatogramIntegratedArea()));
		printWriter.println("Integrated Background Area: " + decimalFormat.format(chromatogram.getBackgroundIntegratedArea()));
		printWriter.println("Integrated Peak Area: " + decimalFormat.format(chromatogram.getPeakIntegratedArea()));
		printWriter.println("");
		printWriter.println("");
		printWriter.println("PEAK RESULTS");
		printWriter.println("------------------------------");
		reportPeakListFIDOverview(printWriter, peaks, "Peaks are not available.", monitor);
		printWriter.println("");
		printWriter.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	private void reportPeakListMSDOverview(PrintWriter printWriter, List<IChromatogramPeakMSD> peaks, String messageNoResults, IProgressMonitor monitor) {

		if(peaks.size() > 0) {
			printChromatogramPeakListMSDOverview(printWriter, peaks, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void reportPeakListFIDOverview(PrintWriter printWriter, List<IChromatogramPeakCSD> peaks, String messageNoResults, IProgressMonitor monitor) {

		if(peaks.size() > 0) {
			printChromatogramPeakListFIDOverview(printWriter, peaks, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printChromatogramPeakListMSDOverview(PrintWriter printWriter, List<IChromatogramPeakMSD> peaks, IProgressMonitor monitor) {

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
		printWriter.print("m/z ...");
		printWriter.println("");
		/*
		 * Print each peak.
		 */
		int counter = 1;
		for(IChromatogramPeakMSD peak : peaks) {
			reportPeak(printWriter, peak, counter++, monitor);
		}
	}

	private void printChromatogramPeakListFIDOverview(PrintWriter printWriter, List<IChromatogramPeakCSD> peaks, IProgressMonitor monitor) {

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
		printWriter.print("Integrator");
		printWriter.print(DELIMITER);
		printWriter.print("Detector");
		printWriter.print(DELIMITER);
		printWriter.print("Model");
		printWriter.println("");
		/*
		 * Print each peak.
		 */
		int counter = 1;
		for(IChromatogramPeakCSD peak : peaks) {
			reportPeak(printWriter, peak, counter++, monitor);
		}
	}

	private void reportPeak(PrintWriter printWriter, IChromatogramPeakMSD peak, int number, IProgressMonitor monitor) {

		IPeakModelMSD peakModel = peak.getPeakModel();
		printWriter.print("[" + number + "]");
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramMSD.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getPeakMassSpectrum().getRetentionIndex()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getStartRetentionTime() / IChromatogramMSD.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getStopRetentionTime() / IChromatogramMSD.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getTailing()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getWidthByInflectionPoints(0.5f) / IChromatogramMSD.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peak.getIntegratedArea()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peak.getSignalToNoiseRatio()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getLeading()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getTailing()));
		printWriter.print(DELIMITER);
		printWriter.print(peak.getScanMax());
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peak.getPurity()));
		printWriter.print(DELIMITER);
		printWriter.print(peak.getIntegratorDescription());
		printWriter.print(DELIMITER);
		printWriter.print(peak.getDetectorDescription());
		printWriter.print(DELIMITER);
		printWriter.print(peak.getModelDescription());
		/*
		 * Print the highest m/z abundance values
		 */
		IPeakMassSpectrum peakMassSpectrum = peakModel.getPeakMassSpectrum();
		List<IIon> ions = peakMassSpectrum.getIons();
		/*
		 * Check how many ions shall be printed.
		 */
		int numberOfIonsToPrint = (ions.size() < NUMBER_OF_IONS_TO_PRINT) ? ions.size() : NUMBER_OF_IONS_TO_PRINT;
		Collections.sort(ions, new IonAbundanceComparator(SortOrder.DESC));
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < numberOfIonsToPrint; i++) {
			IIon ion = ions.get(i);
			if(ion.getIon() == AbstractIon.TIC_ION) {
				builder.append(AbstractIon.TIC_DESCRIPTION);
			} else {
				builder.append(decimalFormat.format(ion.getIon()));
			}
			builder.append(ION_DELIMITER);
		}
		builder.append("...");
		printWriter.print(DELIMITER);
		printWriter.print(builder.toString());
		/*
		 * Close the line.
		 */
		printWriter.println("");
	}

	private void reportPeak(PrintWriter printWriter, IChromatogramPeakCSD peak, int number, IProgressMonitor monitor) {

		IPeakModelCSD peakModel = peak.getPeakModel();
		printWriter.print("[" + number + "]");
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramMSD.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getPeakMaximum().getRetentionIndex()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getStartRetentionTime() / IChromatogramMSD.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getStopRetentionTime() / IChromatogramMSD.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getTailing()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getWidthByInflectionPoints(0.5f) / IChromatogramMSD.MINUTE_CORRELATION_FACTOR));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peak.getIntegratedArea()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peak.getSignalToNoiseRatio()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getLeading()));
		printWriter.print(DELIMITER);
		printWriter.print(decimalFormat.format(peakModel.getTailing()));
		printWriter.print(DELIMITER);
		printWriter.print(peak.getScanMax());
		printWriter.print(DELIMITER);
		printWriter.print(peak.getIntegratorDescription());
		printWriter.print(DELIMITER);
		printWriter.print(peak.getDetectorDescription());
		printWriter.print(DELIMITER);
		printWriter.print(peak.getModelDescription());
		/*
		 * Close the line.
		 */
		printWriter.println("");
	}

	private void reportPeakListIntegrationResults(PrintWriter printWriter, List<IChromatogramPeakMSD> peaks, String messageNoResults, IProgressMonitor monitor) {

		if(peaks.size() > 0) {
			printPeakIntegrationResults(printWriter, peaks, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printPeakIntegrationResults(PrintWriter printWriter, List<IChromatogramPeakMSD> peaks, IProgressMonitor monitor) {

		/*
		 * Headline
		 */
		printWriter.print("[#]");
		printWriter.print(DELIMITER);
		printWriter.print("m/z (0 = TIC or SUM XIC))");
		printWriter.print(DELIMITER);
		printWriter.print("Integrated Area");
		printWriter.println("");
		/*
		 * Print each peak.
		 */
		int counter = 1;
		for(IChromatogramPeakMSD peak : peaks) {
			reportPeakIntegrationResults(printWriter, peak, counter++, monitor);
		}
	}

	private void reportPeakIntegrationResults(PrintWriter printWriter, IChromatogramPeakMSD peak, int number, IProgressMonitor monitor) {

		List<? extends IIntegrationEntry> integrationEntries = peak.getIntegrationEntries();
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			if(integrationEntry instanceof IIntegrationEntryMSD) {
				IIntegrationEntryMSD integrationEntryMSD = (IIntegrationEntryMSD)integrationEntry;
				printWriter.print("[" + number + "]");
				printWriter.print(DELIMITER);
				/*
				 * TIC or XIC
				 */
				double ion = integrationEntryMSD.getIon();
				if(ion == AbstractIon.TIC_ION) {
					printWriter.print(AbstractIon.TIC_DESCRIPTION);
				} else {
					printWriter.print(decimalFormat.format(ion));
				}
				printWriter.print(DELIMITER);
				printWriter.print(decimalFormat.format(integrationEntryMSD.getIntegratedArea()));
				printWriter.println("");
			}
		}
	}

	private void reportPeakListIdentificationResults(PrintWriter printWriter, List<IChromatogramPeakMSD> peaks, String messageNoResults, IProgressMonitor monitor) {

		if(peaks.size() > 0) {
			printChromatogramPeaks(printWriter, peaks, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printChromatogramPeaks(PrintWriter printWriter, List<IChromatogramPeakMSD> peaks, IProgressMonitor monitor) {

		/*
		 * Headline
		 */
		printTargetsHeadline(printWriter, "[#]", monitor);
		/*
		 * Print each peak.
		 */
		int counter = 1;
		for(IChromatogramPeakMSD peak : peaks) {
			List<IPeakTarget> peakTargets = peak.getTargets();
			if(peakTargets.size() > 0) {
				printWriter.println(RESULTS_DELIMITER);
				printTargetsData(printWriter, peakTargets, Integer.toString(counter), monitor);
			}
			counter++;
		}
	}

	private void reportScanIdentificationResults(PrintWriter printWriter, IChromatogramMSD chromatogram, String messageNoResults, IProgressMonitor monitor) {

		if(scanTargetsAvailable(chromatogram)) {
			/*
			 * Headline
			 */
			printTargetsHeadline(printWriter, "[#]", monitor);
			/*
			 * Print each scan targets.
			 */
			for(int i = 1; i <= chromatogram.getNumberOfScans(); i++) {
				IVendorMassSpectrum massSpectrum = chromatogram.getSupplierScan(i);
				List<IMassSpectrumTarget> massSpectrumTargets = massSpectrum.getTargets();
				if(massSpectrumTargets.size() > 0) {
					printWriter.println(RESULTS_DELIMITER);
					printTargetsData(printWriter, massSpectrumTargets, Integer.toString(i), monitor);
				}
			}
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private boolean scanTargetsAvailable(IChromatogramMSD chromatogram) {

		boolean scanTargetAvailable = false;
		exitloop:
		for(int i = 1; i <= chromatogram.getNumberOfScans(); i++) {
			IVendorMassSpectrum massSpectrum = chromatogram.getSupplierScan(i);
			List<IMassSpectrumTarget> massSpectrumTargets = massSpectrum.getTargets();
			if(massSpectrumTargets.size() > 0) {
				scanTargetAvailable = true;
				break exitloop;
			}
		}
		//
		return scanTargetAvailable;
	}

	private void reportChromatogramIdentificationResults(PrintWriter printWriter, IChromatogramMSD chromatogram, String messageNoResults, IProgressMonitor monitor) {

		/*
		 * Get the targets
		 */
		List<IChromatogramTargetMSD> chromatogramTargets = chromatogram.getTargets();
		if(chromatogramTargets.size() > 0) {
			printTargetsHeadline(printWriter, null, monitor);
			printTargetsData(printWriter, chromatogramTargets, null, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printTargetsHeadline(PrintWriter printWriter, String prefix, IProgressMonitor monitor) {

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
	private void printTargetsData(PrintWriter printWriter, List<? extends ITarget> targets, String prefix, IProgressMonitor monitor) {

		/*
		 * Iterate through all targets
		 */
		for(ITarget target : targets) {
			/*
			 * Test if it's an identification entry.
			 */
			if(target instanceof IIdentificationTarget) {
				IIdentificationTarget identificationEntry = (IIdentificationTarget)target;
				ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
				IComparisonResult comparisonResult = identificationEntry.getComparisonResult();
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
				printWriter.print(identificationEntry.getIdentifier());
				printWriter.print(DELIMITER);
				printWriter.print(libraryInformation.getMiscellaneous());
				printWriter.print(DELIMITER);
				printWriter.print(libraryInformation.getComments());
				printWriter.println("");
			}
		}
	}

	private void reportIntegrationResults(PrintWriter printWriter, List<IIntegrationEntry> integrationEntries, String messageNoResults, IProgressMonitor monitor) {

		if(integrationEntries.size() > 0) {
			printIntegrationEntries(printWriter, integrationEntries, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printIntegrationEntries(PrintWriter printWriter, List<IIntegrationEntry> integrationEntries, IProgressMonitor monitor) {

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
			if(integrationEntry instanceof IIntegrationEntryMSD) {
				IIntegrationEntryMSD integrationEntryMSD = (IIntegrationEntryMSD)integrationEntry;
				double ion = integrationEntryMSD.getIon();
				if(ion == AbstractIon.TIC_ION) {
					printWriter.print(AbstractIon.TIC_DESCRIPTION);
				} else {
					printWriter.print(decimalFormat.format(ion));
				}
				printWriter.print(DELIMITER);
				printWriter.print(decimalFormat.format(integrationEntryMSD.getIntegratedArea()));
				printWriter.println("");
			}
		}
	}

	private void reportPeakListQuantitationResults(PrintWriter printWriter, List<IChromatogramPeakMSD> peaks, String messageNoResults, IProgressMonitor monitor) {

		if(peaks.size() > 0) {
			printQuantitationPeaks(printWriter, peaks, monitor);
		} else {
			printWriter.println(messageNoResults);
		}
	}

	private void printQuantitationPeaks(PrintWriter printWriter, List<IChromatogramPeakMSD> peaks, IProgressMonitor monitor) {

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
		for(IChromatogramPeakMSD peak : peaks) {
			List<IQuantitationEntry> quantitationEntries = peak.getQuantitationEntries();
			if(quantitationEntries.size() > 0) {
				printWriter.println(RESULTS_DELIMITER);
				printPeakQuantitationResults(printWriter, quantitationEntries, counter, monitor);
			}
			counter++;
		}
	}

	private void printPeakQuantitationResults(PrintWriter printWriter, List<IQuantitationEntry> quantitationEntries, int number, IProgressMonitor monitor) {

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
			if(quantitationEntry instanceof IQuantitationEntryMSD) {
				IQuantitationEntryMSD quantitationEntryMSD = (IQuantitationEntryMSD)quantitationEntry;
				printWriter.print(DELIMITER);
				/*
				 * TIC or XIC
				 */
				double ion = quantitationEntryMSD.getIon();
				if(ion == AbstractIon.TIC_ION) {
					printWriter.print(AbstractIon.TIC_DESCRIPTION);
				} else {
					printWriter.print(decimalFormat.format(ion));
				}
			} else {
				printWriter.print(DELIMITER);
				printWriter.print("--");
			}
			printWriter.println("");
		}
	}
}
