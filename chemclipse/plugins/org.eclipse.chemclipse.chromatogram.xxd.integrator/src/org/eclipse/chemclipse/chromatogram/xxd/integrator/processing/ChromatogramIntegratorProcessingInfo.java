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
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.processing;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class ChromatogramIntegratorProcessingInfo extends AbstractProcessingInfo implements IChromatogramIntegratorProcessingInfo {

	@Override
	public IChromatogramIntegrationResults getChromatogramIntegrationResults() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IChromatogramIntegrationResults) {
			return (IChromatogramIntegrationResults)object;
		} else {
			throw createTypeCastException("Chromatogram Integrator", object.getClass(), IChromatogramIntegrationResults.class);
		}
	}

	@Override
	public void setChromatogramIntegrationResults(IChromatogramIntegrationResults chromatogramIntegrationResults) {

		setProcessingResult(chromatogramIntegrationResults);
	}
}
