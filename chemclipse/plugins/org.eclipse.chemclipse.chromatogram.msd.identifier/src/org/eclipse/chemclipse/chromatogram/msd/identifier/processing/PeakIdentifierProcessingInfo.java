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
package org.eclipse.chemclipse.chromatogram.msd.identifier.processing;

import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class PeakIdentifierProcessingInfo extends AbstractProcessingInfo implements IPeakIdentifierProcessingInfo {

	@Override
	public IPeakIdentificationResults getPeakIdentificationResults() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IPeakIdentificationResults) {
			return (IPeakIdentificationResults)object;
		} else {
			throw createTypeCastException("Peak Identifier", IPeakIdentificationResults.class);
		}
	}

	@Override
	public void setPeakIdentificationResults(IPeakIdentificationResults peakIdentificationResults) {

		setProcessingResult(peakIdentificationResults);
	}
}
