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
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IDatabaseImportConverterProcessingInfo extends IProcessingInfo {

	/**
	 * Returns an IMassSpectra instance.
	 * 
	 * @return IMassSpectra
	 * @throws TypeCastException
	 */
	IMassSpectra getMassSpectra() throws TypeCastException;

	/**
	 * Sets the IMassSpectra instance.
	 * 
	 * @param massSpectra
	 */
	void setMassSpectra(IMassSpectra massSpectra);
}
