/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.IReportSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ReportWriter2 {

	private static final String DELIMITER = "\t";
	//
	private DecimalFormat decimalFormat;
	private DateFormat dateFormat;
	private TargetExtendedComparator targetExtendedComparator;

	public ReportWriter2() {
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
		dateFormat = ValueFormat.getDateFormatEnglish();
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

	public void generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IReportSettings chromatogramReportSettings, IProgressMonitor monitor) throws IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		//
		for(IChromatogram<? extends IPeak> chromatogram : chromatograms) {
			printHeader(printWriter, chromatogram);
			printWriter.println("");
			printAreaPercentList(printWriter, chromatogram);
			printWriter.println("");
		}
		/*
		 * Be sure to flush and close the stream.
		 */
		printWriter.flush();
		fileWriter.flush();
		printWriter.close();
		fileWriter.close();
	}

	private void printHeader(PrintWriter printWriter, IChromatogram<? extends IPeak> chromatogram) {

		printWriter.println("Filename: " + chromatogram.getName());
		printWriter.println("Sample Name: " + chromatogram.getDataName());
		printWriter.println("Additional Info: " + chromatogram.getDetailedInfo());
		printWriter.println("Acquisition Date: " + dateFormat.format(chromatogram.getDate()));
		printWriter.println("Operator: " + chromatogram.getOperator());
		printWriter.println("Miscellaneous: " + chromatogram.getMiscInfo());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void printAreaPercentList(PrintWriter printWriter, IChromatogram<? extends IPeak> chromatogram) {

		List<IChromatogram> referencedChromatograms = chromatogram.getReferencedChromatograms();
		int size = 1 + referencedChromatograms.size();
		double[] chromatogramAreaSumArray = new double[size];
		double[] peakAreaSumArray = new double[size];
		/*
		 * Headline
		 */
		printWriter.print("Name");
		printWriter.print(DELIMITER);
		printWriter.print("Contributor");
		printWriter.print(DELIMITER);
		printWriter.print("Library Reference");
		printWriter.print(DELIMITER);
		printWriter.print("TIC%");
		printWriter.print(DELIMITER);
		//
		chromatogramAreaSumArray[0] = chromatogram.getPeakIntegratedArea();
		int i = 1;
		for(IChromatogram referencedChromatogram : referencedChromatograms) {
			//
			chromatogramAreaSumArray[i] = referencedChromatogram.getPeakIntegratedArea();
			//
			String label;
			if(referencedChromatogram instanceof IChromatogramMSD) {
				label = "MSD" + i + "%";
			} else if(referencedChromatogram instanceof IChromatogramCSD) {
				label = "CSD" + i + "%";
			} else if(referencedChromatogram instanceof IChromatogramWSD) {
				label = "WSD" + i + "%";
			} else {
				label = "???" + i + "%";
			}
			printWriter.print(label);
			printWriter.print(DELIMITER);
			i++;
		}
		//
		printWriter.print("RI Library");
		printWriter.print(DELIMITER);
		printWriter.print("RI DA");
		printWriter.print(DELIMITER);
		printWriter.print("Scan#");
		printWriter.print(DELIMITER);
		printWriter.print("Retention Time (Minutes)");
		printWriter.print(DELIMITER);
		printWriter.print("Purity");
		printWriter.println("");
		/*
		 * Data
		 */
		for(IPeak peak : chromatogram.getPeaks()) {
			//
			IPeakModel peakModel = peak.getPeakModel();
			int retentionTime = peakModel.getRetentionTimeAtPeakMaximum();
			List<IPeakTarget> peakTargets = peak.getTargets();
			ILibraryInformation libraryInformation = getBestLibraryInformation(peakTargets);
			//
			printWriter.print((libraryInformation != null) ? libraryInformation.getName() : "");
			printWriter.print(DELIMITER);
			printWriter.print((libraryInformation != null) ? libraryInformation.getContributor() : "");
			printWriter.print(DELIMITER);
			printWriter.print((libraryInformation != null) ? libraryInformation.getReferenceIdentifier() : "");
			printWriter.print(DELIMITER);
			peakAreaSumArray[0] += peak.getIntegratedArea();
			printWriter.print(decimalFormat.format(getPercentagePeakArea(chromatogram, peak))); // "TIC%"
			printWriter.print(DELIMITER);
			//
			int j = 1;
			for(IChromatogram referencedChromatogram : referencedChromatograms) {
				IPeak referencedPeak = getReferencedPeak(libraryInformation, referencedChromatogram);
				double peakArea = (referencedPeak != null) ? referencedPeak.getIntegratedArea() : 0.0d;
				peakAreaSumArray[j] += peakArea;
				printWriter.print(decimalFormat.format(getPercentagePeakArea(referencedChromatogram, referencedPeak))); // "FID1A%"
				printWriter.print(DELIMITER);
				j++;
			}
			//
			printWriter.print(decimalFormat.format(libraryInformation.getRetentionIndex())); // "RI Library"
			printWriter.print(DELIMITER);
			printWriter.print(getRetentionIndex(peakModel)); // "RI DA"
			printWriter.print(DELIMITER);
			printWriter.print(chromatogram.getScanNumber(retentionTime));
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(retentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(getPurity(peak)));
			printWriter.println("");
		}
		//
		printWriter.println("");
		printWriter.print("SUM");
		printWriter.print(DELIMITER);
		printWriter.print("");
		printWriter.print(DELIMITER);
		printWriter.print("");
		printWriter.print(DELIMITER);
		//
		for(int k = 0; k < size; k++) {
			printWriter.print(decimalFormat.format(getPercentagePeakArea(chromatogramAreaSumArray[k], peakAreaSumArray[k])));
			printWriter.print(DELIMITER);
		}
		//
		printWriter.print("");
		printWriter.print(DELIMITER);
		printWriter.print("");
		printWriter.print(DELIMITER);
		printWriter.print("");
		printWriter.print(DELIMITER);
		printWriter.print("");
		printWriter.print(DELIMITER);
		printWriter.print("");
		printWriter.println("");
	}

	private IPeak getReferencedPeak(ILibraryInformation libraryInformation, IChromatogram<? extends IPeak> referencedChromatogram) {

		IPeak peak = null;
		//
		exitloop:
		for(IPeak referencedPeak : referencedChromatogram.getPeaks()) {
			ILibraryInformation referencedLibraryInformation = getBestLibraryInformation(referencedPeak.getTargets());
			if(referencedLibraryInformation != null) {
				if(referencedLibraryInformation.getName().equals(referencedLibraryInformation.getName())) {
					peak = referencedPeak;
					break exitloop;
				}
			}
		}
		//
		return peak;
	}

	private float getPurity(IPeak peak) {

		float purity = 1.0f;
		if(peak instanceof IChromatogramPeakMSD) {
			IChromatogramPeakMSD peakMSD = (IChromatogramPeakMSD)peak;
			purity = peakMSD.getPurity();
		} else if(peak instanceof IChromatogramPeakMSD) {
			IChromatogramPeakWSD peakWSD = (IChromatogramPeakWSD)peak;
			purity = peakWSD.getPurity();
		}
		//
		return purity;
	}

	@SuppressWarnings("rawtypes")
	private double getPercentagePeakArea(IChromatogram chromatogram, IPeak peak) {

		double peakAreaPercent = 0.0d;
		if(chromatogram != null && peak != null) {
			if(chromatogram != null && peak != null) {
				double chromatogramPeakArea = chromatogram.getPeakIntegratedArea();
				getPercentagePeakArea(chromatogramPeakArea, peak.getIntegratedArea());
			}
		}
		//
		return peakAreaPercent;
	}

	private double getPercentagePeakArea(double chromatogramPeakArea, double integratedArea) {

		double peakAreaPercent = 0.0d;
		if(chromatogramPeakArea > 0) {
			peakAreaPercent = (100.0d / chromatogramPeakArea) * integratedArea;
		}
		//
		return peakAreaPercent;
	}

	private float getRetentionIndex(IPeakModel peakModel) {

		float retentionIndex = 0.0f;
		if(peakModel instanceof IPeakModelMSD) {
			IPeakModelMSD peakModelMSD = (IPeakModelMSD)peakModel;
			retentionIndex = peakModelMSD.getPeakMassSpectrum().getRetentionIndex();
		}
		//
		return retentionIndex;
	}

	private ILibraryInformation getBestLibraryInformation(List<IPeakTarget> targets) {

		ILibraryInformation libraryInformation = null;
		IIdentificationTarget identificationTarget = getBestIdentificationTarget(targets);
		if(identificationTarget != null) {
			libraryInformation = identificationTarget.getLibraryInformation();
		}
		return libraryInformation;
	}

	private IIdentificationTarget getBestIdentificationTarget(List<? extends IIdentificationTarget> targets) {

		IIdentificationTarget identificationTarget = null;
		targets = new ArrayList<IIdentificationTarget>(targets);
		Collections.sort(targets, targetExtendedComparator);
		if(targets.size() >= 1) {
			identificationTarget = targets.get(0);
		}
		return identificationTarget;
	}
}
