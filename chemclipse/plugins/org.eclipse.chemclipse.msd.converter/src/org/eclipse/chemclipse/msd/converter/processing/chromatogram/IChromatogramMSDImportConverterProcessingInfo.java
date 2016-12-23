/*******************************************************************************
 * Copyright (c) 2012, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.processing.chromatogram;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IChromatogramMSDImportConverterProcessingInfo extends IProcessingInfo {

	/**
	 * Returns an IChromatogram instance.
	 * 
	 * @return IChromatogram
	 * @throws TypeCastException
	 */
	IChromatogramMSD getChromatogram() throws TypeCastException;

	/**
	 * Sets the IChromatogram instance.
	 * 
	 * @param chromatogram
	 */
	void setChromatogram(IChromatogramMSD chromatogram);
}
