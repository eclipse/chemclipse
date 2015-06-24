/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.processing.chromatogram;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class ChromatogramMSDImportConverterProcessingInfo extends AbstractProcessingInfo implements IChromatogramMSDImportConverterProcessingInfo {

	@Override
	public IChromatogramMSD getChromatogram() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IChromatogramMSD) {
			return (IChromatogramMSD)object;
		} else {
			throw createTypeCastException("Chromatogram Import Converter", IChromatogramMSD.class);
		}
	}

	@Override
	public void setChromatogram(IChromatogramMSD chromatogram) {

		setProcessingResult(chromatogram);
	}
}
