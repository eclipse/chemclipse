/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.MzMLType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class XmlReader110 {

	public static final String VERSION = "1.1.0";

	private XmlReader110() {

	}

	public static MzMLType getMzML(File file) throws SAXException, IOException, JAXBException, ParserConfigurationException {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		NodeList topNode = document.getElementsByTagName("mzML");
		//
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (MzMLType)unmarshaller.unmarshal(topNode.item(0));
	}

	public static float getTimeMultiplicator(CVParamType cvParam) {

		float multiplicator = 1f;
		if(cvParam.getUnitAccession().equals("UO:0000028")) {
			if(cvParam.getUnitName().equals("millisecond")) {
				multiplicator = 1f;
			}
		}
		if(cvParam.getUnitAccession().equals("UO:0000010")) {
			if(cvParam.getUnitName().equals("second")) {
				multiplicator = (float)IChromatogramOverview.SECOND_CORRELATION_FACTOR;
			}
		}
		if(cvParam.getUnitAccession().equals("UO:0000031")) {
			if(cvParam.getUnitName().equals("minute")) {
				multiplicator = (float)IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
			}
		}
		return multiplicator;
	}
}
