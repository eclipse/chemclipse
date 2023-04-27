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
 * Matthias Mailänder - auto detection for chromatography files
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.converter;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter.SpecificationValidator;

public class ChromatogramMagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		boolean isValidFormat = false;
		try {
			file = SpecificationValidator.validateSpecification(file);
			if(!file.exists()) {
				return isValidFormat;
			}
			if(!checkFileExtension(file, ".mzML")) {
				return isValidFormat;
			}
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(file));
			boolean hasChromatogramList = false;
			boolean hasRootElement = false;
			while(xmlStreamReader.hasNext()) {
				int eventType = xmlStreamReader.next();
				if(eventType == XMLStreamConstants.START_ELEMENT) {
					String elementName = xmlStreamReader.getLocalName();
					if(elementName.equals("mzML")) {
						hasRootElement = true;
					} else if(elementName.equals("chromatogramList")) {
						hasChromatogramList = true;
					}
					if(hasRootElement && hasChromatogramList) {
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
