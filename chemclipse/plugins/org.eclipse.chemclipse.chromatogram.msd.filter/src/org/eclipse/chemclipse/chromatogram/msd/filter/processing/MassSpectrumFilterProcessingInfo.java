/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.processing;

import org.eclipse.chemclipse.chromatogram.msd.filter.result.IMassSpectrumFilterResult;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class MassSpectrumFilterProcessingInfo extends AbstractProcessingInfo implements IMassSpectrumFilterProcessingInfo {

	@Override
	public IMassSpectrumFilterResult getMassSpectrumFilterResult() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IMassSpectrumFilterResult) {
			return (IMassSpectrumFilterResult)object;
		} else {
			Class<?> actualClass = (object == null) ? new Exception("NULL").getClass() : object.getClass();
			throw createTypeCastException("Mass Spectrum Filter", actualClass, IMassSpectrumFilterResult.class);
		}
	}

	@Override
	public void setMassSpectrumFilterResult(IMassSpectrumFilterResult massSpectrumFilterResult) {

		setProcessingResult(massSpectrumFilterResult);
	}
}
