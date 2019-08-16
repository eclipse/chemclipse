/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.converter;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class SampleQuantExportCSV {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public void write(File file, ISampleQuantReport sampleQuantReport, IProgressMonitor monitor) throws Exception {

		CSVPrinter csvFilePrinter = null;
		try {
			FileWriter fileWriter = new FileWriter(file);
			csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.TDF);
			//
			csvFilePrinter.printRecord("Name", sampleQuantReport.getName());
			csvFilePrinter.printRecord("Data Name", sampleQuantReport.getDataName());
			csvFilePrinter.printRecord("Date", sampleQuantReport.getDate());
			csvFilePrinter.printRecord("Operator", sampleQuantReport.getOperator());
			csvFilePrinter.printRecord("Misc Info", sampleQuantReport.getMiscInfo());
			csvFilePrinter.printRecord("");
			//
			csvFilePrinter.printRecord("ID", "CAS#", "Name", "Max Scan", "Concentration", "Unit", "Misc", "Type", "Min Match Quality", "Match Quality", "Validated");
			for(ISampleQuantSubstance sampleQuantSubstance : sampleQuantReport.getSampleQuantSubstances()) {
				//
				String id = Integer.toString(sampleQuantSubstance.getId());
				String casNumber = sampleQuantSubstance.getCasNumber();
				String name = sampleQuantSubstance.getName();
				String maxScan = Integer.toString(sampleQuantSubstance.getMaxScan());
				String concentration = decimalFormat.format(sampleQuantSubstance.getConcentration());
				String unit = sampleQuantSubstance.getUnit();
				String misc = sampleQuantSubstance.getMisc();
				String type = sampleQuantSubstance.getType();
				String minMatchQuality = decimalFormat.format(sampleQuantSubstance.getMinMatchQuality());
				String matchQuality = decimalFormat.format(sampleQuantSubstance.getMatchQuality());
				String validated = Boolean.toString(sampleQuantSubstance.isValidated());
				//
				csvFilePrinter.printRecord(id, casNumber, name, maxScan, concentration, unit, misc, type, minMatchQuality, matchQuality, validated);
			}
		} finally {
			if(csvFilePrinter != null) {
				csvFilePrinter.close();
			}
		}
	}
}
