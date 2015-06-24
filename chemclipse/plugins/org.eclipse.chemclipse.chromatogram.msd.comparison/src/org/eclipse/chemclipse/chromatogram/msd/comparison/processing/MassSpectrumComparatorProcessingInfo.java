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
package org.eclipse.chemclipse.chromatogram.msd.comparison.processing;

import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class MassSpectrumComparatorProcessingInfo extends AbstractProcessingInfo implements IMassSpectrumComparatorProcessingInfo {

	@Override
	public IMassSpectrumComparisonResult getMassSpectrumComparisonResult() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IMassSpectrumComparisonResult) {
			return (IMassSpectrumComparisonResult)object;
		} else {
			throw createTypeCastException("MassSpectrum Comparator", IMassSpectrumComparisonResult.class);
		}
	}

	@Override
	public void setMassSpectrumComparisonResult(IMassSpectrumComparisonResult massSpectrumComparisonResult) {

		setProcessingResult(massSpectrumComparisonResult);
	}
}
