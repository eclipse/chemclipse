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
package org.eclipse.chemclipse.converter.processing.chromatogram;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class ChromatogramOverviewImportConverterProcessingInfo extends AbstractProcessingInfo implements IChromatogramOverviewImportConverterProcessingInfo {

	@Override
	public IChromatogramOverview getChromatogramOverview() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IChromatogramOverview) {
			return (IChromatogramOverview)object;
		} else {
			throw createTypeCastException("ChromatogramOverview Import Converter", IChromatogramOverview.class);
		}
	}

	@Override
	public void setChromatogramOverview(IChromatogramOverview chromatogramOverview) {

		setProcessingResult(chromatogramOverview);
	}
}
