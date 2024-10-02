/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
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

import java.io.File;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.support.HeaderField;

public class ChromatogramSupport {

	public static void assignIdentifier(IChromatogram<?> chromatogram, HeaderField headerField, String identifier) {

		switch(headerField) {
			case NAME:
				chromatogram.setFile(new File(identifier));
				break;
			case DATA_NAME:
				chromatogram.setDataName(identifier);
				break;
			case MISC_INFO:
				chromatogram.setMiscInfo(identifier);
				break;
			case SAMPLE_GROUP:
				chromatogram.setSampleGroup(identifier);
				break;
			case SAMPLE_NAME:
				chromatogram.setSampleName(identifier);
				break;
			case SHORT_INFO:
				chromatogram.setShortInfo(identifier);
				break;
			case TAGS:
				chromatogram.setTags(identifier);
				break;
			default:
				break;
		}
	}
}