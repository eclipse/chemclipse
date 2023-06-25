/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - auto detection for MALDI files
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.converter;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.chemclipse.converter.core.AbstractFileContentMatcher;
import org.eclipse.chemclipse.converter.core.IFileContentMatcher;

public class MassSpectrumFileContentMatcher extends AbstractFileContentMatcher implements IFileContentMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		boolean isValidFormat = false;
		try {
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(file));
			boolean hasChromatogramList = false;
			boolean hasRootElement = false;
			boolean hasSpectrumList = false;
			while(xmlStreamReader.hasNext()) {
				int eventType = xmlStreamReader.next();
				if(eventType == XMLStreamConstants.START_ELEMENT) {
					String elementName = xmlStreamReader.getLocalName();
					if(elementName.equals("mzML")) {
						hasRootElement = true;
					} else if(elementName.equals("chromatogramList")) {
						hasChromatogramList = true;
					} else if(elementName.equals("spectrumList")) {
						hasSpectrumList = true;
					}
				}
			}
			if(hasRootElement && hasSpectrumList && !hasChromatogramList) {
				isValidFormat = true;
			}
			xmlStreamReader.close();
		} catch(Exception e) {
			// fail silently
		}
		return isValidFormat;
	}
}
