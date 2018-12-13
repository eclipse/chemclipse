/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.chromatogram;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverter;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramConverterWSD extends AbstractChromatogramConverter<IChromatogramWSD> implements IChromatogramConverter<IChromatogramWSD> {

	private static IChromatogramConverter<IChromatogramWSD> instance = null;

	public ChromatogramConverterWSD() {
		super("org.eclipse.chemclipse.wsd.converter.chromatogramSupplier", IChromatogramWSD.class);
	}

	public static IChromatogramConverter<IChromatogramWSD> getInstance() {

		if(instance == null) {
			instance = new ChromatogramConverterWSD();
		}
		//
		return instance;
	}

	@Override
	public void postProcessChromatogram(IProcessingInfo processingInfo, IProgressMonitor monitor) {

	}
}
