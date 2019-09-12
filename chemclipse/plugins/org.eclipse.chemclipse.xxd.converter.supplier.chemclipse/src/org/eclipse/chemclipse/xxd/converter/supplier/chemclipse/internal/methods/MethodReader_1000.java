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
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class MethodReader_1000 implements IMethodReader {

	@Override
	public IProcessMethod convert(File file, IProgressMonitor monitor) throws IOException {

		IProcessMethod processMethod = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				processMethod = readData(zipFile);
			}
		} finally {
			zipFile.close();
		}
		//
		return processMethod;
	}

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		if(version.equals(IFormat.METHOD_VERSION_0001)) {
			isValid = true;
		}
		dataInputStream.close();
		return isValid;
	}

	private IProcessMethod readData(ZipFile zipFile) throws IOException {

		ProcessMethod processMethod = new ProcessMethod();
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_PROCESS_METHOD);
		//
		processMethod.setOperator(readString(dataInputStream));
		processMethod.setDescription(readString(dataInputStream));
		int processEntries = dataInputStream.readInt();
		for(int i = 0; i < processEntries; i++) {
			IProcessEntry processEntry = new ProcessEntry();
			processEntry.setProcessorId(readString(dataInputStream));
			processEntry.setName(readString(dataInputStream));
			processEntry.setDescription(readString(dataInputStream));
			processEntry.setJsonSettings(readString(dataInputStream));
			//
			int dataTypes = dataInputStream.readInt();
			for(int j = 0; j < dataTypes; j++) {
				DataType dataType = DataType.valueOf(readString(dataInputStream));
				processEntry.getSupportedDataTypes().add(dataType);
			}
			// read but ignore for backward compat
			readString(dataInputStream);
			readString(dataInputStream);
			processMethod.add(processEntry);
		}
		//
		dataInputStream.close();
		return processMethod;
	}

	public DataInputStream getDataInputStream(ZipFile zipFile, String entryName) throws IOException {

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

	public String readString(DataInputStream dataInputStream) throws IOException {

		int length = dataInputStream.readInt();
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i <= length; i++) {
			builder.append(String.valueOf(dataInputStream.readChar()));
		}
		return builder.toString();
	}
}
