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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.core.runtime.IProgressMonitor;

public class SampleQuantWriter_1200 implements ISampleQuantWriter {

	@Override
	public void write(File file, ISampleQuantReport sampleQuantReport, IProgressMonitor monitor) throws Exception {

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
		zipOutputStream.setLevel(9); // Max compression
		zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
		/*
		 * Write the data
		 */
		writeVersion(zipOutputStream, monitor);
		writeData(zipOutputStream, sampleQuantReport, monitor);
		/*
		 * Flush and close the output stream.
		 */
		zipOutputStream.flush();
		zipOutputStream.close();
	}

	private void writeVersion(ZipOutputStream zipOutputStream, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Version
		 */
		zipEntry = new ZipEntry("VERSION");
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		writeString(dataOutputStream, ISampleQuantWriter.VERSION_1200);
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeData(ZipOutputStream zipOutputStream, ISampleQuantReport sampleQuantReport, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Version
		 */
		zipEntry = new ZipEntry("DATA");
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		//
		writeString(dataOutputStream, sampleQuantReport.getPathChromatogramOriginal());
		writeString(dataOutputStream, sampleQuantReport.getPathChromatogramEdited());
		//
		writeString(dataOutputStream, sampleQuantReport.getName());
		writeString(dataOutputStream, sampleQuantReport.getDataName());
		writeString(dataOutputStream, sampleQuantReport.getDate());
		writeString(dataOutputStream, sampleQuantReport.getOperator());
		writeString(dataOutputStream, sampleQuantReport.getMiscInfo());
		//
		List<ISampleQuantSubstance> sampleQuantSubstances = sampleQuantReport.getSampleQuantSubstances();
		dataOutputStream.writeInt(sampleQuantSubstances.size());
		for(ISampleQuantSubstance sampleQuantSubstance : sampleQuantSubstances) {
			dataOutputStream.writeInt(sampleQuantSubstance.getId());
			writeString(dataOutputStream, sampleQuantSubstance.getCasNumber());
			writeString(dataOutputStream, sampleQuantSubstance.getName());
			dataOutputStream.writeInt(sampleQuantSubstance.getMaxScan());
			dataOutputStream.writeDouble(sampleQuantSubstance.getConcentration());
			writeString(dataOutputStream, sampleQuantSubstance.getUnit());
			writeString(dataOutputStream, sampleQuantSubstance.getMisc());
			dataOutputStream.writeDouble(sampleQuantSubstance.getMinMatchQuality());
			dataOutputStream.writeDouble(sampleQuantSubstance.getMatchQuality());
			dataOutputStream.writeBoolean(sampleQuantSubstance.isValidated());
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		dataOutputStream.writeInt(value.length()); // Value Length
		dataOutputStream.writeChars(value); // Value
	}
}
