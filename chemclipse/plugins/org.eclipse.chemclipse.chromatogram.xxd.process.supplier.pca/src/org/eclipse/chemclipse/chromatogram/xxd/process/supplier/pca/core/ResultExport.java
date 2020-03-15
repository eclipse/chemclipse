/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
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
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class ResultExport {

	private static final String TAB = "\t";
	private DecimalFormat decimalFormat;

	public ResultExport() {
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
	}

	public void exportToTextFile(File file, IResultsPCA<?, ?> pcaResults, List<IDataInputEntry> dataInputEntries) throws FileNotFoundException {

		PrintWriter printWriter = new PrintWriter(file);
		if(pcaResults != null) {
			/*
			 * Header
			 */
			printWriter.println("-------------------------------------");
			printWriter.println("Settings");
			printWriter.println("-------------------------------------");
			printWriter.print("Number of principal components:");
			printWriter.print(TAB);
			printWriter.println(pcaResults.getPcaSettings().getNumberOfPrincipalComponents());
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Input Files");
			printWriter.println("-------------------------------------");
			for(IDataInputEntry entry : dataInputEntries) {
				printWriter.print(entry.getFileName());
				printWriter.print(TAB);
				printWriter.println(entry.getInputFile());
			}
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Extracted Retention Times (Minutes)");
			printWriter.println("-------------------------------------");
			for(int i = 0; i < pcaResults.getExtractedVariables().size(); i++) {
				String variable = pcaResults.getExtractedVariables().get(i).getValue();
				printWriter.println(variable);
			}
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Input Data Table");
			printWriter.println("-------------------------------------");
			printWriter.print("Filename");
			printWriter.print(TAB);
			for(int i = 0; i < pcaResults.getExtractedVariables().size(); i++) {
				String variable = pcaResults.getExtractedVariables().get(i).getValue();
				printWriter.println(variable);
				printWriter.print(TAB);
			}
			printWriter.println("");
			/*
			 * Data
			 */
			for(IResultPCA pcaResult : pcaResults.getPcaResultList()) {
				String name = pcaResult.getName();
				printWriter.print(name);
				printWriter.print(TAB);
				double[] sampleData = pcaResult.getSampleData();
				for(double data : sampleData) {
					printWriter.print(decimalFormat.format(data));
					printWriter.print(TAB);
				}
				printWriter.println("");
			}
			printWriter.println("");
			printWriter.println("-------------------------------------");
			printWriter.println("Principal Components");
			printWriter.println("-------------------------------------");
			//
			printWriter.print("File");
			printWriter.print(TAB);
			for(int i = 1; i <= pcaResults.getPcaSettings().getNumberOfPrincipalComponents(); i++) {
				printWriter.print("PC" + i);
				printWriter.print(TAB);
			}
			printWriter.println("");
			//
			for(IResultPCA pcaResult : pcaResults.getPcaResultList()) {
				/*
				 * Print the PCs
				 */
				String name = pcaResult.getName();
				double[] eigenSpace = pcaResult.getScoreVector();
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
