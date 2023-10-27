/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.chemclipse.pcr.converter.supplier.rdml.model.IPCRReader;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.xxd.converter.supplier.io.exception.UnknownVersionException;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;

public class PCRReader {

	public IPlate read(File file) throws IOException, SAXException, JAXBException, ParserConfigurationException, InvalidParameterException {

		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			while(zipEntries.hasMoreElements()) {
				ZipEntry zipEntry = zipEntries.nextElement();
				if(!zipEntry.isDirectory()) {
					if(zipEntry.getName().equals("rdml_data.xml")) {
						IPCRReader reader = chooseReader(zipFile.getInputStream(zipEntry));
						return reader.readData(zipFile.getInputStream(zipEntry));
					}
				}
			}
		}
		return null;
	}

	private IPCRReader chooseReader(InputStream inputStream) throws IOException {

		IPCRReader pcrReader = null;
		//
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			final String header = bufferedReader.readLine();
			if(header.contains("1.3")) {
				pcrReader = new PCRReaderVersion13();
			} else if(header.contains("1.2")) {
				pcrReader = new PCRReaderVersion12();
			} else if(header.contains("1.1")) {
				pcrReader = new PCRReaderVersion11();
			} else {
				throw new UnknownVersionException();
			}
		}
		return pcrReader;
	}
}