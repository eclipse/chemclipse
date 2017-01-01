/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
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
package org.eclipse.chemclipse.chromatogram.filter.processing;

import org.eclipse.chemclipse.chromatogram.filter.result.IPeakFilterResult;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class PeakFilterProcessingInfo extends AbstractProcessingInfo implements IPeakFilterProcessingInfo {

	@Override
	public IPeakFilterResult getPeakFilterResult() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IPeakFilterResult) {
			return (IPeakFilterResult)object;
		} else {
			throw createTypeCastException("Peak Filter", object.getClass(), IPeakFilterResult.class);
		}
	}

	@Override
	public void setPeakFilterResult(IPeakFilterResult peakFilterResult) {

		setProcessingResult(peakFilterResult);
	}
}
