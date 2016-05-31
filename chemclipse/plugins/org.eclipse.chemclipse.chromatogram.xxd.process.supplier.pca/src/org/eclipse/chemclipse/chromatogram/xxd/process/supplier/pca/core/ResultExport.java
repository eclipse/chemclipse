/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class ResultExport {

	private static final String TAB = "\t";
	private DecimalFormat decimalFormat;

	public ResultExport() {
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
	}

	public void exportToTextFile(File file, IPcaResults pcaResults) throws FileNotFoundException {

		PrintWriter printWriter = new PrintWriter(file);
		if(pcaResults != null) {
			Set<Map.Entry<ISample, IPcaResult>> entrySet = pcaResults.getPcaResultMap().entrySet();
			/*
			 * Header
			 */
			printWriter.println("-------------------------------------");
			printWriter.println("Settings");
			printWriter.println("-------------------------------------");
			printWriter.print("Number of principle components:");
			printWriter.print(TAB);
			printWriter.println(pcaResults.getNumberOfPrincipleComponents());
			printWriter.print("Retention time window:");
			printWriter.print(TAB);
			printWriter.println(pcaResults.getRetentionTimeWindow());
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Input Files");
			printWriter.println("-------------------------------------");
			for(IDataInputEntry entry : pcaResults.getDataInputEntries()) {
				printWriter.print(entry.getName());
				printWriter.print(TAB);
				printWriter.println(entry.getInputFile());
			}
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Extracted Retention Times (Minutes)");
			printWriter.println("-------------------------------------");
			for(int retentionTime : pcaResults.getExtractedRetentionTimes()) {
				printWriter.println(decimalFormat.format(retentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			}
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Peak Intensity Table");
			printWriter.println("-------------------------------------");
			printWriter.print("Filename");
			printWriter.print(TAB);
			for(int retentionTime : pcaResults.getExtractedRetentionTimes()) {
				printWriter.print(decimalFormat.format(retentionTime / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
				printWriter.print(TAB);
			}
			printWriter.println("");
			/*
			 * Data
			 */
			for(Map.Entry<ISample, IPcaResult> entry : entrySet) {
				printWriter.print(entry.getKey().getName());
				printWriter.print(TAB);
				IPcaResult pcaResult = entry.getValue();
				double[] sampleData = pcaResult.getSampleData();
				for(double data : sampleData) {
					printWriter.print(decimalFormat.format(data));
					printWriter.print(TAB);
				}
				printWriter.println("");
			}
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Principle Components");
			printWriter.println("-------------------------------------");
			//
			printWriter.print("File");
			printWriter.print(TAB);
			for(int i = 1; i <= pcaResults.getNumberOfPrincipleComponents(); i++) {
				printWriter.print("PC" + i);
				printWriter.print(TAB);
			}
			printWriter.println("");
			//
			for(Map.Entry<ISample, IPcaResult> entry : entrySet) {
				/*
				 * Print the PCs
				 */
				String name = entry.getKey().getName();
				IPcaResult pcaResult = entry.getValue();
				double[] eigenSpace = pcaResult.getEigenSpace();
				printWriter.print(name);
				printWriter.print(TAB);
				for(double value : eigenSpace) {
					printWriter.print(decimalFormat.format(value));
					printWriter.print(TAB);
				}
				printWriter.println("");
			}
		} else {
			/*
			 * No results available.
			 */
			printWriter.println("There are no results available yet.");
		}
		//
		printWriter.flush();
		printWriter.close();
	}
}
