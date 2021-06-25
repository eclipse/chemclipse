/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.converter;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.IConstants;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.IFormat;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.SpecificationValidator;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model.MsRun;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ChromatogramMagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		boolean isValidFormat = false;
		try {
			file = SpecificationValidator.validateSpecification(file);
			if(!file.exists()) {
				return isValidFormat;
			}
			if(!checkFileExtension(file, ".mzXML")) {
				return isValidFormat;
			}
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			NodeList nodeList = document.getElementsByTagName(IConstants.NODE_MS_RUN);
			//
			JAXBContext jaxbContext = JAXBContext.newInstance(IFormat.CONTEXT_PATH_V_320);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			MsRun msRun = (MsRun)unmarshaller.unmarshal(nodeList.item(0));
			if(msRun.getScan().size() > 1)
				isValidFormat = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return isValidFormat;
	}
}
