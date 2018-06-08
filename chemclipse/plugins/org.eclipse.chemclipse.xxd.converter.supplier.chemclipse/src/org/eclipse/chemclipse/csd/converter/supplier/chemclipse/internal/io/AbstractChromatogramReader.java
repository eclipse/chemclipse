/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.eclipse.chemclipse.csd.converter.io.AbstractChromatogramCSDReader;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDReader;

public abstract class AbstractChromatogramReader extends AbstractChromatogramCSDReader implements IChromatogramCSDReader {

	/**
	 * Object = ZipFile or ZipInputStream
	 * May return null;
	 * 
	 * @param object
	 * @param entryName
	 * @return {@link DataInputStream}
	 * @throws IOException
	 */
	public DataInputStream getDataInputStream(Object object, String entryName) throws IOException {

		if(object instanceof ZipFile) {
			return getDataInputStream((ZipFile)object, entryName);
		} else if(object instanceof ZipInputStream) {
			return getDataInputStream((ZipInputStream)object, entryName);
		} else {
			return null;
		}
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