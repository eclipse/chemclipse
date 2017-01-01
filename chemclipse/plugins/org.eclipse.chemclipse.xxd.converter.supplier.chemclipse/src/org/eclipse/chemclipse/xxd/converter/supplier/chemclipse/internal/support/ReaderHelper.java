/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReaderHelper {

	public String getVersion(File file) throws IOException {

		DataInputStream dataInputStream;
		FileInputStream fileInputStream = new FileInputStream(file);
		ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
		//
		dataInputStream = getDataInputStream(zipInputStream, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		zipInputStream.close();
		//
		return version;
	}

	public ZipInputStream getZipInputStream(File file) throws IOException {

		FileInputStream fileInputStream = new FileInputStream(file);
		ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
		return zipInputStream;
	}

	public DataInputStream getDataInputStream(ZipInputStream zipInputStream, String entryName) throws IOException {

		ZipEntry zipEntry;
		while((zipEntry = zipInputStream.getNextEntry()) != null) {
			/*
			 * Check each file.
			 */
			if(!zipEntry.isDirectory()) {
				String name = zipEntry.getName();
				if(name.equals(entryName)) {
					return new DataInputStream(zipInputStream);
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
