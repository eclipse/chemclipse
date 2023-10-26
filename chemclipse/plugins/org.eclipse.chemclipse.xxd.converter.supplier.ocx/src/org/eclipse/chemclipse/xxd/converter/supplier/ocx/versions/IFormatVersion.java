/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.versions;

public interface IFormatVersion {

	String DESCRIPTION_CHROMATOGRAM = "Open Chromatography Binary";
	String FILE_EXTENSION_CHROMATOGRAM = ".ocb";
	String FILE_NAME_CHROMATOGRAM = DESCRIPTION_CHROMATOGRAM.replaceAll("\\s", "") + FILE_EXTENSION_CHROMATOGRAM;
	String FILTER_EXTENSION_CHROMATOGRAM = "*" + FILE_EXTENSION_CHROMATOGRAM;
	String FILTER_NAME_CHROMATOGRAM = DESCRIPTION_CHROMATOGRAM + " (*" + FILE_EXTENSION_CHROMATOGRAM + ")";
	String CONVERTER_ID_CHROMATOGRAM = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	//
	String DESCRIPTION_METHOD = "Open Chromatography Method";
	String FILE_EXTENSION_METHOD = ".ocm";
	String FILE_NAME_METHOD = DESCRIPTION_METHOD.replaceAll("\\s", "") + FILE_EXTENSION_METHOD;
	String FILTER_EXTENSION_METHOD = "*" + FILE_EXTENSION_METHOD;
	String FILTER_NAME_METHOD = DESCRIPTION_METHOD + " (*" + FILE_EXTENSION_METHOD + ")";
	String CONVERTER_ID_METHOD = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.processMethodSupplier";

	String getVersion();

	String getRelease();

	default String getLabel() {

		return getVersion() + " (" + getRelease() + ")";
	}

	static String[][] getOptions(IFormatVersion[] values) {

		String[][] elements = new String[values.length][2];
		//
		int counter = 0;
		for(IFormatVersion value : values) {
			elements[counter][0] = value.getLabel();
			elements[counter][1] = value.getVersion();
			counter++;
		}
		//
		return elements;
	}
}