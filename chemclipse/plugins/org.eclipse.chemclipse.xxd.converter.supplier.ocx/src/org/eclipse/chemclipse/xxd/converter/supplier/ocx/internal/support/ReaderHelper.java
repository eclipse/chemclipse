/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ReaderHelper {

	public String getVersion(File file) throws IOException {

		FileInputStream fileInputStream = new FileInputStream(file);
		ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
		String version = getVersion(zipInputStream, "");
		zipInputStream.close();
		return version;
	}

	public String getVersion(Object object, String directoryPrefix) throws IOException {

		String entryName = directoryPrefix + IFormat.FILE_VERSION;
		String version = "";
		DataInputStream dataInputStream = null;
		if(object instanceof ZipInputStream zipInputStream) {
			dataInputStream = getDataInputStream(zipInputStream, entryName);
		} else if(object instanceof ZipFile zipFile) {
			dataInputStream = getDataInputStream(zipFile, entryName);
		}
		//
		if(dataInputStream != null) {
			version = readString(dataInputStream);
		}
		//
		return version;
	}

	public ZipInputStream getZipInputStream(File file) throws IOException {

		FileInputStream fileInputStream = new FileInputStream(file);
		return new ZipInputStream(new BufferedInputStream(fileInputStream));
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
