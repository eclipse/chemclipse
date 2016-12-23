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
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.processing;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity.IMassSpectrumPurityResult;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class MassSpectrumPurityProcessingInfo extends AbstractProcessingInfo implements IMassSpectrumPurityProcessingInfo {

	@Override
	public IMassSpectrumPurityResult getMassSpectrumPurityResult() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IMassSpectrumPurityResult) {
			return (IMassSpectrumPurityResult)object;
		} else {
			throw createTypeCastException("MassSpectrum Purity", object.getClass(), IMassSpectrumPurityResult.class);
		}
	}

	@Override
	public void setMassSpectrumPurityResult(IMassSpectrumPurityResult massSpectrumPurityResult) {

		setProcessingResult(massSpectrumPurityResult);
	}
}
