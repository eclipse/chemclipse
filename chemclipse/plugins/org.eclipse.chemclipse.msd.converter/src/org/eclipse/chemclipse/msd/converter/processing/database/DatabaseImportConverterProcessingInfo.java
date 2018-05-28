/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.processing.database;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class DatabaseImportConverterProcessingInfo extends AbstractProcessingInfo implements IDatabaseImportConverterProcessingInfo {

	@Override
	public IMassSpectra getMassSpectra() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IMassSpectra) {
			return (IMassSpectra)object;
		} else {
			Class<?> actualClass = (object == null) ? new Exception("NULL").getClass() : object.getClass();
			throw createTypeCastException("Database Import Converter", actualClass, IMassSpectra.class);
		}
	}

	@Override
	public void setMassSpectra(IMassSpectra massSpectra) {

		setProcessingResult(massSpectra);
	}
}
