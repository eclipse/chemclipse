/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - imlementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.processing.chromatogram;

import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;

public class ChromatogramWSDImportConverterProcessingInfo extends AbstractProcessingInfo implements IChromatogramWSDImportConverterProcessingInfo {

	@Override
	public IChromatogramWSD getChromatogram() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IChromatogramWSD) {
			return (IChromatogramWSD)object;
		} else {
			throw createTypeCastException("Chromatogram Import Converter", object.getClass(), IChromatogramWSD.class);
		}
	}

	@Override
	public void setChromatogram(IChromatogramWSD chromatogram) {

		setProcessingResult(chromatogram);
	}
}
