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
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.converter;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.chemclipse.converter.core.AbstractFileContentMatcher;
import org.eclipse.chemclipse.converter.core.IFileContentMatcher;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.io.ReaderVersion105;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MassSpectrumFileContentMatcher extends AbstractFileContentMatcher implements IFileContentMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		boolean isValidFormat = false;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			NodeList root = document.getElementsByTagName(ReaderVersion105.NODE_MZ_DATA);
			if(root.getLength() != 1) {
				return isValidFormat;
			}
			NodeList spectrumList = document.getElementsByTagName(ReaderVersion105.NODE_SPECTRUM_LIST);
			if(spectrumList.getLength() > 0) {
				Element element = (Element)spectrumList.item(0);
				int spectrumCount = Integer.parseInt(element.getAttribute("count"));
				if(spectrumCount == 1) {
					isValidFormat = true;
				}
			}
		} catch(Exception e) {
			// Print no exception.
		}
		return isValidFormat;
	}
}
