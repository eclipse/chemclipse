/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.processing.chromatogram;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class ChromatogramCSDImportConverterProcessingInfo extends AbstractProcessingInfo implements IChromatogramCSDImportConverterProcessingInfo {

	@Override
	public IChromatogramCSD getChromatogram() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IChromatogramCSD) {
			return (IChromatogramCSD)object;
		} else {
			Class<?> actualClass = (object == null) ? new Exception("NULL").getClass() : object.getClass();
			throw createTypeCastException("Chromatogram Import Converter", actualClass, IChromatogramCSD.class);
		}
	}

	@Override
	public void setChromatogram(IChromatogramCSD chromatogram) {

		setProcessingResult(chromatogram);
	}
}
