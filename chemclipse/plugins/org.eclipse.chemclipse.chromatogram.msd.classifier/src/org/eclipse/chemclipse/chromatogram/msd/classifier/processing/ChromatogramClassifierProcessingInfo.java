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
package org.eclipse.chemclipse.chromatogram.msd.classifier.processing;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class ChromatogramClassifierProcessingInfo extends AbstractProcessingInfo implements IChromatogramClassifierProcessingInfo {

	@Override
	public IChromatogramClassifierResult getChromatogramClassifierResult() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IChromatogramClassifierResult) {
			return (IChromatogramClassifierResult)object;
		} else {
			throw createTypeCastException("Chromatogram Classifier", IChromatogramClassifierResult.class);
		}
	}

	@Override
	public void setChromatogramClassifierResult(IChromatogramClassifierResult chromatogramClassifierResult) {

		setProcessingResult(chromatogramClassifierResult);
	}
}
