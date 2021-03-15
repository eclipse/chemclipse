/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.ReportSettings2;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
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
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0000");
	private DateFormat dateFormat = ValueFormat.getDateFormatEnglish();
	/*
	 * This map is used to store the summed area result instead
	 * of calculating it again and again.
	 */
	private Map<IChromatogram<?>, Double> chromatogramAreaMap = new HashMap<>();

	public void generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, ReportSettings2 reportSettings, IProgressMonitor monitor) throws IOException {

		try (PrintWriter printWriter = new PrintWriter(new FileWriter(file, append))) {
			for(IChromatogram<? extends IPeak> chromatogram : chromatograms) {
				printHeader(printWriter, chromatogram);
				printWriter.println("");
				printAreaPercentList(printWriter, chromatogram, reportSettings);
				printWriter.println("");
			}
			printWriter.flush();
		} finally {
			// Auto-closable
		}
	}

	private void printHeader(PrintWriter printWriter, IChromatogram<? extends IPeak> chromatogram) {

		printWriter.println("File Name: " + chromatogram.getName());
		printWriter.println("Sample Name: " + chromatogram.getDataName());
		printWriter.println("Additional Info: " + chromatogram.getDetailedInfo() + " " + chromatogram.getMiscInfo()); // Don't change without team feedback.
		printWriter.println("Acquisition Date: " + dateFormat.format(chromatogram.getDate()));
		printWriter.println("Operator: " + chromatogram.getOperator());
		printWriter.println("Miscellaneous: " + chromatogram.getMiscInfo());
	}

	private void printAreaPercentList(PrintWriter printWriter, IChromatogram<? extends IPeak> chromatogramSource, ReportSettings2 reportSettings) {

		double[] chromatogramAreaSumArray = getChromatogramAreaSumArray(chromatogramSource);
		double[] peakAreaSumArray = initializePeakAreaSumArray(chromatogramSource);
		boolean addPeakArea = reportSettings.isAddPeakArea();
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
		if(addPeakArea) {
			printWriter.print("TIC");
			printWriter.print(DELIMITER);
		}
		/*
		 * Variable length depending on referenced chromatograms
		 */
		printAreaPercentHeadlines(printWriter, chromatogramSource, reportSettings); // FID1%, ...
		/*
		 * Additional Entries
		 */
		printWriter.print("RI Library");
		printWriter.print(DELIMITER);
		printWriter.print("RI DA");
		printWriter.print(DELIMITER);
		printWriter.print("Scan#");
		printWriter.print(DELIMITER);
		printWriter.print("Retention Time [Minutes]");
		printWriter.print(DELIMITER);
		printWriter.print("Purity");
		printWriter.println("");
		/*
		 * Data
		 */
		for(IPeak peakSource : chromatogramSource.getPeaks()) {
			//
			IPeakModel peakModelSource = peakSource.getPeakModel();
			int retentionTime = peakModelSource.getRetentionTimeAtPeakMaximum();
			Set<IIdentificationTarget> peakTargetsSource = peakSource.getTargets();
			/*
			 * Use peak only if targets and a library information
			 * are available.
			 */
			if(peakTargetsSource.size() > 0) {
				/*
				 * Get the best target
				 */
				float retentionIndex = reportSettings.isUseRetentionIndexQC() ? peakModelSource.getPeakMaximum().getRetentionIndex() : 0.0f;
				IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, retentionIndex);
				ILibraryInformation libraryInformationSource = IIdentificationTarget.getBestLibraryInformation(peakTargetsSource, identificationTargetComparator);
				//
				if(libraryInformationSource != null) {
					printWriter.print((libraryInformationSource != null) ? libraryInformationSource.getName() : "");
					printWriter.print(DELIMITER);
					printWriter.print((libraryInformationSource != null) ? libraryInformationSource.getContributor() : "");
					printWriter.print(DELIMITER);
					printWriter.print((libraryInformationSource != null) ? libraryInformationSource.getReferenceIdentifier() : "");
					printWriter.print(DELIMITER);
					/*
					 * Variable length depending on referenced chromatograms
					 */
					peakAreaSumArray = printAreaPercentData(printWriter, chromatogramSource, peakSource, libraryInformationSource, peakAreaSumArray, reportSettings); // FID1%, ...
					/*
					 * Additional Entries
					 */
					printWriter.print(decimalFormat.format(libraryInformationSource.getRetentionIndex())); // "RI Library"
					printWriter.print(DELIMITER);
					printWriter.print(getRetentionIndex(peakModelSource)); // "RI DA"
					printWriter.print(DELIMITER);
					printWriter.print(chromatogramSource.getScanNumber(retentionTime));
					printWriter.print(DELIMITER);
					printWriter.print(decimalFormat.format(retentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
					printWriter.print(DELIMITER);
					printWriter.print(decimalFormat.format(getPurity(peakSource)));
					printWriter.println("");
				}
			}
		}
		/*
		 * Area Sum
		 */
		printWriter.println("");
		printWriter.print("SUM");
		printWriter.print(DELIMITER);
		printWriter.print("");
		printWriter.print(DELIMITER);
		printWriter.print("");
		printWriter.print(DELIMITER);
		printAreaPercentSum(printWriter, chromatogramAreaSumArray, peakAreaSumArray, reportSettings); // Sum FID1%, ...
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

	private void printAreaPercentHeadlines(PrintWriter printWriter, IChromatogram<?> chromatogramSource, ReportSettings2 reportSettings) {

		List<IChromatogram<?>> referencedChromatograms = chromatogramSource.getReferencedChromatograms();
		boolean addPeakArea = reportSettings.isAddPeakArea();
		//
		int i = 1;
		for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
			String labelArea;
			if(referencedChromatogram instanceof IChromatogramMSD) {
				labelArea = "MSD" + i;
			} else if(referencedChromatogram instanceof IChromatogramCSD) {
				labelArea = "CSD" + i;
			} else if(referencedChromatogram instanceof IChromatogramWSD) {
				labelArea = "WSD" + i;
			} else {
				labelArea = "???" + i;
			}
			//
			printWriter.print(labelArea + "%");
			printWriter.print(DELIMITER);
			if(addPeakArea) {
				printWriter.print(labelArea);
				printWriter.print(DELIMITER);
			}
			//
			i++;
		}
	}

	private double[] printAreaPercentData(PrintWriter printWriter, IChromatogram<?> chromatogramSource, IPeak peakSource, ILibraryInformation libraryInformationSource, double[] peakAreaSumArray, ReportSettings2 reportSettings) {

		List<IChromatogram<?>> referencedChromatograms = chromatogramSource.getReferencedChromatograms();
		boolean addPeakArea = reportSettings.isAddPeakArea();
		/*
		 * Master Peak
		 */
		peakAreaSumArray[0] += peakSource.getIntegratedArea();
		printWriter.print(decimalFormat.format(getPercentagePeakArea(chromatogramSource, peakSource))); // "TIC%"
		printWriter.print(DELIMITER);
		if(addPeakArea) {
			printWriter.print(decimalFormat.format(peakSource.getIntegratedArea())); // "TIC"
			printWriter.print(DELIMITER);
		}
		/*
		 * Reference Peak
		 */
		int i = 1;
		for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
			IPeak referencedPeak = getReferencedPeak(peakSource, libraryInformationSource, referencedChromatogram, reportSettings);
			double peakArea = (referencedPeak != null) ? referencedPeak.getIntegratedArea() : 0.0d;
			peakAreaSumArray[i] += peakArea;
			printWriter.print(decimalFormat.format(getPercentagePeakArea(referencedChromatogram, referencedPeak))); // "FID1A%"
			printWriter.print(DELIMITER);
			if(addPeakArea) {
				printWriter.print(decimalFormat.format(referencedPeak != null ? referencedPeak.getIntegratedArea() : 0)); // "FID1A"
				printWriter.print(DELIMITER);
			}
			i++;
		}
		//
		return peakAreaSumArray;
	}

	private void printAreaPercentSum(PrintWriter printWriter, double[] chromatogramAreaSumArray, double[] peakAreaSumArray, ReportSettings2 reportSettings) {

		boolean addPeakArea = reportSettings.isAddPeakArea();
		//
		if(chromatogramAreaSumArray.length == peakAreaSumArray.length) {
			int size = chromatogramAreaSumArray.length;
			for(int i = 0; i < size; i++) {
				printWriter.print(decimalFormat.format(getPercentagePeakArea(chromatogramAreaSumArray[i], peakAreaSumArray[i])));
				printWriter.print(DELIMITER);
				if(addPeakArea) {
					printWriter.print("");
					printWriter.print(DELIMITER);
				}
			}
		}
	}

	private double[] getChromatogramAreaSumArray(IChromatogram<?> chromatogram) {

		List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
		int size = 1 + referencedChromatograms.size();
		double[] chromatogramAreaSumArray = new double[size];
		//
		chromatogramAreaSumArray[0] = chromatogram.getPeakIntegratedArea();
		//
		int i = 1;
		for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
			chromatogramAreaSumArray[i++] = referencedChromatogram.getPeakIntegratedArea();
		}
		//
		return chromatogramAreaSumArray;
	}

	private double[] initializePeakAreaSumArray(IChromatogram<?> chromatogram) {

		List<IChromatogram<?>> referencedChromatograms = chromatogram.getReferencedChromatograms();
		int size = 1 + referencedChromatograms.size();
		return new double[size];
	}

	private IPeak getReferencedPeak(IPeak peakSource, ILibraryInformation libraryInformationSource, IChromatogram<? extends IPeak> referencedChromatogram, ReportSettings2 reportSettings) {

		if(peakSource != null && libraryInformationSource != null && referencedChromatogram != null) {
			//
			IPeakModel peakModelSource = peakSource.getPeakModel();
			int retentionTimeSource = peakModelSource.getRetentionTimeAtPeakMaximum();
			int startRetentionTime = peakModelSource.getStartRetentionTime() - reportSettings.getDeltaRetentionTimeLeft();
			int stopRetentionTime = peakModelSource.getStopRetentionTime() - reportSettings.getDeltaRetentionTimeRight();
			boolean useBestMatch = reportSettings.isUseBestMatch();
			//
			List<IPeak> peaksOfInterest = extractPeaksOfInterest(referencedChromatogram, startRetentionTime, stopRetentionTime);
			if(peaksOfInterest.size() > 0) {
				return extractBestMatchingPeak(peaksOfInterest, retentionTimeSource, libraryInformationSource, useBestMatch);
			}
		}
		//
		return null;
	}

	private IPeak extractBestMatchingPeak(List<IPeak> peaksOfInterest, int retentionTimeSource, ILibraryInformation libraryInformationSource, boolean useBestMatch) {

		if(useBestMatch) {
			return getClosestPeak(peaksOfInterest, retentionTimeSource);
		} else {
			return getBestPeak(peaksOfInterest, libraryInformationSource, retentionTimeSource);
		}
	}

	private IPeak getClosestPeak(List<IPeak> peaksOfInterest, int retentionTimeSource) {

		IPeak peakTarget = null;
		for(IPeak peakReference : peaksOfInterest) {
			peakTarget = getClosestPeak(peakTarget, peakReference, retentionTimeSource);
		}
		return peakTarget;
	}

	private IPeak getBestPeak(List<IPeak> peaksOfInterest, ILibraryInformation libraryInformationSource, int retentionTimeSource) {

		IPeak peakTarget = null;
		for(IPeak peakReference : peaksOfInterest) {
			if(peakTarget == null) {
				if(isPeakTargetMatch(peakReference, libraryInformationSource)) {
					peakTarget = peakReference;
				}
			} else {
				IPeak peakClosest = getClosestPeak(peakTarget, peakReference, retentionTimeSource);
				if(peakClosest != peakTarget) {
					if(isPeakTargetMatch(peakClosest, libraryInformationSource)) {
						peakTarget = peakClosest;
					}
				}
			}
		}
		return peakTarget;
	}

	private boolean isPeakTargetMatch(IPeak peak, ILibraryInformation libraryInformationSource) {

		for(IIdentificationTarget peakTarget : peak.getTargets()) {
			if(isPeakTargetMatch(libraryInformationSource, peakTarget.getLibraryInformation())) {
				return true;
			}
		}
		return false;
	}

	private IPeak getClosestPeak(IPeak peakTarget, IPeak peakReference, int retentionTimeSource) {

		if(peakTarget == null) {
			return peakReference;
		} else {
			int retentionTimeReference = peakReference.getPeakModel().getRetentionTimeAtPeakMaximum();
			int retentionTimeTarget = peakTarget.getPeakModel().getRetentionTimeAtPeakMaximum();
			int deltaReference = Math.abs(retentionTimeSource - retentionTimeReference);
			int deltaTarget = Math.abs(retentionTimeSource - retentionTimeTarget);
			if(deltaReference < deltaTarget) {
				return peakReference;
			} else {
				return peakTarget;
			}
		}
	}

	private List<IPeak> extractPeaksOfInterest(IChromatogram<? extends IPeak> referencedChromatogram, int startRetentionTime, int stopRetentionTime) {

		List<IPeak> peaksOfInterest = new ArrayList<>();
		for(IPeak peak : referencedChromatogram.getPeaks()) {
			int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
			if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
				peaksOfInterest.add(peak);
			}
		}
		return peaksOfInterest;
	}

	private boolean isPeakTargetMatch(ILibraryInformation libraryInformation, ILibraryInformation referencedLibraryInformation) {

		if(libraryInformation != null && referencedLibraryInformation != null) {
			if(libraryInformation.getName().equals(referencedLibraryInformation.getName())) {
				return true;
			}
		}
		return false;
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

	private double getPercentagePeakArea(IChromatogram<?> chromatogram, IPeak peak) {

		double peakAreaPercent = 0.0d;
		if(chromatogram != null && peak != null) {
			Double chromatogramPeakArea = chromatogramAreaMap.get(chromatogram);
			if(chromatogramPeakArea == null) {
				chromatogramPeakArea = chromatogram.getPeakIntegratedArea();
				chromatogramAreaMap.put(chromatogram, chromatogramPeakArea);
			}
			peakAreaPercent = getPercentagePeakArea(chromatogramPeakArea, peak.getIntegratedArea());
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
}
