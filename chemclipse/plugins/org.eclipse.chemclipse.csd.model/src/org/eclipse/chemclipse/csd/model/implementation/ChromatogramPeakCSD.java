/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.implementation;

import org.eclipse.chemclipse.csd.model.core.AbstractChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakModelCSD;
import org.eclipse.chemclipse.model.exceptions.PeakException;

public class ChromatogramPeakCSD extends AbstractChromatogramPeakCSD implements IChromatogramPeakCSD {

	public ChromatogramPeakCSD(IPeakModelCSD peakModel, IChromatogramCSD chromatogram) throws IllegalArgumentException, PeakException {
		super(peakModel, chromatogram);
	}

	public ChromatogramPeakCSD(IPeakModelCSD peakModel, IChromatogramCSD chromatogram, String modelDescription) throws IllegalArgumentException, PeakException {
		super(peakModel, chromatogram, modelDescription);
	}
}
