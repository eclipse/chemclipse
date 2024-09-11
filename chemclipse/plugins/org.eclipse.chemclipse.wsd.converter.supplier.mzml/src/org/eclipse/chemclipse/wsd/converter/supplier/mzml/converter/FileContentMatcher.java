/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.mzml.converter;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.chemclipse.converter.core.AbstractFileContentMatcher;
import org.eclipse.chemclipse.converter.core.IFileContentMatcher;

public class FileContentMatcher extends AbstractFileContentMatcher implements IFileContentMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		boolean isValidFormat = false;
		try {
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(file));
			boolean hasChromatogramList = false;
			boolean hasRootElement = false;
			boolean hasPDA = false;
			while(xmlStreamReader.hasNext()) {
				int eventType = xmlStreamReader.next();
				if(eventType == XMLStreamConstants.START_ELEMENT) {
					String elementName = xmlStreamReader.getLocalName();
					if(elementName.equals("mzML")) {
						hasRootElement = true;
					} else if(elementName.equals("chromatogramList")) {
						hasChromatogramList = true;
					} else if(elementName.equals("fileContent")) {
						while(xmlStreamReader.hasNext()) {
							int innerEventType = xmlStreamReader.next();
							if(innerEventType == XMLStreamConstants.START_ELEMENT && xmlStreamReader.getLocalName().equals("cvParam")) {
								String accession = xmlStreamReader.getAttributeValue(null, "accession");
								String paramName = xmlStreamReader.getAttributeValue(null, "name");
								if(accession.equals("MS:1000806") && paramName.equals("absorption spectrum")) {
									hasPDA = true;
									break;
								}
							}
							if(innerEventType == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("fileContent")) {
								break;
							}
						}
					}
					if(hasRootElement && hasChromatogramList && hasPDA) {
						isValidFormat = true;
						break;
					}
				}
			}
			xmlStreamReader.close();
		} catch(Exception e) {
			// fail silently
		}
		return isValidFormat;
	}
}
