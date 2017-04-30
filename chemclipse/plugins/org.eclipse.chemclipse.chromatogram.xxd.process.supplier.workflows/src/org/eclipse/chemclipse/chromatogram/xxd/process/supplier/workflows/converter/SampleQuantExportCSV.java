/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class SampleQuantExportCSV {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public void write(File file, ISampleQuantReport sampleQuantReport, IProgressMonitor monitor) throws Exception {

		ICsvListWriter csvListWriter = null;
		try {
			FileWriter fileWriter = new FileWriter(file);
			csvListWriter = new CsvListWriter(fileWriter, CsvPreference.TAB_PREFERENCE);
			//
			csvListWriter.write("Name", sampleQuantReport.getName());
			csvListWriter.write("Data Name", sampleQuantReport.getDataName());
			csvListWriter.write("Date", sampleQuantReport.getDate());
			csvListWriter.write("Operator", sampleQuantReport.getOperator());
			csvListWriter.write("Misc Info", sampleQuantReport.getMiscInfo());
			csvListWriter.write("");
			//
			csvListWriter.writeHeader("ID", "CAS#", "Name", "Max Scan", "Concentration", "Unit", "Misc", "Type", "Min Match Quality", "Match Quality", "Validated");
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
				csvListWriter.write(id, casNumber, name, maxScan, concentration, unit, misc, type, minMatchQuality, matchQuality, validated);
			}
		} finally {
			if(csvListWriter != null) {
				csvListWriter.close();
			}
		}
	}
}
