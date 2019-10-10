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

import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractMethodReader implements IMethodReader {

	private String version;

	public AbstractMethodReader(String version) {
		this.version = version;
	}

	@Override
	public IProcessMethod convert(File file, IProgressMonitor monitor) throws IOException {

		try (ZipFile zipFile = new ZipFile(file)) {
			if(isValidFileFormat(zipFile)) {
				return readData(zipFile, file);
			}
		}
		return null;
	}

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String streamVersion = readString(dataInputStream);
		if(streamVersion.equals(version)) {
			isValid = true;
		}
		dataInputStream.close();
		return isValid;
	}

	private IProcessMethod readData(ZipFile zipFile, File file) throws IOException {

		try (DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_PROCESS_METHOD)) {
			return deserialize(dataInputStream, file);
		}
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

	protected static String readString(DataInputStream dataInputStream) throws IOException {

		int length = dataInputStream.readInt();
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i <= length; i++) {
			builder.append(String.valueOf(dataInputStream.readChar()));
		}
		return builder.toString();
	}

	protected abstract IProcessMethod deserialize(DataInputStream dataInputStream, File file) throws IOException;
}
