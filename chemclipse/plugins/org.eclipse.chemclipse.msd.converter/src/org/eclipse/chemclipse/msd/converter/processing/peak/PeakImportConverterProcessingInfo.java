/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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
package org.eclipse.chemclipse.msd.converter.processing.peak;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class PeakImportConverterProcessingInfo extends AbstractProcessingInfo implements IPeakImportConverterProcessingInfo {

	@Override
	public IPeaks getPeaks() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IPeaks) {
			return (IPeaks)object;
		} else {
			throw createTypeCastException("Peak Import Converter", object.getClass(), IPeaks.class);
		}
	}

	@Override
	public void setPeaks(IPeaks peaks) {

		setProcessingResult(peaks);
	}
}
