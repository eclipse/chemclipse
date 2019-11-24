/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - Adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.chromatogram;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverter;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramConverterWSD extends AbstractChromatogramConverter<IChromatogramPeakWSD, IChromatogramWSD> implements IChromatogramConverter<IChromatogramPeakWSD, IChromatogramWSD> {

	private static IChromatogramConverter<IChromatogramPeakWSD, IChromatogramWSD> instance = null;

	public ChromatogramConverterWSD() {
		super("org.eclipse.chemclipse.wsd.converter.chromatogramSupplier", IChromatogramWSD.class, DataCategory.WSD);
	}

	public static IChromatogramConverter<IChromatogramPeakWSD, IChromatogramWSD> getInstance() {

		if(instance == null) {
			instance = new ChromatogramConverterWSD();
		}
		//
		return instance;
	}

	@Override
	public void postProcessChromatogram(IProcessingInfo<IChromatogramWSD> processingInfo, IProgressMonitor monitor) {

	}
}
