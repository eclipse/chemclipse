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

import org.eclipse.chemclipse.model.identifier.IChromatogramIdentificationResult;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class ChromatogramIdentifierProcessingInfo extends AbstractProcessingInfo implements IChromatogramIdentifierProcessingInfo {

	@Override
	public IChromatogramIdentificationResult getChromatogramIdentificationResult() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IChromatogramIdentificationResult) {
			return (IChromatogramIdentificationResult)object;
		} else {
			throw createTypeCastException("Chromatogram Identifier", IChromatogramIdentificationResult.class);
		}
	}

	@Override
	public void setChromatogramIdentificationResult(IChromatogramIdentificationResult chromatogramIdentificationResult) {

		setProcessingResult(chromatogramIdentificationResult);
	}
}
