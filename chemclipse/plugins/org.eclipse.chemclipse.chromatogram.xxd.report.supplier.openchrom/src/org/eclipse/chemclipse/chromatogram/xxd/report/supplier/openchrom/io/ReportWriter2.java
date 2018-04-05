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
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
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

	private void printAreaPercentList(PrintWriter printWriter, IChromatogram<? extends IPeak> chromatogram) {

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
		printWriter.print("FID1A%");
		printWriter.print(DELIMITER);
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
			printWriter.print(""); // "TIC%"
			printWriter.print(DELIMITER);
			printWriter.print(""); // "FID1A%"
			printWriter.print(DELIMITER);
			printWriter.print(""); // "RI Library"
			printWriter.print(DELIMITER);
			printWriter.print(""); // "RI DA"
			printWriter.print(DELIMITER);
			printWriter.print(chromatogram.getScanNumber(retentionTime));
			printWriter.print(DELIMITER);
			printWriter.print(decimalFormat.format(retentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			printWriter.print(DELIMITER);
			if(peak instanceof IChromatogramPeakMSD) {
				IChromatogramPeakMSD peakMSD = (IChromatogramPeakMSD)peak;
				printWriter.print(decimalFormat.format(peakMSD.getPurity()));
			} else if(peak instanceof IChromatogramPeakMSD) {
				IChromatogramPeakWSD peakWSD = (IChromatogramPeakWSD)peak;
				printWriter.print(decimalFormat.format(peakWSD.getPurity()));
			} else {
				printWriter.print(decimalFormat.format(1.0f));
			}
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
		printWriter.print("100");
		printWriter.print(DELIMITER);
		printWriter.print("100");
		printWriter.print(DELIMITER);
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
