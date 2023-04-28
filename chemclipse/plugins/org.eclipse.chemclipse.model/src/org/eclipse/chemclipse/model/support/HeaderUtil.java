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
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.support.HeaderField;

public class HeaderUtil {

	public static String getChromatogramName(IChromatogram<?> chromatogram, HeaderField headerField, String defaultName) {

		String chromatogramName = null;
		//
		switch(headerField) {
			case NAME:
				chromatogramName = validate(chromatogram.getName());
				break;
			case DATA_NAME:
				chromatogramName = validate(chromatogram.getDataName());
				break;
			case SHORT_INFO:
				chromatogramName = validate(chromatogram.getShortInfo());
				break;
			case SAMPLE_GROUP:
				chromatogramName = validate(chromatogram.getSampleGroup());
				break;
			case MISC_INFO:
				chromatogramName = validate(chromatogram.getMiscInfo());
				break;
			default:
				// Do nothing, see check default.
				break;
		}
		//
		if(chromatogramName == null || chromatogramName.isEmpty()) {
			return defaultName;
		} else {
			return chromatogramName;
		}
	}

	public static HeaderField getHeaderField(String value) {

		try {
			return HeaderField.valueOf(value);
		} catch(Exception e) {
			return HeaderField.NAME;
		}
	}

	private static String validate(String value) {

		return value == null || value.isEmpty() ? null : value;
	}
}