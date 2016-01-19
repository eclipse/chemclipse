/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.processing.massspectrum;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class MassSpectrumImportConverterProcessingInfo extends AbstractProcessingInfo implements IMassSpectrumImportConverterProcessingInfo {

	@Override
	public IMassSpectra getMassSpectra() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IMassSpectra) {
			return (IMassSpectra)object;
		} else {
			throw createTypeCastException("MassSpectra Import Converter", object.getClass(), IMassSpectra.class);
		}
	}

	@Override
	public void setMassSpectra(IMassSpectra massSpectra) {

		setProcessingResult(massSpectra);
	}
}
