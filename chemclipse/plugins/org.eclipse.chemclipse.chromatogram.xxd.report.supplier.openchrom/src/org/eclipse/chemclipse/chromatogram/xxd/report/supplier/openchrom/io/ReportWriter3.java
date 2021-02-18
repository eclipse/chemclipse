/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
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
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.ReportSettings3;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class ReportWriter3 {

	private static final String DELIMITER = "\t";
	//
	private DecimalFormat decimalFormatRetentionTime = ValueFormat.getDecimalFormatEnglish("0.00");
	private DecimalFormat decimalFormatAreaNormal = ValueFormat.getDecimalFormatEnglish("0.0#E0");
	private DecimalFormat decimalFormatConcentration = ValueFormat.getDecimalFormatEnglish("0.000");
	private DateFormat dateFormat = ValueFormat.getDateFormatEnglish();

	public void generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, ReportSettings3 reportSettings, IProgressMonitor monitor) throws IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		//
		for(IChromatogram<? extends IPeak> chromatogram : chromatograms) {
			printHeader(printWriter, chromatogram);
			printWriter.println("");
			printQuantitationResults(printWriter, chromatogram);
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
		printWriter.println("Additional Info: " + chromatogram.getDetailedInfo() + " " + chromatogram.getMiscInfo()); // Don't change without team feedback.
		printWriter.println("Acquisition Date: " + dateFormat.format(chromatogram.getDate()));
		printWriter.println("Operator: " + chromatogram.getOperator());
		printWriter.println("Miscellaneous: " + chromatogram.getMiscInfo());
	}

	private void printQuantitationResults(PrintWriter printWriter, IChromatogram<? extends IPeak> chromatogram) {

		printWriter.print("#");
		printWriter.print(DELIMITER);
		printWriter.print("Identification");
		printWriter.print(DELIMITER);
		printWriter.print("Substance");
		printWriter.print(DELIMITER);
		printWriter.print("RT");
		printWriter.print(DELIMITER);
		printWriter.print("Area");
		printWriter.print(DELIMITER);
		printWriter.print("Conc.");
		printWriter.print(DELIMITER);
		printWriter.print("Unit");
		printWriter.print(DELIMITER);
		printWriter.print("Description");
		printWriter.println("");
		/*
		 * Data
		 */
		int i = 1;
		for(IPeak peak : chromatogram.getPeaks()) {
			//
			IPeakModel peakModel = peak.getPeakModel();
			float retentionIndex = peakModel.getPeakMaximum().getRetentionIndex();
			IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, retentionIndex);
			ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(peak.getTargets(), identificationTargetComparator);
			String identification = (libraryInformation != null) ? libraryInformation.getName() : "";
			String retentionTime = decimalFormatRetentionTime.format(peakModel.getRetentionTimeAtPeakMaximum() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			//
			for(IQuantitationEntry quantitationEntry : peak.getQuantitationEntries()) {
				printWriter.print("P" + i);
				printWriter.print(DELIMITER);
				printWriter.print(identification);
				printWriter.print(DELIMITER);
				printWriter.print(quantitationEntry.getName());
				printWriter.print(DELIMITER);
				printWriter.print(retentionTime);
				printWriter.print(DELIMITER);
				printWriter.print(decimalFormatAreaNormal.format(quantitationEntry.getArea()));
				printWriter.print(DELIMITER);
				printWriter.print(decimalFormatConcentration.format(quantitationEntry.getConcentration()));
				printWriter.print(DELIMITER);
				printWriter.print(quantitationEntry.getConcentrationUnit());
				printWriter.print(DELIMITER);
				printWriter.print(quantitationEntry.getDescription());
				printWriter.println("");
			}
			i++;
		}
	}
}
