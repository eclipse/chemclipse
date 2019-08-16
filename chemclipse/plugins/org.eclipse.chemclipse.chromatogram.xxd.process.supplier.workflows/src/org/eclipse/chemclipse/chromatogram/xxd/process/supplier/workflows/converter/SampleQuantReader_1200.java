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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.SampleQuantReport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.SampleQuantSubstance;
import org.eclipse.core.runtime.IProgressMonitor;

public class SampleQuantReader_1200 implements ISampleQuantReader {

	@Override
	public ISampleQuantReport read(File file, IProgressMonitor monitor) throws Exception {

		ISampleQuantReport sampleQuantReport = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				monitor.subTask("Import Sample Quant Report");
				sampleQuantReport = readFromZipFile(zipFile, monitor);
			}
		} finally {
			zipFile.close();
		}
		//
		return sampleQuantReport;
	}

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream;
		//
		dataInputStream = getDataInputStream(zipFile, "VERSION");
		String version = readString(dataInputStream);
		if(version.equals(ISampleQuantWriter.VERSION_1200)) {
			isValid = true;
		}
		//
		dataInputStream.close();
		return isValid;
	}

	private ISampleQuantReport readFromZipFile(ZipFile zipFile, IProgressMonitor monitor) throws IOException {

		DataInputStream dataInputStream = getDataInputStream(zipFile, "DATA");
		ISampleQuantReport sampleQuantReport = new SampleQuantReport();
		//
		sampleQuantReport.setPathChromatogramOriginal(readString(dataInputStream));
		sampleQuantReport.setPathChromatogramEdited(readString(dataInputStream));
		//
		sampleQuantReport.setName(readString(dataInputStream));
		sampleQuantReport.setDataName(readString(dataInputStream));
		sampleQuantReport.setDate(readString(dataInputStream));
		sampleQuantReport.setOperator(readString(dataInputStream));
		sampleQuantReport.setMiscInfo(readString(dataInputStream));
		/*
		 * sample quant substances
		 */
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			ISampleQuantSubstance sampleQuantSubstance = new SampleQuantSubstance();
			//
			sampleQuantSubstance.setId(dataInputStream.readInt());
			sampleQuantSubstance.setCasNumber(readString(dataInputStream));
			sampleQuantSubstance.setName(readString(dataInputStream));
			sampleQuantSubstance.setMaxScan(dataInputStream.readInt());
			sampleQuantSubstance.setConcentration(dataInputStream.readDouble());
			sampleQuantSubstance.setUnit(readString(dataInputStream));
			sampleQuantSubstance.setMisc(readString(dataInputStream));
			sampleQuantSubstance.setMinMatchQuality(dataInputStream.readDouble());
			sampleQuantSubstance.setMatchQuality(dataInputStream.readDouble());
			sampleQuantSubstance.setValidated(dataInputStream.readBoolean());
			//
			sampleQuantReport.getSampleQuantSubstances().add(sampleQuantSubstance);
		}
		//
		dataInputStream.close();
		return sampleQuantReport;
	}

	private DataInputStream getDataInputStream(ZipFile zipFile, String entryName) throws IOException {

		Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
		while(zipEntries.hasMoreElements()) {
			/*
			 * Check each file.
			 */
			ZipEntry zipEntry = zipEntries.nextElement();
			if(!zipEntry.isDirectory()) {
				String name = zipEntry.getName();
				if(name.equals(entryName)) {
					return new DataInputStream(new BufferedInputStream(zipFile.getInputStream(zipEntry)));
				}
			}
		}
		throw new IOException("There could be found no entry given with the name: " + entryName);
	}

	private String readString(DataInputStream dataInputStream) throws IOException {

		int length = dataInputStream.readInt();
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i <= length; i++) {
			builder.append(String.valueOf(dataInputStream.readChar()));
		}
		return builder.toString();
	}
}
