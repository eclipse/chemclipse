/*******************************************************************************
 * Copyright (c) 2012, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.chromatogram;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverter;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramConverterCSD extends AbstractChromatogramConverter<IChromatogramPeakCSD, IChromatogramCSD> implements IChromatogramConverter<IChromatogramPeakCSD, IChromatogramCSD> {

	private static IChromatogramConverter<IChromatogramPeakCSD, IChromatogramCSD> instance = null;

	public ChromatogramConverterCSD() {

		super("org.eclipse.chemclipse.csd.converter.chromatogramSupplier", IChromatogramCSD.class);
	}

	public static IChromatogramConverter<IChromatogramPeakCSD, IChromatogramCSD> getInstance() {

		if(instance == null) {
			instance = new ChromatogramConverterCSD();
		}
		//
		return instance;
	}

	@Override
	public void postProcessChromatogram(IProcessingInfo<IChromatogramCSD> processingInfo, IProgressMonitor monitor) {

	}
}
