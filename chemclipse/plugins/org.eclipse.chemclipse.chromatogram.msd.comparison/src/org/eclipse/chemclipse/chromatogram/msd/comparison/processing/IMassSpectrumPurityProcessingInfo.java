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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.processing;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity.IMassSpectrumPurityResult;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IMassSpectrumPurityProcessingInfo extends IProcessingInfo {

	/**
	 * Returns the comparison result.
	 * 
	 * @return {@link IMassSpectrumPurityResult}
	 * @throws TypeCastException
	 */
	IMassSpectrumPurityResult getMassSpectrumPurityResult() throws TypeCastException;

	/**
	 * Sets the comparison result.
	 * 
	 * @param massSpectrumComparisonResult
	 */
	void setMassSpectrumPurityResult(IMassSpectrumPurityResult massSpectrumPurityResult);
}
