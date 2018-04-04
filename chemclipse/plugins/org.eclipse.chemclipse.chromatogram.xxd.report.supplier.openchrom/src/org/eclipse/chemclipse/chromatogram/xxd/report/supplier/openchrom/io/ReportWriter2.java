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
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.IReportSettings;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class ReportWriter2 {

	private static final String DELIMITER = "\t";
	//
	private DecimalFormat decimalFormat;
	private DateFormat dateFormat;

	public ReportWriter2() {
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
		dateFormat = ValueFormat.getDateFormatEnglish();
	}

	public void generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IReportSettings chromatogramReportSettings, IProgressMonitor monitor) throws IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		//
		for(IChromatogram<? extends IPeak> chromatogram : chromatograms) {
			printWriter.println("Filename: " + chromatogram.getName());
			printWriter.println("Sample Name: " + chromatogram.getDataName());
			printWriter.println("Additional Info: " + chromatogram.getDetailedInfo());
			printWriter.println("Acquisition Date: " + dateFormat.format(chromatogram.getDate()));
			printWriter.println("Operator: " + chromatogram.getOperator());
			printWriter.println("Miscellaneous: " + chromatogram.getMiscInfo());
			printWriter.println("");
			for(IPeak peak : chromatogram.getPeaks()) {
				List<IPeakTarget> peakTargets = peak.getTargets();
				if(peakTargets != null && peakTargets.size() > 0) {
					printWriter.print(peakTargets.get(0).getLibraryInformation().getName());
					printWriter.print(DELIMITER);
					printWriter.print(decimalFormat.format(peak.getPeakModel().getRetentionTimeAtPeakMaximum() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
					printWriter.println("");
				}
			}
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
}
