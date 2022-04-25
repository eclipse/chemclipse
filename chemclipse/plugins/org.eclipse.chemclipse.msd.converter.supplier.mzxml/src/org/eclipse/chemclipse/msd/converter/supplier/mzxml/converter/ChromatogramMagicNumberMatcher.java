/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.IConstants;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.SpecificationValidator;
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
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			NodeList root = document.getElementsByTagNameNS("*", IConstants.NODE_MZXML);
			if(root.getLength() != 1) {
				return isValidFormat;
			}
			NodeList scanList = document.getElementsByTagName(IConstants.NODE_SCAN);
			if(scanList.getLength() > 1)
				isValidFormat = true;
		} catch(Exception e) {
			// fail silently
		}
		return isValidFormat;
	}
}
