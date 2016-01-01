/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.processing;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class PeakIntegratorProcessingInfo extends AbstractProcessingInfo implements IPeakIntegratorProcessingInfo {

	@Override
	public IPeakIntegrationResults getPeakIntegrationResults() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IPeakIntegrationResults) {
			return (IPeakIntegrationResults)object;
		} else {
			throw createTypeCastException("Peak Integrator", IPeakIntegrationResults.class);
		}
	}

	@Override
	public void setPeakIntegrationResults(IPeakIntegrationResults peakIntegrationResults) {

		setProcessingResult(peakIntegrationResults);
	}
}
