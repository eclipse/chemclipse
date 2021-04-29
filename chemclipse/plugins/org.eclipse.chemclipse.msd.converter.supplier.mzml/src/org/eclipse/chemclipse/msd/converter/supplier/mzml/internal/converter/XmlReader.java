/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.MzML;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlReader {

	public static MzML getMzML(File file, String contextPath) throws SAXException, IOException, JAXBException, ParserConfigurationException {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		NodeList topNode = document.getElementsByTagName(IConstants.NODE_MZML);
		//
		JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (MzML)unmarshaller.unmarshal(topNode.item(0));
	}

	public static int getTimeMultiplicator(CVParamType cvParam) {

		int multiplicator = 1;
		if(cvParam.getUnitAccession().equals("UO:0000028")) {
			if(cvParam.getUnitName().equals("millisecond")) {
				multiplicator = 1;
			}
		}
		if(cvParam.getUnitAccession().equals("UO:0000010")) {
			if(cvParam.getUnitName().equals("second")) {
				multiplicator = 1000;
			}
		}
		if(cvParam.getUnitAccession().equals("UO:0000031")) {
			if(cvParam.getUnitName().equals("minute")) {
				multiplicator = 60 * 1000;
			}
		}
		return multiplicator;
	}
}
