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
package org.eclipse.chemclipse.chromatogram.filter.processing;

import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class ChromatogramFilterProcessingInfo extends AbstractProcessingInfo implements IChromatogramFilterProcessingInfo {

	@Override
	public IChromatogramFilterResult getChromatogramFilterResult() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IChromatogramFilterResult) {
			return (IChromatogramFilterResult)object;
		} else {
			throw createTypeCastException("Chromatogram Filter", object.getClass(), IChromatogramFilterResult.class);
		}
	}

	@Override
	public void setChromatogramFilterResult(IChromatogramFilterResult chromatogramFilterResult) {

		setProcessingResult(chromatogramFilterResult);
	}
}
