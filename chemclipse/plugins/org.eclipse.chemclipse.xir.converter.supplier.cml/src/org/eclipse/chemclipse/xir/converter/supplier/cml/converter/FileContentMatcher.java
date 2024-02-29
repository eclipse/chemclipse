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
package org.eclipse.chemclipse.xir.converter.supplier.cml.converter;

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
			while(xmlStreamReader.hasNext()) {
				int eventType = xmlStreamReader.next();
				if(eventType == XMLStreamConstants.START_ELEMENT) {
					if(xmlStreamReader.getLocalName().equals("spectrum")) {
						String type = xmlStreamReader.getAttributeValue(null, "type");
						if(type != null && (type.equals("infrared") || type.equals("IR"))) {
							isValidFormat = true;
						}
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
