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
import org.eclipse.core.runtime.IProgressMonitor;

public class SampleQuantReader implements ISampleQuantReader {

	@Override
	public ISampleQuantReport read(File file, IProgressMonitor monitor) throws Exception {

		ISampleQuantReport sampleQuantReport = null;
		ZipFile zipFile = new ZipFile(file);
		DataInputStream dataInputStream;
		//
		dataInputStream = getDataInputStream(zipFile, "VERSION");
		String version = readString(dataInputStream);
		dataInputStream.close();
		//
		ISampleQuantReader sampleQuantReader = null;
		if(version.equals(ISampleQuantWriter.VERSION_1100)) {
			sampleQuantReader = new SampleQuantReader_1100();
		} else if(version.equals(ISampleQuantWriter.VERSION_1200)) {
			sampleQuantReader = new SampleQuantReader_1200();
		}
		//
		if(sampleQuantReader != null) {
			sampleQuantReport = sampleQuantReader.read(file, monitor);
		}
		//
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
