/*******************************************************************************
 * Copyright (c) 2012, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class MassSpectrumComparatorProcessingInfo extends AbstractProcessingInfo implements IMassSpectrumComparatorProcessingInfo {

	@Override
	public IMassSpectrumComparisonResult getMassSpectrumComparisonResult() throws TypeCastException {

		Object object = getProcessingResult();
		if(object == null) {
			throw new NullPointerException();
		}
		if(object instanceof IMassSpectrumComparisonResult) {
			return (IMassSpectrumComparisonResult)object;
		} else {
			throw createTypeCastException("MassSpectrum Comparator", object.getClass(), IMassSpectrumComparisonResult.class);
		}
	}

	@Override
	public void setMassSpectrumComparisonResult(IMassSpectrumComparisonResult massSpectrumComparisonResult) {

		setProcessingResult(massSpectrumComparisonResult);
	}
}
