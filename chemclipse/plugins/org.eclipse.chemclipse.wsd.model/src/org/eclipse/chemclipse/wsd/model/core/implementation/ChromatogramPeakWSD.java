/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.implementation;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.wsd.model.core.AbstractChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakModelWSD;

public class ChromatogramPeakWSD extends AbstractChromatogramPeakWSD implements IChromatogramPeakWSD {

	public ChromatogramPeakWSD(IPeakModelWSD peakModel, IChromatogramWSD chromatogram, String modelDescription) throws IllegalArgumentException, PeakException {
		super(peakModel, chromatogram, modelDescription);
	}

	public ChromatogramPeakWSD(IPeakModelWSD peakModel, IChromatogramWSD chromatogram) throws IllegalArgumentException, PeakException {
		super(peakModel, chromatogram);
	}
}
